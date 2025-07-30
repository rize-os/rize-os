package rize.os.cockpit;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration
{
    private static final String POSTGRES_IMAGE = "postgres:17.5";

    private static final String KEYCLOAK_IMAGE = "keycloak/keycloak:26.3.2";
    private static final String KEYCLOAK_REALM_IMPORT_FILE = "keycloak/administration-realm.json";
    private static final String KEYCLOAK_ADMIN_USERNAME = "admin";
    private static final String KEYCLOAK_ADMIN_PASSWORD = "password";

    static KeycloakContainer keycloak;

    static
    {
        keycloak = new KeycloakContainer(KEYCLOAK_IMAGE)
                .withRealmImportFile(KEYCLOAK_REALM_IMPORT_FILE)
                .withAdminUsername(KEYCLOAK_ADMIN_USERNAME)
                .withAdminPassword(KEYCLOAK_ADMIN_PASSWORD);
        keycloak.start();
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer()
    {
        return new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE));
    }

    @Bean
    GenericContainer<?> keycloakContainer()
    {
        return keycloak;
    }

    /*
    @Bean
    @ServiceConnection
    PulsarContainer pulsarContainer()
    {
        return new PulsarContainer(DockerImageName.parse("apachepulsar/pulsar:latest"));
    }
    */

    public static void updateContainerProperties(DynamicPropertyRegistry registry)
    {
        registry.add("rize.cockpit.security.oauth2.issuer-uri", () -> keycloak.getAuthServerUrl() + "/realms/administration");
    }
}
