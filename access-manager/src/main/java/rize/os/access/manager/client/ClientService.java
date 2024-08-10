package rize.os.access.manager.client;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.springframework.stereotype.Service;
import rize.os.access.manager.client.exceptions.ClientConstraintException;
import rize.os.access.manager.client.exceptions.ClientCreateException;
import rize.os.access.manager.client.exceptions.ClientUpdateException;
import rize.os.access.manager.organization.exceptions.OrganizationCreateException;

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
        clientRepresentation.setEnabled(client.isEnabled());

        try (var response = realmResource.clients().create(clientRepresentation))
        {
            if (response.getStatus() != 201)
                throw new ClientCreateException(client, response);

            var createdClientRepresentation = realmResource.clients().findByClientId(client.getClientId()).getFirst();
            log.debug("Created client in Keycloak successfully: ID [{}]", createdClientRepresentation.getId());
            return createdClientRepresentation;
        }
        catch (OrganizationCreateException e) { throw e; }
        catch (Exception e) { throw new ClientCreateException(client, e); }
    }

    private void updateClientRepresentation(final ClientRepresentation clientRepresentation)
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
