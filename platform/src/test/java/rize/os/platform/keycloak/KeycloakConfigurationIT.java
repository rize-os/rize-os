package rize.os.platform.keycloak;

import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.platform.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class KeycloakConfigurationIT
{
    @Autowired
    private Keycloak keycloak;

    @Autowired
    private RealmResource realmResource;

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
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
        assertThat(realmResource.toRepresentation().getRealm()).isEqualTo("rize-os");
    }
}