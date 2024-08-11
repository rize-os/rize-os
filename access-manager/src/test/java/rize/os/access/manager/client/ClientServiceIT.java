package rize.os.access.manager.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.access.manager.TestcontainersConfiguration;
import rize.os.access.manager.client.exceptions.ClientConstraintException;
import rize.os.access.manager.client.exceptions.ClientCreateException;
import rize.os.access.manager.client.exceptions.ClientNotFoundException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class ClientServiceIT
{
    @Autowired
    private ClientService clientService;

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry)
    {
        registry.add("keycloak.url", TestcontainersConfiguration.keycloak::getAuthServerUrl);
    }

    @Test
    void shouldFindAllClients()
    {
        var clients = clientService.findAll();

        assertThat(clients).isNotNull();
        assertThat(clients).isNotEmpty();
    }

    @Test
    void shouldFindClientByClientId()
    {
        var clientToCreate = Client.builder()
                .clientId("should-find-client-by-client-id")
                .name("shouldFindClientByClientId")
                .redirectUris(List.of("https://rize-os.dev"))
                .build();
        var createdClient = clientService.createClient(clientToCreate);

        var foundClient = clientService.findByClientId(clientToCreate.getClientId());

        assertThat(foundClient).isNotEmpty();
        assertThat(foundClient.get()).isEqualTo(createdClient);
    }

    @Test
    void shouldFindClientsByOrganizationId()
    {
        var organizationId = UUID.randomUUID().toString();
        var clientToCreate = Client.builder()
                .clientId("should-find-clients-by-organization-id")
                .name("shouldFindClientsByOrganizationId")
                .organizationId(organizationId)
                .redirectUris(List.of("https://rize-os.dev"))
                .build();
        var createdClient = clientService.createClient(clientToCreate);

        var foundClients = clientService.findByOrganizationId(organizationId);

        assertThat(foundClients).isNotNull();
        assertThat(foundClients).hasSize(1);
        assertThat(foundClients.getFirst()).isEqualTo(createdClient);
    }

    @Test
    void shouldNotFindClientsByOrganizationId()
    {
        var foundClients = clientService.findByOrganizationId(UUID.randomUUID().toString());

        assertThat(foundClients).isNotNull();
        assertThat(foundClients).isEmpty();
    }

    @Test
    void shouldFailToCreateClientWithoutClientId()
    {
        var clientToCreate = Client.builder()
                .name("shouldFailToCreateClientWithoutClientId")
                .redirectUris(List.of("https://rize-os.dev"))
                .build();

        assertThatThrownBy(() -> clientService.createClient(clientToCreate))
                .isInstanceOf(ClientConstraintException.class);
    }

    @Test
    void shouldFailToCreateClientWithoutRedirectUris()
    {
        var clientToCreate = Client.builder()
                .clientId("should-fail-to-create-client-without-redirect-uris")
                .name("shouldFailToCreateClientWithoutRedirectUris")
                .build();

        assertThatThrownBy(() -> clientService.createClient(clientToCreate))
                .isInstanceOf(ClientConstraintException.class);
    }

    @Test
    void shouldFailToCreateClientWithSameClientId()
    {
        var clientToCreate1 = Client.builder()
                .clientId("should-fail-to-create-client-with-same-client-id")
                .name("shouldFailToCreateClientWithSameClientId")
                .redirectUris(List.of("https://rize-os.dev"))
                .build();
        clientService.createClient(clientToCreate1);

        var clientToCreate2 = Client.builder()
                .clientId("should-fail-to-create-client-with-same-client-id")
                .name("shouldFailToCreateClientWithSameClientId")
                .redirectUris(List.of("https://rize-os.dev"))
                .build();

        assertThatThrownBy(() -> clientService.createClient(clientToCreate2))
                .isInstanceOf(ClientCreateException.class)
                .hasMessageContaining("Client with client-id \"" + clientToCreate2.getClientId() + "\" already exists");
    }

    @Test
    void shouldDeleteClient()
    {
        var clientToCreate = Client.builder()
                .clientId("should-delete-client")
                .name("shouldDeleteClient")
                .redirectUris(List.of("https://rize-os.dev"))
                .build();
        var createdClient = clientService.createClient(clientToCreate);

        clientService.deleteClient(createdClient.getId());

        var clients = clientService.findAll();

        assertThat(clients).doesNotContain(createdClient);
        assertThat(clients.stream().filter(client -> client.getId().equals(createdClient.getId()))).isEmpty();
    }

    @Test
    void shouldFailToDeleteNonExistingClient()
    {
        var id = UUID.randomUUID().toString();
        assertThatThrownBy(() -> clientService.deleteClient(id))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("Client not found with id: " + id);
    }
}