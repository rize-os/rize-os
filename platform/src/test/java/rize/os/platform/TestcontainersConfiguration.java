package rize.os.platform;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestcontainersConfiguration
{
    private static final String KEYCLOAK_ADMIN_USERNAME = "admin";
    private static final String KEYCLOAK_ADMIN_PASSWORD = "secret";

    static KeycloakContainer keycloak;

    static
    {
        keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:25.0.5")
                .withFeaturesEnabled("organization")
                .withRealmImportFile("/realm.json")
                .withAdminUsername(KEYCLOAK_ADMIN_USERNAME)
                .withAdminPassword(KEYCLOAK_ADMIN_PASSWORD);
        keycloak.start();
    }

    public static void updateContainerProperties(DynamicPropertyRegistry registry)
    {
        registry.add("keycloak.url", TestcontainersConfiguration.keycloak::getAuthServerUrl);
        registry.add("keycloak.admin.username", () -> KEYCLOAK_ADMIN_USERNAME);
        registry.add("keycloak.admin.password", () -> KEYCLOAK_ADMIN_PASSWORD);
    }
}
