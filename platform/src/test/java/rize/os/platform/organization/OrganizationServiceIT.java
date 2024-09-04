package rize.os.platform.organization;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.platform.TestcontainersConfiguration;

import java.util.UUID;

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
    void shouldFindOrganizationById()
    {
        var organizationToFind = Organization.builder()
                .name("should-find-organization-by-id")
                .displayName("shouldFindOrganizationById")
                .region("de")
                .enabled(true)
                .build();
        var createdOrganization = organizationService.createOrganization(organizationToFind);

        var foundOrganization = organizationService.findOrganizationById(createdOrganization.getId()).orElseThrow();
        assertThat(foundOrganization).isEqualTo(createdOrganization);
    }

    @Test
    void shouldNotFindOrganizationById()
    {
        var foundOrganization = organizationService.findOrganizationById(UUID.randomUUID().toString());
        assertThat(foundOrganization).isEmpty();
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

    @Test
    void shouldUpdateOrganization()
    {
        var organizationToCreate = Organization.builder()
                .name("should-update-organization")
                .displayName("shouldUpdateOrganization")
                .region("de")
                .enabled(true)
                .build();
        var createdOrganization = organizationService.createOrganization(organizationToCreate);

        var organizationToUpdate = Organization.builder()
                .id(createdOrganization.getId())
                .name("should-update-organization-updated")
                .displayName("shouldUpdateOrganizationUpdated")
                .region("de-1")
                .enabled(false)
                .build();
        var updatedOrganization = organizationService.updateOrganization(organizationToUpdate);

        assertThat(updatedOrganization).isNotNull();
        assertThat(updatedOrganization.getId()).isEqualTo(createdOrganization.getId());
        assertThat(updatedOrganization.getName()).isEqualTo(organizationToUpdate.getName());
        assertThat(updatedOrganization.getDisplayName()).isEqualTo(organizationToUpdate.getDisplayName());
        assertThat(updatedOrganization.getRegion()).isEqualTo(organizationToUpdate.getRegion());
        assertThat(updatedOrganization.isEnabled()).isEqualTo(organizationToUpdate.isEnabled());

        var foundOrganization = organizationService.findOrganizationByName(organizationToUpdate.getName()).orElseThrow();
        assertThat(foundOrganization).isEqualTo(updatedOrganization);
    }

    @Test
    void shouldFailToUpdateNonExistingOrganization()
    {
        var organizationToUpdate = Organization.builder()
                .id(UUID.randomUUID().toString())
                .name("should-fail-to-update-non-existing-organization")
                .displayName("shouldFailToUpdateNonExistingOrganization")
                .region("de")
                .build();

        assertThatThrownBy(() -> organizationService.updateOrganization(organizationToUpdate))
                .isInstanceOf(OrganizationNotFoundException.class);
    }

    @Test
    void shouldFailToUpdateOrganizationToExistingName()
    {
        var organizationToCreate1 = Organization.builder()
                .name("should-fail-to-update-organization-to-existing-name-1")
                .displayName("shouldFailToUpdateOrganizationToExistingName")
                .region("de")
                .build();
        organizationService.createOrganization(organizationToCreate1);

        var organizationToCreate2 = Organization.builder()
                .name("should-fail-to-update-organization-to-existing-name-2")
                .displayName("shouldFailToUpdateOrganizationToExistingName")
                .region("de")
                .build();
        var organizationToUpdate = organizationService.createOrganization(organizationToCreate2);

        organizationToUpdate.setName(organizationToCreate1.getName());
        assertThatThrownBy(() -> organizationService.updateOrganization(organizationToUpdate))
                .isInstanceOf(OrganizationAlreadyExistsException.class);
    }

    @Test
    void shouldFailToUpdateOrganizationWithInvalidValues()
    {
        var organizationToCreate = Organization.builder()
                .name("should-fail-to-update-organization-with-invalid-values")
                .displayName("shouldFailToUpdateOrganizationWithInvalidValues")
                .region("de")
                .enabled(true)
                .build();
        var organizationToUpdate = organizationService.createOrganization(organizationToCreate);

        organizationToUpdate.setName("shouldFailToUpdateOrganizationWithInvalidValues");
        organizationToUpdate.setDisplayName("");
        organizationToUpdate.setRegion("");

        assertThatThrownBy(() -> organizationService.updateOrganization(organizationToUpdate))
                .isInstanceOf(OrganizationConstraintException.class)
                .extracting("violations", InstanceOfAssertFactories.ITERABLE)
                .hasSize(3);
    }
}