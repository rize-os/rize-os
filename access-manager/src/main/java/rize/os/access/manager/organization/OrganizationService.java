package rize.os.access.manager.organization;

import jakarta.annotation.Nonnull;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.stereotype.Service;
import rize.os.access.manager.organization.exceptions.OrganizationConstraintException;
import rize.os.access.manager.organization.exceptions.OrganizationCreateException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class OrganizationService
{
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final OrganizationMapper organizationMapper;
    private final RealmResource realmResource;

    /**
     * Returns all organizations from Keycloak.
     * @return List of all organizations
     */
    @Nonnull
    List<Organization> findAll()
    {
        log.debug("Loading all organizations from Keycloak");
        var organizationRepresentations = realmResource.organizations().getAll();
        var organizations = organizationRepresentations.stream().map(organizationMapper::toOrganization).toList();
        return loggedOrganizations(organizations);
    }

    /**
     * Returns a list of organizations from Keycloak that matches the given search term.
     * @param search Search term to filter organizations
     * @return List of organizations that matches the search term
     */
    @Nonnull
    List<Organization> find(@Nonnull String search)
    {
        log.debug("Searching organizations in Keycloak with search term: {}", search);
        var organizationRepresentations = realmResource.organizations().search(search, false, 0, Integer.MAX_VALUE);
        var organizations = organizationRepresentations.stream().map(organizationMapper::toOrganization).toList();
        return loggedOrganizations(organizations);
    }

    /**
     * Returns the organization from Keycloak that matches the given id.
     * @param id ID of the organization to find
     * @return The organization object if found, otherwise empty
     */
    @Nonnull
    Optional<Organization> findById(@Nonnull String id)
    {
        log.debug("Searching organization in Keycloak with id: {}", id);

        try
        {
            var organizationRepresentation = realmResource.organizations().get(id).toRepresentation();
            var organization = organizationMapper.toOrganization(organizationRepresentation);
            log.debug("Found organization: {}", organization);
            return Optional.of(organization);
        }
        catch (NotFoundException e)
        {
            log.debug("Organization with id {} not found in Keycloak", id);
            return Optional.empty();
        }
    }

    /**
     * Creates a new organization in the Keycloak realm. If the organization object does not have any domains, a default domain with the value of the alias is added.
     * @param organization Object of the organization to create
     * @return The created organization object
     * @throws OrganizationConstraintException If the organization object has invalid values
     * @throws OrganizationCreateException If the organization could not be created in Keycloak
     */
    @Nonnull
    Organization createOrganization(@Nonnull Organization organization) throws OrganizationConstraintException, OrganizationCreateException
    {
        if (organization.getDomains() == null || organization.getDomains().isEmpty())
            organization.setDomains(List.of(new Organization.Domain(organization.getAlias(), false)));

        log.info("Creating new organization: {}", organization);
        validateOrganization(organization);

        var organizationRepresentation = organizationMapper.toOrganizationRepresentation(organization);
        try (var response = realmResource.organizations().create(organizationRepresentation))
        {
            if (response.getStatus() != 201)
                throw new OrganizationCreateException(organization, response);

            var createdOrganizationRepresentation = realmResource.organizations().search(organization.getName(), true, 0, 1).getFirst();
            var createdOrganization = organizationMapper.toOrganization(createdOrganizationRepresentation);
            log.info("Created organization successfully: {}", createdOrganization);
            return createdOrganization;
        }
        catch (OrganizationCreateException e) { throw e; }
        catch (Exception e) { throw new OrganizationCreateException(organization, e); }
    }

    /**
     * Validate the values for the given organization object.
     * @param organization The organization object to validate
     * @throws OrganizationConstraintException If the organization object has invalid values
     */
    private void validateOrganization(Organization organization) throws OrganizationConstraintException
    {
        var violations = validator.validate(organization);
        if (!violations.isEmpty())
            throw new OrganizationConstraintException(organization, violations);
    }

    private List<Organization> loggedOrganizations(List<Organization> organizations)
    {
        log.debug("Found {} organizations", organizations.size());
        if (log.isTraceEnabled()) organizations.forEach(organization -> log.trace("- {}", organization));
        return organizations;
    }
}
