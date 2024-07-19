package rize.os.access.manager.organization;

import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.OrganizationDomainRepresentation;
import org.keycloak.representations.idm.OrganizationRepresentation;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationMapperTest
{
    private final OrganizationMapper organizationMapper = new OrganizationMapper();

    @Test
    void shouldMapOrganizationRepresentationToOrganization()
    {
        // Given
        var domainRepresentation = new OrganizationDomainRepresentation("test-organization.com");
        domainRepresentation.setVerified(true);
        var organizationRepresentation = new OrganizationRepresentation();
        organizationRepresentation.setId(UUID.randomUUID().toString());
        organizationRepresentation.setName("Name of the Test Organization");
        organizationRepresentation.setDescription("alias-of-organization");
        organizationRepresentation.setEnabled(true);
        organizationRepresentation.addDomain(domainRepresentation);

        // When
        Organization organization = organizationMapper.toOrganization(organizationRepresentation);

        // Then
        assertThat(organization.getId()).isEqualTo(organizationRepresentation.getId());
        assertThat(organization.getName()).isEqualTo(organizationRepresentation.getName());
        assertThat(organization.getAlias()).isEqualTo(organizationRepresentation.getDescription());
        assertThat(organization.isEnabled()).isEqualTo(organizationRepresentation.isEnabled());
        assertThat(organization.getDomains()).hasSize(1);
        assertThat(organization.getDomains().getFirst().getName()).isEqualTo(domainRepresentation.getName());
        assertThat(organization.getDomains().getFirst().isVerified()).isEqualTo(domainRepresentation.isVerified());
    }

    @Test
    void shouldMapOrganizationToOrganizationRepresentation()
    {
        // Given
        Organization organization = Organization.builder()
                .id(UUID.randomUUID().toString())
                .name("Name of the Test Organization")
                .alias("alias-of-organization")
                .enabled(true)
                .domains(List.of(new Organization.Domain("test-organization.com", true)))
                .build();

        // When
        OrganizationRepresentation organizationRepresentation = organizationMapper.toOrganizationRepresentation(organization);

        // Then
        assertThat(organizationRepresentation.getId()).isEqualTo(organization.getId());
        assertThat(organizationRepresentation.getName()).isEqualTo(organization.getName());
        assertThat(organizationRepresentation.getDescription()).isEqualTo(organization.getAlias());
        assertThat(organizationRepresentation.isEnabled()).isEqualTo(organization.isEnabled());
        assertThat(organizationRepresentation.getDomains()).hasSize(1);
        assertThat(organizationRepresentation.getDomains().stream().findFirst().get().getName()).isEqualTo(organization.getDomains().getFirst().getName());
        assertThat(organizationRepresentation.getDomains().stream().findFirst().get().isVerified()).isEqualTo(organization.getDomains().getFirst().isVerified());
    }
}