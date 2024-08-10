package rize.os.access.manager.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.access.manager.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

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
}