package rize.os.platform.organization;

import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.OrganizationRepresentation;
import rize.os.commons.organization.OrganizationDto;

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

    @Test
    void shouldMapOrganizationToOrganizationDto()
    {
        var organization = Organization.builder()
                .id(UUID.randomUUID().toString())
                .name("organization-name")
                .displayName("Display Name")
                .region("de")
                .enabled(true)
                .build();

        var organizationDto = organizationMapper.toOrganizationDto(organization);

        assertThat(organizationDto.getId()).isEqualTo(organization.getId());
        assertThat(organizationDto.getName()).isEqualTo(organization.getName());
        assertThat(organizationDto.getDisplayName()).isEqualTo(organization.getDisplayName());
        assertThat(organizationDto.getRegion()).isEqualTo(organization.getRegion());
        assertThat(organizationDto.isEnabled()).isTrue();
    }

    @Test
    void shouldMapOrganizationDtoToOrganization()
    {
        var organizationDto = OrganizationDto.builder()
                .id(UUID.randomUUID().toString())
                .name("organization-name")
                .displayName("Display Name")
                .region("de")
                .enabled(true)
                .build();

        var organization = organizationMapper.toOrganization(organizationDto);

        assertThat(organization.getId()).isEqualTo(organizationDto.getId());
        assertThat(organization.getName()).isEqualTo(organizationDto.getName());
        assertThat(organization.getDisplayName()).isEqualTo(organizationDto.getDisplayName());
        assertThat(organization.getRegion()).isEqualTo(organizationDto.getRegion());
        assertThat(organization.isEnabled()).isTrue();
    }
}