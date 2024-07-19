package rize.os.access.manager.organization;

import jakarta.annotation.Nonnull;
import org.keycloak.representations.idm.OrganizationDomainRepresentation;
import org.keycloak.representations.idm.OrganizationRepresentation;
import org.springframework.stereotype.Component;

@Component
class OrganizationMapper
{
    @Nonnull
    Organization toOrganization(@Nonnull OrganizationRepresentation organizationRepresentation)
    {
        return Organization.builder()
                .id(organizationRepresentation.getId())
                .name(organizationRepresentation.getName())
                .alias(organizationRepresentation.getDescription()) // TODO Should be changed, after Keycloak supports alias
                .domains(organizationRepresentation.getDomains().stream().map(this::toDomain).toList())
                .enabled(organizationRepresentation.isEnabled())
                .build();
    }

    @Nonnull
    OrganizationRepresentation toOrganizationRepresentation(@Nonnull Organization organization)
    {
        var organizationRepresentation = new OrganizationRepresentation();
        organizationRepresentation.setId(organization.getId());
        organizationRepresentation.setName(organization.getName());
        organizationRepresentation.setDescription(organization.getAlias());
        organizationRepresentation.setEnabled(organization.isEnabled());
        organization.getDomains().forEach(domain -> organizationRepresentation.addDomain(toDomainRepresentation(domain)));
        return organizationRepresentation;
    }

    @Nonnull
    Organization.Domain toDomain(@Nonnull OrganizationDomainRepresentation domain)
    {
        return Organization.Domain.builder()
                .name(domain.getName())
                .verified(domain.isVerified())
                .build();
    }

    @Nonnull
    OrganizationDomainRepresentation toDomainRepresentation(@Nonnull Organization.Domain domain)
    {
        OrganizationDomainRepresentation domainRepresentation = new OrganizationDomainRepresentation();
        domainRepresentation.setName(domain.getName());
        domainRepresentation.setVerified(domain.isVerified());
        return domainRepresentation;
    }
}
