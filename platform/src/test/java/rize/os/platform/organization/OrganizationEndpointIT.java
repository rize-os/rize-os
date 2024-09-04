package rize.os.platform.organization;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.commons.organization.OrganizationDto;
import rize.os.platform.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class OrganizationEndpointIT
{
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationEndpoint organizationEndpoint;

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }


    @Test
    void shouldFindAll()
    {
        var organizationToCreate = Organization.builder().name("organization-endpoint-find-all").displayName("shouldFindAll").region("de").build();
        organizationService.createOrganization(organizationToCreate);

        var organizations = organizationEndpoint.findAll();
        assertThat(organizations).isNotEmpty();
        assertThat(organizations).extracting(OrganizationDto::getName).contains(organizationToCreate.getName());
    }

    @Test
    void shouldCreateOrganization()
    {
        var organizationToCreate = OrganizationDto.builder().name("organization-endpoint-create").displayName("shouldCreateOrganization").region("de").build();
        var createdOrganization = organizationEndpoint.create(organizationToCreate);
        assertThat(createdOrganization).isNotNull();
        assertThat(createdOrganization.getName()).isEqualTo(organizationToCreate.getName());
        assertThat(createdOrganization.getDisplayName()).isEqualTo(organizationToCreate.getDisplayName());
        assertThat(createdOrganization.getRegion()).isEqualTo(organizationToCreate.getRegion());
    }

    @Test
    void shouldUpdateOrganization()
    {
        var organizationToCreate = Organization.builder().name("organization-endpoint-update").displayName("shouldUpdateOrganization").region("de").build();
        var createdOrganization = organizationService.createOrganization(organizationToCreate);

        var organizationToUpdate = OrganizationDto.builder().id(createdOrganization.getId()).name("organization-endpoint-update").displayName("shouldUpdateOrganizationUpdated").region("de").build();
        var updatedOrganization = organizationEndpoint.update(organizationToUpdate);
        assertThat(updatedOrganization).isNotNull();
        assertThat(updatedOrganization.getName()).isEqualTo(organizationToUpdate.getName());
        assertThat(updatedOrganization.getDisplayName()).isEqualTo(organizationToUpdate.getDisplayName());
        assertThat(updatedOrganization.getRegion()).isEqualTo(organizationToUpdate.getRegion());
    }

    @Test
    void shouldDeleteOrganization()
    {
        var organizationToCreate = Organization.builder().name("organization-endpoint-delete").displayName("shouldDeleteOrganization").region("de").build();
        var createdOrganization = organizationService.createOrganization(organizationToCreate);

        organizationEndpoint.delete(createdOrganization.getId());
        assertThat(organizationService.findOrganizationById(createdOrganization.getId())).isEmpty();
    }
}