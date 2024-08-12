package rize.os.access.manager.client;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.springframework.stereotype.Service;
import rize.os.access.manager.client.exceptions.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService
{
    private final ClientMapper clientMapper;
    private final RealmResource realmResource;

    /**
     * Returns all clients from Keycloak.
     * @return List of clients
     */
    @Nonnull
    List<Client> findAll()
    {
        log.debug("Loading all clients from Keycloak");
        var clientRepresentations = realmResource.clients().findAll();
        var clients = clientRepresentations.stream().map(clientMapper::toClient).toList();
        return loggedClients(clients);
    }

    /**
     * Returns the client from Keycloak for the given ID.
     * @param id ID of the client (not client-id)
     * @return Client object if found; empty otherwise
     */
    Optional<Client> findById(@Nonnull String id)
    {
        var clientRepresentation = findRepresentationById(id);
        if (clientRepresentation.isEmpty())
            return Optional.empty();

        var client = clientMapper.toClient(clientRepresentation.get());
        return loggedClient(client);
    }

    /**
     * Returns the client from Keycloak that matches the given client-id.
     * @param clientId Client-id of the client to find
     * @return Client object if found; empty otherwise
     */
    @Nonnull
    Optional<Client> findByClientId(@Nonnull String clientId)
    {
        var clientRepresentation = findRepresentationByClientId(clientId);
        if (clientRepresentation.isEmpty())
            return Optional.empty();

        var client = clientMapper.toClient(clientRepresentation.get());
        return loggedClient(client);
    }

    /**
     * Returns all clients that belong to the organization with the given ID.
     * @param organizationId ID of the organization
     * @return List of clients
     */
    @Nonnull
    List<Client> findByOrganizationId(@Nonnull String organizationId)
    {
        log.debug("Loading clients for organization with ID [{}]", organizationId);
        var clients = realmResource.clients().findAll().stream()
                .filter(client -> client.getAttributes().containsKey(Client.ORGANIZATION_ID_ATTRIBUTE))
                .filter(client -> client.getAttributes().get(Client.ORGANIZATION_ID_ATTRIBUTE).equals(organizationId))
                .map(clientMapper::toClient)
                .toList();

        return loggedClients(clients);
    }

    /**
     * Creates a new client in Keycloak.
     * @param client Object of the client to create
     * @return The created client object
     * @throws ClientConstraintException If the client object has invalid values
     * @throws ClientCreateException If the client could not be created in Keycloak
     */
    @Nonnull
    Client createClient(@Nonnull Client client) throws ClientConstraintException, ClientCreateException
    {
        log.info("Creating client: {}", client);
        validateClient(client);
        var clientRepresentation = createClientRepresentation(client);

        clientRepresentation.setAttributes(clientMapper.getAttributesForClient(client, clientRepresentation.getAttributes()));
        updateClientRepresentation(clientRepresentation);

        var createdClient = clientMapper.toClient(clientRepresentation);
        log.info("Created client successfully: {}", createdClient);
        return createdClient;
    }

    /**
     * Updates the given client in Keycloak.
     * @param client Object of the client to update
     * @return Object of the updated client
     * @throws ClientNotFoundException If the ID of the given client was not found in Keycloak
     * @throws ClientConstraintException If the client object has invalid values
     * @throws ClientUpdateException If the client could not be updated in Keycloak
     */
    @Nonnull
    Client updateClient(@Nonnull Client client) throws ClientNotFoundException, ClientConstraintException, ClientUpdateException
    {
        log.info("Updating client '{}' to: {}", client.getClientId(), client);
        var clientRepresentation = findRepresentationById(client.getId()).orElseThrow(() -> new ClientNotFoundException(client.getId()));

        validateClient(client);
        clientRepresentation.setName(client.getName());
        clientRepresentation.setRedirectUris(client.getRedirectUris());
        updateClientRepresentation(clientRepresentation);

        var updatedClient = findById(client.getId()).orElseThrow(() -> new ClientNotFoundException(client.getId()));
        log.info("Updated client successfully: {}", updatedClient);
        return updatedClient;
    }

    /**
     * Deletes the client in Keycloak that matches the given ID.
     * @param id ID of the client to delete
     * @throws ClientNotFoundException If the client with the given ID is not found
     * @throws ClientDeleteException If the client could not be deleted in Keycloak
     */
    void deleteClient(@Nonnull String id) throws ClientNotFoundException, ClientDeleteException
    {
        var client = findById(id).orElseThrow(() -> new ClientNotFoundException(id));
        log.info("Deleting client: {}", client);

        try
        {
            realmResource.clients().get(id).remove();
            log.info("Deleted client successfully: {}", client);
        }
        catch (Exception e)
        {
            throw new ClientDeleteException(client, e);
        }
    }

    private Optional<ClientRepresentation> findRepresentationById(String id)
    {
        try
        {
            log.debug("Searching for client with ID [{}] in Keycloak", id);
            return Optional.of(realmResource.clients().get(id).toRepresentation());
        }
        catch (Exception e)
        {
            log.debug("Client with ID [{}] not found", id);
            return Optional.empty();
        }
    }

    private Optional<ClientRepresentation> findRepresentationByClientId(String clientId)
    {
        try
        {
            log.debug("Searching for client with client-id '{}' in Keycloak", clientId);
            return realmResource.clients().findByClientId(clientId).stream().findFirst();
        }
        catch (Exception e)
        {
            log.debug("Client with client-id '{}' not found", clientId);
            return Optional.empty();
        }
    }

    private ClientRepresentation createClientRepresentation(Client client) throws ClientCreateException
    {
        log.debug("Create client with client-id '{}' in Keycloak", client.getClientId());
        var clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(client.getClientId());
        clientRepresentation.setName(client.getName());
        clientRepresentation.setRedirectUris(client.getRedirectUris());

        try (var response = realmResource.clients().create(clientRepresentation))
        {
            if (response.getStatus() != 201)
                throw new ClientCreateException(client, response);

            var createdClientRepresentation = realmResource.clients().findByClientId(client.getClientId()).getFirst();
            log.debug("Created client in Keycloak successfully: ID [{}]", createdClientRepresentation.getId());
            return createdClientRepresentation;
        }
        catch (ClientCreateException e) { throw e; }
        catch (Exception e) { throw new ClientCreateException(client, e); }
    }

    private void updateClientRepresentation(final ClientRepresentation clientRepresentation) throws ClientUpdateException
    {
        try
        {
            log.debug("Update client '{}' in Keycloak", clientRepresentation.getClientId());
            realmResource.clients().get(clientRepresentation.getId()).update(clientRepresentation);
            log.debug("Updated client '{}' successfully", clientRepresentation.getClientId());
        }
        catch (Exception e)
        {
            throw new ClientUpdateException(clientMapper.toClient(clientRepresentation), e);
        }
    }

    private void validateClient(Client client) throws ClientConstraintException
    {
        var violations = client.validate();
        if (!violations.isEmpty())
            throw new ClientConstraintException(client, violations);

        var clientWithId = findByClientId(client.getClientId());
        if (clientWithId.isPresent() && !clientWithId.get().getId().equals(client.getId()))
            throw new ClientCreateException(client, "Client with client-id \"" + client.getClientId() + "\" already exists");
    }

    private List<Client> loggedClients(List<Client> clients)
    {
        log.debug("Found {} clients", clients.size());
        if (log.isTraceEnabled()) clients.forEach(client -> log.trace(" - {}", client));
        return clients;
    }

    private Optional<Client> loggedClient(Client client)
    {
        log.debug("Found client: {}", client);
        return Optional.of(client);
    }
}
