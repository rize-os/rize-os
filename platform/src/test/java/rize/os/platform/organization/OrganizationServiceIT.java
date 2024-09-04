package rize.os.platform.organization;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.platform.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class OrganizationServiceIT
{
    @Autowired
    private OrganizationService organizationService;

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }


    @Test
    void shouldFindAllOrganizations()
    {
        var org1 = organizationService.createOrganization(Organization.builder().name("should-find-all-organizations-1").displayName("shouldFindAllOrganizations1").region("de").build());
        var org2 = organizationService.createOrganization(Organization.builder().name("should-find-all-organizations-2").displayName("shouldFindAllOrganizations2").region("de").build());

        var organizations = organizationService.findAll();
        assertThat(organizations).isNotEmpty();
        assertThat(organizations).contains(org1, org2);
    }

    @Test
    void shouldFindOrganizationByName()
    {
        var organizationToFind = Organization.builder()
                .name("should-find-organization-by-name")
                .displayName("shouldFindOrganizationByName")
                .region("de")
                .enabled(true)
                .build();
        organizationService.createOrganization(organizationToFind);

        var foundOrganization = organizationService.findOrganizationByName(organizationToFind.getName());

        assertThat(foundOrganization).isPresent();
        assertThat(foundOrganization.get().getName()).isEqualTo(organizationToFind.getName());
        assertThat(foundOrganization.get().getDisplayName()).isEqualTo(organizationToFind.getDisplayName());
        assertThat(foundOrganization.get().getRegion()).isEqualTo(organizationToFind.getRegion());
        assertThat(foundOrganization.get().isEnabled()).isEqualTo(organizationToFind.isEnabled());
    }

    @Test
    void shouldNotFindOrganizationByName()
    {
        var foundOrganization = organizationService.findOrganizationByName("should-not-find-organization-by-name");

        assertThat(foundOrganization).isEmpty();
    }

    @Test
    void shouldCreateOrganization()
    {
        var organizationToCreate = Organization.builder()
                .name("should-create-organization")
                .displayName("shouldCreateOrganization")
                .region("de")
                .enabled(true)
                .build();

        var createdOrganization = organizationService.createOrganization(organizationToCreate);

        assertThat(createdOrganization).isNotNull();
        assertThat(createdOrganization.getId()).isNotNull();
        assertThat(createdOrganization.getName()).isEqualTo(organizationToCreate.getName());
        assertThat(createdOrganization.getDisplayName()).isEqualTo(organizationToCreate.getDisplayName());
        assertThat(createdOrganization.getRegion()).isEqualTo(organizationToCreate.getRegion());
        assertThat(createdOrganization.isEnabled()).isEqualTo(organizationToCreate.isEnabled());
    }

    @Test
    void shouldFailToCreateOrganizationsWithInvalidValues()
    {
        var organizationWithInvalidName = Organization.builder()
                .name("should fail to create organization with invalid name")
                .displayName("shouldFailToCreateOrganizationWithInvalidName")
                .region("de")
                .build();
        assertThatThrownBy(() -> organizationService.createOrganization(organizationWithInvalidName))
                .isInstanceOf(OrganizationConstraintException.class);

        var organizationWithInvalidDisplayName = Organization.builder()
                .name("should-fail-to-create-organization-with-invalid-display-name")
                .displayName("")
                .region("de")
                .build();
        assertThatThrownBy(() -> organizationService.createOrganization(organizationWithInvalidDisplayName))
                .isInstanceOf(OrganizationConstraintException.class);

        var organizationWithInvalidRegion = Organization.builder()
                .name("should-fail-to-create-organization-with-invalid-region")
                .displayName("shouldFailToCreateOrganizationWithInvalidRegion")
                .region("")
                .build();
        assertThatThrownBy(() -> organizationService.createOrganization(organizationWithInvalidRegion))
                .isInstanceOf(OrganizationConstraintException.class);
    }

    @Test
    void shouldFailToCreateOrganizationsWithSameName()
    {
        var organizationToCreate = Organization.builder()
                .name("should-fail-to-create-organization-with-same-name")
                .displayName("shouldFailToCreateOrganizationWithSameName")
                .region("de")
                .build();
        organizationService.createOrganization(organizationToCreate);

        var organizationWithSameName = Organization.builder()
                .name("should-fail-to-create-organization-with-same-name")
                .displayName("shouldFailToCreateOrganizationWithSameName")
                .region("de")
                .build();
        assertThatThrownBy(() -> organizationService.createOrganization(organizationWithSameName))
                .isInstanceOf(OrganizationAlreadyExistsException.class);
    }
}