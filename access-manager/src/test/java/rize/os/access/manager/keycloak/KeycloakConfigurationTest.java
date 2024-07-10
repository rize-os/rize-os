package rize.os.access.manager.keycloak;

import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
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
class KeycloakConfigurationTest
{
    @Autowired
    private Keycloak keycloak;

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry)
    {
        registry.add("keycloak.url", TestcontainersConfiguration.keycloak::getAuthServerUrl);
    }

    @Test
    void shouldFindMasterRealm()
    {
        RealmRepresentation realm = keycloak.realm("master").toRepresentation();
        assertThat(realm.getRealm()).isEqualTo("master");
    }

    @Test
    void shouldFindRizeRealm()
    {
        RealmRepresentation realm = keycloak.realm("rize-os").toRepresentation();
        assertThat(realm.getRealm()).isEqualTo("rize-os");
    }
}