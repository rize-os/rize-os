package rize.os.access.manager;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestcontainersConfiguration
{
    public static KeycloakContainer keycloak;

    static
    {
        keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:25.0.1")
                .withFeaturesEnabled("organization")
                .withRealmImportFile("/realm.json")
                .withAdminUsername("_keycloak-admin")
                .withAdminPassword("secret");
        keycloak.start();
    }
}
