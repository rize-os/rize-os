package rize.os.access.manager.organization;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.access.manager.TestcontainersConfiguration;
import rize.os.access.manager.organization.exceptions.OrganizationConstraintException;
import rize.os.access.manager.organization.exceptions.OrganizationCreateException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class OrganizationServiceTest
{
    @Autowired
    private OrganizationService organizationService;

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry)
    {
        registry.add("keycloak.url", TestcontainersConfiguration.keycloak::getAuthServerUrl);
    }

    @Test
    void shouldCreateOrganizationWithoutDomain()
    {
        var organizationToCreate = Organization.builder()
                .name("Test Organization 1")
                .description("Description of the Test Organization 1")
                .alias(Organization.nameToAlias("Test Organization 1"))
                .build();

        var createdOrganization = organizationService.createOrganization(organizationToCreate);

        assertThat(createdOrganization.getId()).isNotNull();
        assertThat(createdOrganization.getName()).isEqualTo(organizationToCreate.getName());
        assertThat(createdOrganization.getDescription()).isEqualTo(organizationToCreate.getDescription());
        assertThat(createdOrganization.getAlias()).isEqualTo(organizationToCreate.getAlias());
        assertThat(createdOrganization.isEnabled()).isTrue();
        assertThat(createdOrganization.getDomains()).hasSize(1);
        assertThat(createdOrganization.getDomains().getFirst().getName()).isEqualTo(organizationToCreate.getAlias());
    }

    @Test
    void shouldCreateOrganizationWithDomain()
    {
        var organizationToCreate = Organization.builder()
                .name("Test Organization 2")
                .alias(Organization.nameToAlias("Test Organization 2"))
                .domains(List.of(new Organization.Domain("test-organization-2.com", true)))
                .build();

        var createdOrganization = organizationService.createOrganization(organizationToCreate);

        assertThat(createdOrganization.getId()).isNotNull();
        assertThat(createdOrganization.getName()).isEqualTo(organizationToCreate.getName());
        assertThat(createdOrganization.getDescription()).isEqualTo(organizationToCreate.getDescription());
        assertThat(createdOrganization.getAlias()).isEqualTo(organizationToCreate.getAlias());
        assertThat(createdOrganization.isEnabled()).isTrue();
        assertThat(createdOrganization.getDomains()).hasSize(1);
        assertThat(createdOrganization.getDomains().getFirst().getName()).isEqualTo(organizationToCreate.getDomains().getFirst().getName());
        assertThat(createdOrganization.getDomains().getFirst().isVerified()).isEqualTo(organizationToCreate.getDomains().getFirst().isVerified());
    }

    @Test
    void shouldFailToCreateOrganizationWithInvalidAlias()
    {
        var organizationToCreate1 = Organization.builder()
                .name("Test Organization 3")
                .description("Description of the Test Organization 3")
                .alias("invalid_alias")
                .build();

        assertThatThrownBy(() -> organizationService.createOrganization(organizationToCreate1))
                .isInstanceOf(OrganizationConstraintException.class);

        var organizationToCreate2 = Organization.builder()
                .name("Test Organization 3")
                .description("Description of the Test Organization 3")
                .alias("")
                .build();

        assertThatThrownBy(() -> organizationService.createOrganization(organizationToCreate2))
                .isInstanceOf(OrganizationConstraintException.class);
    }

    @Test
    void shouldFailToCreateOrganizationWithoutName()
    {
        var organizationToCreate1 = Organization.builder()
                .description("Description of the Test Organization 4")
                .alias(Organization.nameToAlias("Test Organization 4"))
                .build();

        assertThatThrownBy(() -> organizationService.createOrganization(organizationToCreate1))
                .isInstanceOf(OrganizationConstraintException.class);

        var organizationToCreate2 = Organization.builder()
                .name("")
                .description("Description of the Test Organization 4")
                .alias(Organization.nameToAlias("Test Organization 4"))
                .build();

        assertThatThrownBy(() -> organizationService.createOrganization(organizationToCreate2))
                .isInstanceOf(OrganizationConstraintException.class);
    }

    @Test
    void shouldFailToCreateOrganizationsWithSameName()
    {
        var organizationToCreate1 = Organization.builder()
                .name("Test Organization 5")
                .description("Description of the Test Organization 5")
                .alias(Organization.nameToAlias("Test Organization 5"))
                .build();

        organizationService.createOrganization(organizationToCreate1);

        var organizationToCreate2 = Organization.builder()
                .name("Test Organization 5")
                .description("Description of the Test Organization 5")
                .alias(Organization.nameToAlias("Test Organization 5"))
                .build();

        assertThatThrownBy(() -> organizationService.createOrganization(organizationToCreate2))
                .isInstanceOf(OrganizationCreateException.class)
                .hasMessageContaining("409");
    }
}