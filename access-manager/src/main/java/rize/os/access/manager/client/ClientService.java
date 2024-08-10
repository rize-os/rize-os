package rize.os.access.manager.client;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private List<Client> loggedClients(List<Client> clients)
    {
        log.debug("Found {} clients", clients.size());
        if (log.isTraceEnabled()) clients.forEach(client -> log.trace(" - {}", client));
        return clients;
    }
}
