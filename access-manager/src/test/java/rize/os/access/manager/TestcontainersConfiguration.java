package rize.os.access.manager;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestcontainersConfiguration
{
    private static final String KEYCLOAK_ADMIN_USERNAME = "_keycloak-admin";
    private static final String KEYCLOAK_ADMIN_PASSWORD = "secret";

    private static final String POSTGRES_DATABASE_NAME = "access_manager";
    private static final String POSTGRES_USERNAME = "_postgres-admin";
    private static final String POSTGRES_PASSWORD = "secret";

    static KeycloakContainer keycloak;
    static PostgreSQLContainer<?> postgresql;

    static
    {
        keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:25.0.2")
                .withFeaturesEnabled("organization")
                .withRealmImportFile("/realm.json")
                .withAdminUsername(KEYCLOAK_ADMIN_USERNAME)
                .withAdminPassword(KEYCLOAK_ADMIN_PASSWORD);
        keycloak.start();

        postgresql = new PostgreSQLContainer("postgres:16.3")
                .withDatabaseName(POSTGRES_DATABASE_NAME)
                .withUsername(POSTGRES_USERNAME)
                .withPassword(POSTGRES_PASSWORD);
        postgresql.start();
    }

    public static void updateContainerProperties(DynamicPropertyRegistry registry)
    {
        registry.add("keycloak.url", TestcontainersConfiguration.keycloak::getAuthServerUrl);
        registry.add("keycloak.admin.username", () -> KEYCLOAK_ADMIN_USERNAME);
        registry.add("keycloak.admin.password", () -> KEYCLOAK_ADMIN_PASSWORD);

        registry.add("datasource.url", TestcontainersConfiguration.postgresql::getJdbcUrl);
        registry.add("datasource.username", () -> POSTGRES_USERNAME);
        registry.add("datasource.password", () -> POSTGRES_PASSWORD);
    }
}
