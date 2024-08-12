package rize.os.access.manager.organization;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import rize.os.access.manager.TestcontainersConfiguration;
import rize.os.access.manager.organization.exceptions.OrganizationConstraintException;
import rize.os.access.manager.organization.exceptions.OrganizationCreateException;
import rize.os.access.manager.organization.exceptions.OrganizationNotFoundException;

import java.util.List;
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
        var organizationToCreate = Organization.builder()
                .name("should-find-all-organizations")
                .displayName("shouldFindAllOrganizations")
                .alias("should-find-all-organizations-alias")
                .imageId(UUID.randomUUID())
                .build();

        organizationService.createOrganization(organizationToCreate);
        var organizations = organizationService.findAll();

        assertThat(organizations).isNotNull();
        assertThat(organizations).hasSizeGreaterThanOrEqualTo(1);
        assertThat(organizations).anyMatch(organization -> organization.getName().equals(organizationToCreate.getName()));
    }

    @Test
    void shouldFindOrganizationsBySearchTerm()
    {
        var organizationToCreate = Organization.builder()
                .name("should-find-organizations-by-search-term")
                .displayName("shouldFindOrganizationsBySearchTerm")
                .alias("should-find-organizations-by-search-term-alias")
                .imageId(UUID.randomUUID())
                .build();

        organizationService.createOrganization(organizationToCreate);
        var organizations = organizationService.find("by-search-term");

        assertThat(organizations).isNotNull();
        assertThat(organizations).hasSizeGreaterThanOrEqualTo(1);
        assertThat(organizations).anyMatch(organization -> organization.getName().equals(organizationToCreate.getName()));
    }

    @Test
    void shouldFindNoOrganizationsBySearchTerm()
    {
        var organizations = organizationService.find("Organization XXXXXXXXX");

        assertThat(organizations).isNotNull();
        assertThat(organizations).isEmpty();
    }

    @Test
    void shouldFindOrganizationById()
    {
        var organizationToCreate = Organization.builder()
                .name("should-find-organization-by-id")
                .displayName("shouldFindOrganizationById")
                .alias("should-find-organization-by-id-alias")
                .imageId(UUID.randomUUID())
                .build();

        var createdOrganization = organizationService.createOrganization(organizationToCreate);
        assertThat(createdOrganization).isNotNull();
        assertThat(createdOrganization.getId()).isNotNull();

        var foundOrganization = organizationService.findById(createdOrganization.getId());
        assertThat(foundOrganization).isPresent();
        assertThat(foundOrganization.get()).isEqualTo(createdOrganization);
    }

    @Test
    void shouldNotFindOrganizationById()
    {
        var foundOrganization = organizationService.findById(UUID.randomUUID().toString());
        assertThat(foundOrganization).isEmpty();
    }

    @Test
    void shouldFindOrganizationByName()
    {
        var organizationToCreate = Organization.builder()
                .name("should-find-organization-by-name")
                .displayName("shouldFindOrganizationByName")
                .alias("should-find-organization-by-name-alias")
                .imageId(UUID.randomUUID())
                .build();

        var createdOrganization = organizationService.createOrganization(organizationToCreate);
        var foundOrganization = organizationService.findByName(createdOrganization.getName());

        assertThat(foundOrganization).isPresent();
        assertThat(foundOrganization.get()).isEqualTo(createdOrganization);
    }

    @Test
    void shouldNotFindOrganizationByName()
    {
        var foundOrganization = organizationService.findByName("some-random-name-xxxxxxxxx");
        assertThat(foundOrganization).isEmpty();
    }

    @Test
    void shouldFindOrganizationByAlias()
    {
        var organizationToCreate = Organization.builder()
                .name("should-find-organization-by-alias")
                .displayName("shouldFindOrganizationByAlias")
                .alias("should-find-organization-by-alias-alias")
                .imageId(UUID.randomUUID())
                .build();

        var createdOrganization = organizationService.createOrganization(organizationToCreate);
        var foundOrganization = organizationService.findByAlias(createdOrganization.getAliases().getFirst());

        assertThat(foundOrganization).isPresent();
        assertThat(foundOrganization.get()).isEqualTo(createdOrganization);
    }

    @Test
    void shouldNotFindOrganizationByAlias()
    {
        var foundOrganization = organizationService.findByAlias("some-random-alias-xxxxxxxxx");
        assertThat(foundOrganization).isEmpty();
    }

    @Test
    void shouldCreateOrganization()
    {
        var organizationToCreate = Organization.builder()
                .name("should-create-organization")
                .displayName("shouldCreateOrganization")
                .alias("should-create-organization-alias")
                .imageId(UUID.randomUUID())
                .build();

        var createdOrganization = organizationService.createOrganization(organizationToCreate);

        assertThat(createdOrganization.getId()).isNotNull();
        assertThat(createdOrganization.getName()).isEqualTo(organizationToCreate.getName());
        assertThat(createdOrganization.getDisplayName()).isEqualTo(organizationToCreate.getDisplayName());
        assertThat(createdOrganization.getAliases()).hasSize(1);
        assertThat(createdOrganization.getAliases().getFirst()).isEqualTo(organizationToCreate.getAliases().getFirst());
        assertThat(createdOrganization.getImageId()).isEqualTo(organizationToCreate.getImageId());
        assertThat(createdOrganization.isEnabled()).isTrue();

        var foundOrganization = organizationService.findById(createdOrganization.getId());
        assertThat(foundOrganization).isPresent();
        assertThat(foundOrganization.get()).isEqualTo(createdOrganization);
    }

    @Test
    void shouldCreateOrganizationWithoutAliasSet()
    {
        var organizationToCreate = Organization.builder()
                .name("should-create-organization-without-alias-set")
                .displayName("shouldCreateOrganizationWithoutAliasSet")
                .imageId(UUID.randomUUID())
                .build();

        var createdOrganization = organizationService.createOrganization(organizationToCreate);

        assertThat(createdOrganization.getId()).isNotNull();
        assertThat(createdOrganization.getName()).isEqualTo(organizationToCreate.getName());
        assertThat(createdOrganization.getDisplayName()).isEqualTo(organizationToCreate.getDisplayName());
        assertThat(createdOrganization.getAliases()).hasSize(1);
        assertThat(createdOrganization.getAliases().getFirst()).isEqualTo(organizationToCreate.getName());
        assertThat(createdOrganization.getImageId()).isEqualTo(organizationToCreate.getImageId());
        assertThat(createdOrganization.isEnabled()).isTrue();

        var foundOrganization = organizationService.findByAlias(organizationToCreate.getName());
        assertThat(foundOrganization).isPresent();
        assertThat(foundOrganization.get()).isEqualTo(createdOrganization);
    }

    @Test
    void shouldFailToCreateOrganizationWithSameName()
    {
        var organizationToCreate1 = Organization.builder()
                .name("should-fail-to-create-organization-with-same-name")
                .alias("should-fail-to-create-organization-with-same-name-1")
                .displayName("shouldFailToCreateOrganizationWithSameName1")
                .build();

        organizationService.createOrganization(organizationToCreate1);

        var organizationToCreate2 = Organization.builder()
                .name("should-fail-to-create-organization-with-same-name")
                .alias("should-fail-to-create-organization-with-same-name-2")
                .displayName("shouldFailToCreateOrganizationWithSameName2")
                .build();

        assertThatThrownBy(() -> organizationService.createOrganization(organizationToCreate2))
                .isInstanceOf(OrganizationCreateException.class)
                .hasMessageContaining("Name \"" + organizationToCreate2.getName() + "\" already exists");
    }

    @Test
    void shouldFailToCreateOrganizationWithExistingAlias()
    {
        var organizationToCreate1 = Organization.builder()
                .name("should-fail-to-create-organization-with-existing-alias-1")
                .alias("should-fail-to-create-organization-with-existing-alias")
                .displayName("shouldFailToCreateOrganizationWithExistingAlias1")
                .build();

        organizationService.createOrganization(organizationToCreate1);

        var organizationToCreate2 = Organization.builder()
                .name("should-fail-to-create-organization-with-existing-alias-2")
                .alias("should-fail-to-create-organization-with-existing-alias")
                .displayName("shouldFailToCreateOrganizationWithExistingAlias2")
                .build();

        assertThatThrownBy(() -> organizationService.createOrganization(organizationToCreate2))
                .isInstanceOf(OrganizationCreateException.class)
                .hasMessageContaining("Alias \"" + organizationToCreate2.getAliases().getFirst() + "\" already exists");
    }

    @Test
    void shouldFailToCreateOrganizationWithInvalidNames()
    {
        shouldFailToCreateOrganizationWithInvalidName("");
        shouldFailToCreateOrganizationWithInvalidName("Name-with-Capital-Letters");
        shouldFailToCreateOrganizationWithInvalidName("name-with-special-characters!");
        shouldFailToCreateOrganizationWithInvalidName("name with spaces");
        shouldFailToCreateOrganizationWithInvalidName("a");
        shouldFailToCreateOrganizationWithInvalidName(RandomStringUtils.random(65, true, true));
    }

    void shouldFailToCreateOrganizationWithInvalidName(String nameToValidate)
    {
        var organizationToCreate = Organization.builder()
                .name(nameToValidate)
                .displayName("Organization with invalid name")
                .alias("valid-alias")
                .build();

        assertThatThrownBy(() -> organizationService.createOrganization(organizationToCreate))
                .isInstanceOf(OrganizationConstraintException.class);
    }

    @Test
    void shouldFailToCreateOrganizationWithInvalidAliases()
    {
        shouldFailToCreateOrganizationWithInvalidAlias("");
        shouldFailToCreateOrganizationWithInvalidAlias("Alias-with-Capital-Letters");
        shouldFailToCreateOrganizationWithInvalidAlias("alias-with-special-characters!");
        shouldFailToCreateOrganizationWithInvalidAlias("alias with spaces");
        shouldFailToCreateOrganizationWithInvalidAlias("a");
        shouldFailToCreateOrganizationWithInvalidAlias(RandomStringUtils.random(65, true, true));
    }

    void shouldFailToCreateOrganizationWithInvalidAlias(String aliasToValidate)
    {
        var organizationToCreate = Organization.builder()
                .name("valid-name")
                .displayName("Organization with invalid alias")
                .alias(aliasToValidate)
                .build();

        assertThatThrownBy(() -> organizationService.createOrganization(organizationToCreate))
                .isInstanceOf(OrganizationConstraintException.class);
    }

    @Test
    void shouldFailToCreateOrganizationWithoutDisplayName()
    {
        var organizationToCreate = Organization.builder()
                .name("should-fail-to-create-organization-without-display-name")
                .alias("should-fail-to-create-organization-without-display-name")
                .build();

        assertThatThrownBy(() -> organizationService.createOrganization(organizationToCreate))
                .isInstanceOf(OrganizationConstraintException.class);
    }

    @Test
    void shouldUpdateOrganization()
    {
        var organizationToCreate = Organization.builder()
                .name("should-update-organization")
                .displayName("shouldUpdateOrganization")
                .alias("should-update-organization-alias")
                .imageId(UUID.randomUUID())
                .build();

        var createdOrganization = organizationService.createOrganization(organizationToCreate);

        createdOrganization.setName("should-update-organization-updated");
        createdOrganization.setDisplayName("Should Be Updated");
        createdOrganization.setAliases(List.of("should-update-organization-alias-updated"));
        createdOrganization.setImageId(UUID.randomUUID());
        createdOrganization.setEnabled(false);

        var updatedOrganization = organizationService.updateOrganization(createdOrganization);
        assertThat(updatedOrganization).isNotNull();
        assertThat(updatedOrganization.getId()).isEqualTo(createdOrganization.getId());
        assertThat(updatedOrganization.getName()).isEqualTo(createdOrganization.getName());
        assertThat(updatedOrganization.getDisplayName()).isEqualTo(createdOrganization.getDisplayName());
        assertThat(updatedOrganization.getAliases()).hasSize(1);
        assertThat(updatedOrganization.getAliases()).contains(createdOrganization.getAliases().getFirst());
        assertThat(updatedOrganization.getImageId()).isEqualTo(createdOrganization.getImageId());
        assertThat(updatedOrganization.isEnabled()).isFalse();
    }

    @Test
    void shouldFailToUpdateNonExistingOrganization()
    {
        var organizationToUpdate = Organization.builder()
                .id(UUID.randomUUID().toString())
                .name("should-fail-to-update-non-existing-organization")
                .displayName("shouldFailToUpdateNonExistingOrganization")
                .alias("should-fail-to-update-non-existing-organization-alias")
                .build();

        assertThatThrownBy(() -> organizationService.updateOrganization(organizationToUpdate))
                .isInstanceOf(OrganizationNotFoundException.class);
    }

    @Test
    void shouldFailToUpdateOrganizationWithExistingName()
    {
        var organizationToCreate1 = Organization.builder()
                .name("should-fail-to-update-organization-with-existing-name-1")
                .displayName("shouldFailToUpdateOrganizationWithExistingName1")
                .alias("should-fail-to-update-organization-with-existing-name-alias-1")
                .build();

        organizationService.createOrganization(organizationToCreate1);

        var organizationToCreate2 = Organization.builder()
                .name("should-fail-to-update-organization-with-existing-name-2")
                .displayName("shouldFailToUpdateOrganizationWithExistingName2")
                .alias("should-fail-to-update-organization-with-existing-name-alias-2")
                .build();

        var createdOrganization2 = organizationService.createOrganization(organizationToCreate2);

        createdOrganization2.setName(organizationToCreate1.getName());

        assertThatThrownBy(() -> organizationService.updateOrganization(createdOrganization2))
                .isInstanceOf(OrganizationCreateException.class)
                .hasMessageContaining("Name \"" + organizationToCreate1.getName() + "\" already exists");
    }

    @Test
    void shouldFailToUpdateOrganizationWithExistingAlias()
    {
        var organizationToCreate1 = Organization.builder()
                .name("should-fail-to-update-organization-with-existing-alias-1")
                .displayName("shouldFailToUpdateOrganizationWithExistingAlias1")
                .alias("should-fail-to-update-organization-with-existing-alias-1")
                .build();

        organizationService.createOrganization(organizationToCreate1);

        var organizationToCreate2 = Organization.builder()
                .name("should-fail-to-update-organization-with-existing-alias-2")
                .displayName("shouldFailToUpdateOrganizationWithExistingAlias2")
                .alias("should-fail-to-update-organization-with-existing-alias-2")
                .build();

        var createdOrganization2 = organizationService.createOrganization(organizationToCreate2);

        createdOrganization2.setAliases(organizationToCreate1.getAliases());

        assertThatThrownBy(() -> organizationService.updateOrganization(createdOrganization2))
                .isInstanceOf(OrganizationCreateException.class)
                .hasMessageContaining("Alias \"" + organizationToCreate1.getAliases().getFirst() + "\" already exists");
    }

    @Test
    void shouldDeleteOrganization()
    {
        var organizationToCreate = Organization.builder()
                .name("should-delete-organization")
                .displayName("shouldDeleteOrganization")
                .alias("should-delete-organization-alias")
                .imageId(UUID.randomUUID())
                .build();

        var createdOrganization = organizationService.createOrganization(organizationToCreate);

        organizationService.deleteOrganization(createdOrganization.getId());

        var foundOrganization = organizationService.findById(createdOrganization.getId());
        assertThat(foundOrganization).isEmpty();
    }

    @Test
    void shouldFailToDeleteNonExistingOrganization()
    {
        assertThatThrownBy(() -> organizationService.deleteOrganization(UUID.randomUUID().toString()))
                .isInstanceOf(OrganizationNotFoundException.class);
    }
}