package rize.os.access.manager.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import rize.os.access.manager.organization.Organization;
import rize.os.access.manager.organization.events.OrganizationCreatedEvent;
import rize.os.access.manager.organization.events.OrganizationDeletedEvent;
import rize.os.access.manager.organization.events.OrganizationModifiedEvent;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationGatewayClientHandler
{
    private static final String CLIENT_ID_PREFIX = "application-gateway-org-";

    private final ClientService clientService;

    @Value("${access-manager.clients.application-gateway.redirect-uri-pattern}")
    List<String> redirectUriPattern;

    @EventListener
    void handleOrganizationCreatedEvent(OrganizationCreatedEvent event)
    {
        createApplicationGatewayClientsForOrganization(event.organization());
    }

    @EventListener
    void handleOrganizationModifiedEvent(OrganizationModifiedEvent event)
    {
        createApplicationGatewayClientsForOrganization(event.organization());
    }

    @EventListener
    void handleOrganizationDeletedEvent(OrganizationDeletedEvent event)
    {
        deleteApplicationGatewayClientsForOrganization(event.organization());
    }

    private void createApplicationGatewayClientsForOrganization(Organization organization)
    {
        log.info("Update application-gateway clients for organization '{}' for aliases: {}", organization.getDisplayName(), organization.getAliases());
        var existingClientsForOrganization = clientService.findByOrganizationId(organization.getId());

        var clientsToUpdate = existingClientsForOrganization.stream().filter(client -> organization.getAliases().contains(client.getClientId())).toList();
        updateClients(organization, clientsToUpdate);

        var clientsToRemove = existingClientsForOrganization.stream().filter(client -> !organization.getAliases().contains(client.getClientId())).toList();
        deleteClients(clientsToRemove);

        var newAliases = organization.getAliases().stream()
                .filter(alias -> existingClientsForOrganization.stream().noneMatch(client -> client.getClientId().endsWith("-" + alias)))
                .toList();
        createClients(organization, newAliases);
    }

    private void deleteApplicationGatewayClientsForOrganization(Organization organization)
    {
        log.info("Delete application-gateway clients for organization '{}'", organization.getDisplayName());
        var clientsForOrganization = clientService.findByOrganizationId(organization.getId());
        deleteClients(clientsForOrganization);
    }

    private void updateClients(Organization organization, List<Client> clients)
    {
        log.info("Update clients {} due to modifications of organization '{}'", clients.stream().map(Client::getClientId).toList(), organization.getDisplayName());
        clients.forEach(client ->  {
            var alias = getAliasForClient(client);
            var redirectUris = new HashSet<>(client.getRedirectUris());
            redirectUris.addAll(getRedirectUrisByPattern(alias));

            client.setName(getClientNameForAlias(organization, alias));
            client.setRedirectUris(List.copyOf(redirectUris));
        });
    }

    private void deleteClients(List<Client> clients)
    {
        log.info("Delete clients {} due to removed aliases from organization", clients.stream().map(Client::getClientId).toList());
        for (var client : clients)
        {
            try { clientService.deleteClient(client.getId()); }
            catch (Exception e) { log.error("Failed to delete client {} for removed alias from organization", client.getClientId()); }
        }
    }

    private void createClients(Organization organization, List<String> aliases)
    {
        if (aliases.isEmpty())
        {
            log.info("No new aliases for organization '{}' found -> skip creation of new application-gateway clients", organization.getDisplayName());
            return;
        }

        log.info("Create new clients for the aliases {} of the organization '{}'", aliases, organization.getDisplayName());
        aliases.forEach(alias -> createClient(organization, alias));
    }

    private void createClient(Organization organization, String alias)
    {
        var clientToCreate = Client.builder()
                .clientId(getClientIdForAlias(alias))
                .name(getClientNameForAlias(organization, alias))
                .organizationId(organization.getId())
                .redirectUris(getRedirectUrisByPattern(alias))
                .build();

        clientService.createClient(clientToCreate);
    }

    private List<String> getRedirectUrisByPattern(String alias)
    {
        return redirectUriPattern.stream()
                .map(uri -> uri.replace("%{alias}%", alias))
                .toList();
    }

    private String getClientIdForAlias(String alias)
    {
        return CLIENT_ID_PREFIX + alias;
    }

    private String getClientNameForAlias(Organization organization, String alias)
    {
        return "Application-Gateway Client - \"" + organization.getDisplayName() + "\": [" + alias + "]";
    }

    private String getAliasForClient(Client client)
    {
        return client.getClientId().replace(CLIENT_ID_PREFIX, "");
    }
}
