package rize.os.platform.organization;

import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.OrganizationRepresentation;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class OrganizationMapperTest
{
    private final OrganizationMapper organizationMapper = new OrganizationMapper();

    @Test
    void shouldMapOrganizationRepresentationToOrganization()
    {
        var organizationRepresentation = new OrganizationRepresentation();
        organizationRepresentation.setId(UUID.randomUUID().toString());
        organizationRepresentation.setName("organization-name");
        organizationRepresentation.setEnabled(true);
        organizationRepresentation.setAttributes(new HashMap<>());
        organizationRepresentation.getAttributes().put(Organization.DISPLAY_NAME_ATTRIBUTE, List.of("display-name"));
        organizationRepresentation.getAttributes().put(Organization.REGION_ATTRIBUTE, List.of("region"));

        var organization = organizationMapper.toOrganization(organizationRepresentation);

        assertThat(organization.getId()).isEqualTo(organizationRepresentation.getId());
        assertThat(organization.getName()).isEqualTo("organization-name");
        assertThat(organization.getDisplayName()).isEqualTo("display-name");
        assertThat(organization.getRegion()).isEqualTo("region");
        assertThat(organization.isEnabled()).isTrue();
    }

    @Test
    void shouldHaveEmptyDisplayNameWithoutAttribute()
    {
        var organizationRepresentation = new OrganizationRepresentation();
        organizationRepresentation.setId(UUID.randomUUID().toString());
        organizationRepresentation.setName("organization-name");
        organizationRepresentation.setEnabled(true);
        organizationRepresentation.setAttributes(new HashMap<>());
        organizationRepresentation.getAttributes().put(Organization.REGION_ATTRIBUTE, List.of("region"));

        var organization = organizationMapper.toOrganization(organizationRepresentation);

        assertThat(organization.getId()).isEqualTo(organizationRepresentation.getId());
        assertThat(organization.getName()).isEqualTo("organization-name");
        assertThat(organization.getDisplayName()).isEmpty();
        assertThat(organization.getRegion()).isEqualTo("region");
        assertThat(organization.isEnabled()).isTrue();
    }

    @Test
    void shouldHaveEmptyRegionWithoutAttribute()
    {
        var organizationRepresentation = new OrganizationRepresentation();
        organizationRepresentation.setId(UUID.randomUUID().toString());
        organizationRepresentation.setName("organization-name");
        organizationRepresentation.setEnabled(true);
        organizationRepresentation.setAttributes(new HashMap<>());
        organizationRepresentation.getAttributes().put(Organization.DISPLAY_NAME_ATTRIBUTE, List.of("display-name"));

        var organization = organizationMapper.toOrganization(organizationRepresentation);

        assertThat(organization.getId()).isEqualTo(organizationRepresentation.getId());
        assertThat(organization.getName()).isEqualTo("organization-name");
        assertThat(organization.getDisplayName()).isEqualTo("display-name");
        assertThat(organization.getRegion()).isEmpty();
        assertThat(organization.isEnabled()).isTrue();
    }
}