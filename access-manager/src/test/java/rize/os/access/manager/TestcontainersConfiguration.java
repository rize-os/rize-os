package rize.os.access.manager;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestcontainersConfiguration
{
    public static KeycloakContainer keycloak;
    public static PostgreSQLContainer<?> postgresql;

    static
    {
        keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:25.0.2")
                .withFeaturesEnabled("organization")
                .withRealmImportFile("/realm.json")
                .withAdminUsername("_keycloak-admin")
                .withAdminPassword("secret");
        keycloak.start();

        postgresql = new PostgreSQLContainer("postgres:16.3")
                .withDatabaseName("access_manager")
                .withUsername("_postgres-admin")
                .withPassword("secret");
        postgresql.start();
    }
}
