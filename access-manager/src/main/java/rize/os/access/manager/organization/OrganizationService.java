package rize.os.access.manager.organization;

import jakarta.annotation.Nonnull;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.OrganizationRepresentation;
import org.springframework.stereotype.Service;
import rize.os.access.manager.organization.exceptions.OrganizationConstraintException;
import rize.os.access.manager.organization.exceptions.OrganizationCreateException;
import rize.os.access.manager.organization.exceptions.OrganizationNotFoundException;
import rize.os.access.manager.organization.exceptions.OrganizationUpdateException;

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
        var organizationRepresentation = findRepresentationById(id);
        if (organizationRepresentation.isEmpty())
            return Optional.empty();

        var organization = organizationMapper.toOrganization(organizationRepresentation.get());
        log.debug("Found organization: {}", organization);
        return Optional.of(organization);
    }

    /**
     * Returns the organization from Keycloak that matches the given alias.
     * @param alias Alias of the organization to find
     * @return The organization object if found, otherwise empty
     */
    @Nonnull
    Optional<Organization> findByAlias(@Nonnull String alias)
    {
        var organizationRepresentation = findRepresentationByAlias(alias);
        if (organizationRepresentation.isEmpty())
            return Optional.empty();

        var organization = organizationMapper.toOrganization(organizationRepresentation.get());
        log.debug("Found organization: {}", organization);
        return Optional.of(organization);
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
     * Updates the name of the organization in Keycloak that matches the given id.
     * @param id ID of the organization to update
     * @param name New name of the organization
     * @return The updated organization object
     */
    @Nonnull
    Organization updateName(@Nonnull String id, @Nonnull String name)
    {
        OrganizationRepresentation organizationRepresentation = findRepresentationById(id)
                .orElseThrow(() -> new OrganizationNotFoundException(id));

        log.info("Updating organization name with id [{}] to: {}", id, name);
        organizationRepresentation.setName(name);

        try (var response = realmResource.organizations().get(id).update(organizationRepresentation))
        {
            if (response.getStatus() != 204)
                throw new OrganizationUpdateException(organizationMapper.toOrganization(organizationRepresentation), "name", response);
        }
        catch (OrganizationUpdateException e) { throw e; }
        catch (Exception e) { throw new OrganizationUpdateException(organizationMapper.toOrganization(organizationRepresentation), "name", e); }

        log.info("Updated organization alias of organization [{}] successfully: {}", organizationRepresentation.getId(), name);
        return findById(id).orElseThrow(() -> new OrganizationNotFoundException(id));
    }

    /**
     * Updates the alias of the organization in Keycloak that matches the given id.
     * @param id ID of the organization to update
     * @param alias New alias of the organization
     * @return The updated organization object
     */
    @Nonnull
    Organization updateAlias(@Nonnull String id, @Nonnull String alias)
    {
        OrganizationRepresentation organizationRepresentation = findRepresentationById(id)
                .orElseThrow(() -> new OrganizationNotFoundException(id));

        log.info("Updating organization alias with id [{}] to: {}", id, alias);
        organizationRepresentation.setDescription(alias);

        if (findRepresentationByAlias(alias).isPresent())
            throw new OrganizationUpdateException(organizationMapper.toOrganization(organizationRepresentation), "alias", new Exception("Alias \"" + alias + "\" already exists"));

        try (var response = realmResource.organizations().get(id).update(organizationRepresentation))
        {
            if (response.getStatus() != 204)
                throw new OrganizationUpdateException(organizationMapper.toOrganization(organizationRepresentation), "alias", response);
        }
        catch (OrganizationUpdateException e) { throw e; }
        catch (Exception e) { throw new OrganizationUpdateException(organizationMapper.toOrganization(organizationRepresentation), "alias", e); }

        log.info("Updated organization alias of organization {} successfully: {}", organizationRepresentation.getName(), alias);
        return findById(id).orElseThrow(() -> new OrganizationNotFoundException(id));
    }

    private Optional<OrganizationRepresentation> findRepresentationById(String id)
    {
        try
        {
            log.debug("Searching organization in Keycloak with id: {}", id);
            return Optional.of(realmResource.organizations().get(id).toRepresentation());
        }
        catch (NotFoundException e)
        {
            log.warn("Organization with id {} not found in Keycloak", id);
            return Optional.empty();
        }
    }

    private Optional<OrganizationRepresentation> findRepresentationByAlias(String alias)
    {
         log.debug("Searching organization in Keycloak with alias: {}", alias);
         var organizations = realmResource.organizations().search(alias, true, 0, 1);
         var organizationWithSameAlias = organizations.stream().filter(organization -> organization.getDescription().equals(alias)).findFirst();

         if (organizationWithSameAlias.isEmpty())
         {
             log.debug("Organization with alias {} not found in Keycloak", alias);
             return Optional.empty();
         }

         log.debug("Found organization with alias {}: {}", alias, organizationWithSameAlias.get().getName());
         return organizationWithSameAlias;
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
