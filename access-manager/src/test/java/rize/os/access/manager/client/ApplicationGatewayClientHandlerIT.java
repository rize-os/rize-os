package rize.os.access.manager.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.access.manager.TestcontainersConfiguration;
import rize.os.access.manager.organization.Organization;
import rize.os.access.manager.organization.events.OrganizationCreatedEvent;
import rize.os.access.manager.organization.events.OrganizationDeletedEvent;
import rize.os.access.manager.organization.events.OrganizationModifiedEvent;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class ApplicationGatewayClientHandlerIT
{
    @Autowired
    private ApplicationGatewayClientHandler applicationGatewayClientHandler;

    @Autowired
    private ClientService clientService;

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @Test
    void shouldCreateApplicationGatewayClientsForCreatedOrganization()
    {
        var organization = Organization.builder()
                .id(UUID.randomUUID().toString())
                .name("test-organization")
                .displayName("Test Organization")
                .alias("test-organization-alias-1")
                .alias("test-organization-alias-2")
                .build();
        var createdEvent = new OrganizationCreatedEvent(organization);

        applicationGatewayClientHandler.handleOrganizationCreatedEvent(createdEvent);

        var expectedClientId1 = "application-gateway-org-test-organization-alias-1";
        var expectedClientId2 = "application-gateway-org-test-organization-alias-2";

        var clientsForOrganization = clientService.findByOrganizationId(organization.getId());
        assertThat(clientsForOrganization).hasSize(2);
        assertThat(clientsForOrganization.stream().filter(client -> client.getClientId().equals(expectedClientId1)).findFirst()).isPresent();
        assertThat(clientsForOrganization.stream().filter(client -> client.getClientId().equals(expectedClientId2)).findFirst()).isPresent();
        var client1 = clientsForOrganization.stream().filter(client -> client.getClientId().equals(expectedClientId1)).findFirst().get();
        var client2 = clientsForOrganization.stream().filter(client -> client.getClientId().equals(expectedClientId2)).findFirst().get();
        assertThat(client1.getRedirectUris()).hasSize(2);
        assertThat(client1.getRedirectUris()).contains("http://localhost:8080/test-organization-alias-1", "http://127.0.0.1:8080/test-organization-alias-1");
        assertThat(client2.getRedirectUris()).hasSize(2);
        assertThat(client2.getRedirectUris()).contains("http://localhost:8080/test-organization-alias-2", "http://127.0.0.1:8080/test-organization-alias-2");

        // cleanup
        clientService.deleteClient(client1.getId());
        clientService.deleteClient(client2.getId());
    }

    @Test
    void shouldUpdateApplicationGatewayClientsForModifiedOrganization()
    {
        var organization = Organization.builder()
                .id(UUID.randomUUID().toString())
                .name("test-organization")
                .displayName("Test Organization")
                .alias("test-organization-alias")
                .build();
        var createdEvent = new OrganizationCreatedEvent(organization);
        applicationGatewayClientHandler.handleOrganizationCreatedEvent(createdEvent);
        assertThat(clientService.findByOrganizationId(organization.getId())).hasSize(1);

        organization.setAliases(List.of("updated-organization-alias"));
        var modifiedEvent = new OrganizationModifiedEvent(organization);
        applicationGatewayClientHandler.handleOrganizationModifiedEvent(modifiedEvent);

        var clients = clientService.findByOrganizationId(organization.getId());
        assertThat(clients).hasSize(1);
        var client = clients.getFirst();
        assertThat(client.getClientId()).isEqualTo("application-gateway-org-updated-organization-alias");
        assertThat(clientService.findByClientId("application-gateway-org-test-organization-alias")).isEmpty(); // old client should be deleted

        // cleanup
        clientService.deleteClient(client.getId());
    }

    @Test
    void shouldDeleteApplicationGatewayClientsForDeletedOrganization()
    {
        var organization = Organization.builder()
                .id(UUID.randomUUID().toString())
                .name("test-organization")
                .displayName("Test Organization")
                .alias("test-organization-alias")
                .build();
        var createdEvent = new OrganizationCreatedEvent(organization);
        applicationGatewayClientHandler.handleOrganizationCreatedEvent(createdEvent);
        assertThat(clientService.findByOrganizationId(organization.getId())).hasSize(1);

        var deletedEvent = new OrganizationDeletedEvent(organization);
        applicationGatewayClientHandler.handleOrganizationDeletedEvent(deletedEvent);

        assertThat(clientService.findByOrganizationId(organization.getId())).isEmpty();
        assertThat(clientService.findByClientId("application-gateway-org-test-organization-alias")).isEmpty();
    }
}