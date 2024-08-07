package rize.os.access.manager.organization;

import jakarta.annotation.Nonnull;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.OrganizationDomainRepresentation;
import org.keycloak.representations.idm.OrganizationRepresentation;
import org.springframework.stereotype.Service;
import rize.os.access.manager.organization.exceptions.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class OrganizationService
{
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
     * Returns organizations from Keycloak that matches the given search term.
     * @param search Search term to filter organizations
     * @return List of organizations that matches the search term
     */
    @Nonnull
    List<Organization> find(@Nonnull String search)
    {
        log.debug("Searching organizations in Keycloak with search term: '{}'", search);
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
        return loggedOrganization(organization);
    }

    /**
     * Returns the organization from Keycloak that matches the given name.
     * @param name Name of the organization to find
     * @return The organization object if found, otherwise empty
     */
    @Nonnull
    Optional<Organization> findByName(@Nonnull String name)
    {
        var organizationRepresentation = findRepresentationByName(name);
        if (organizationRepresentation.isEmpty())
            return Optional.empty();

        var organization = organizationMapper.toOrganization(organizationRepresentation.get());
        return loggedOrganization(organization);
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
        return loggedOrganization(organization);
    }

    /**
     * Creates a new organization in Keycloak. If the organization object has no aliases set, the name of the organization will be used as a alias.
     * @param organization Object of the organization to create
     * @return The created organization object
     * @throws OrganizationConstraintException If the organization object has invalid values
     * @throws OrganizationCreateException If the organization could not be created in Keycloak
     */
    @Nonnull
    Organization createOrganization(@Nonnull Organization organization) throws OrganizationConstraintException, OrganizationCreateException
    {
        if ((organization.getAliases() == null || organization.getAliases().isEmpty()) && organization.getName() != null)
            organization.setAliases(List.of(organization.getName()));

        log.info("Creating new organization: {}", organization);
        validateOrganization(organization);
        var organizationRepresentation = createOrganizationRepresentation(organization);

        organizationRepresentation.setAttributes(organizationMapper.getAttributesForOrganization(organization));
        updateOrganizationRepresentation(organizationRepresentation);

        var createdOrganization = organizationMapper.toOrganization(organizationRepresentation);
        log.info("Created organization successfully: {}", createdOrganization);
        return createdOrganization;
    }

    /**
     * Updates the organization in Keycloak that matches the given organization object.
     * @param organization Object of the organization to update
     * @return The updated organization object
     * @throws OrganizationConstraintException If the organization object has invalid values
     * @throws OrganizationUpdateException If the organization could not be updated in Keycloak
     * @throws OrganizationNotFoundException If the organization could not be found
     */
    @Nonnull
    Organization updateOrganization(@Nonnull Organization organization) throws OrganizationConstraintException, OrganizationUpdateException, OrganizationNotFoundException
    {
        log.info("Updating organization with ID [{}] to: {}", organization.getId(), organization);
        var organizationRepresentation = findRepresentationById(organization.getId()).orElseThrow(() -> new OrganizationNotFoundException(organization.getId()));

        validateOrganization(organization);
        organizationRepresentation.setName(organization.getName());
        organizationRepresentation.setDescription(organization.getDescription());
        organizationRepresentation.setEnabled(organization.isEnabled());
        organizationRepresentation.setAttributes(organizationMapper.getAttributesForOrganization(organization));
        updateOrganizationRepresentation(organizationRepresentation);

        var updatedOrganization = findById(organization.getId()).orElseThrow(() -> new OrganizationNotFoundException(organization.getId()));
        log.info("Updated organization successfully: {}", updatedOrganization);
        return updatedOrganization;
    }

    /**
     * Deletes the organization in Keycloak that matches the given id.
     * @param id ID of the organization to delete
     * @throws OrganizationNotFoundException If the organization with the given ID could not be found
     * @throws OrganizationDeleteException If the organization could not be deleted in Keycloak
     */
    void deleteOrganization(@Nonnull String id) throws OrganizationNotFoundException, OrganizationDeleteException
    {
        var organization = findById(id).orElseThrow(() -> new OrganizationNotFoundException(id));
        deleteOrganization(organization);
    }


    private void deleteOrganization(@Nonnull Organization organization)
    {
        log.info("Deleting organization: {}", organization);

        try (var response = realmResource.organizations().get(organization.getId()).delete())
        {
            System.out.println(response.getStatus());
            if (response.getStatus() != 204)
                throw new OrganizationDeleteException(organization, response);
        }
        catch (OrganizationDeleteException e) { throw e; }
        catch (Exception e) { throw new OrganizationDeleteException(organization, e); }

        log.info("Deleted organization successfully: {}", organization);
    }

    private Optional<OrganizationRepresentation> findRepresentationById(String id)
    {
        try
        {
            log.debug("Searching for organization in Keycloak with ID [{}]", id);
            return Optional.of(realmResource.organizations().get(id).toRepresentation());
        }
        catch (NotFoundException e)
        {
            log.warn("Organization with id [{}] not found in Keycloak", id);
            return Optional.empty();
        }
    }

    private Optional<OrganizationRepresentation> findRepresentationByName(String name)
    {
        try
        {
            log.debug("Searching organization in Keycloak with name: '{}'", name);
            var organizations = realmResource.organizations().search(name, true, 0, 1);
            var organizationWithSameName = organizations.stream()
                    .filter(organization -> organization.getName().equals(name))
                    .findFirst();

            if (organizationWithSameName.isEmpty())
            {
                log.debug("Organization with name '{}' not found in Keycloak", name);
                return Optional.empty();
            }

            log.debug("Found organization with name '{}': {}", name, organizationWithSameName.get().getName());
            return organizationWithSameName;
        }
        catch (NotFoundException e)
        {
            log.warn("Organization with name '{}' not found in Keycloak", name);
            return Optional.empty();
        }
    }

    private Optional<OrganizationRepresentation> findRepresentationByAlias(String alias)
    {
        try
        {
            log.debug("Searching organization in Keycloak with attribute '{}' containing value: '{}'", Organization.ALIASES_ATTRIBUTE, alias);
            var organizations = realmResource.organizations().searchByAttribute(Organization.ALIASES_ATTRIBUTE + ":" + alias);
            var organizationWithSameAlias = organizations.stream()
                    .filter(organization -> organization.getAttributes().get(Organization.ALIASES_ATTRIBUTE).contains(alias))
                    .findFirst();

            if (organizationWithSameAlias.isEmpty())
            {
                log.debug("Organization with alias '{}' not found in Keycloak", alias);
                return Optional.empty();
            }

            log.debug("Found organization with alias '{}': {}", alias, organizationWithSameAlias.get().getName());
            return organizationWithSameAlias;
        }
        catch (NotFoundException e)
        {
            log.warn("Organization with alias {} not found in Keycloak", alias);
            return Optional.empty();
        }
    }

    private OrganizationRepresentation createOrganizationRepresentation(Organization organization) throws OrganizationCreateException
    {
        log.debug("Create organization with name '{}' in Keycloak", organization.getName());
        OrganizationRepresentation organizationRepresentation = new OrganizationRepresentation();
        organizationRepresentation.setName(organization.getName());
        organizationRepresentation.setDescription(organization.getDescription());
        organizationRepresentation.addDomain(new OrganizationDomainRepresentation(organization.getName()));

        try (var response = realmResource.organizations().create(organizationRepresentation))
        {
            if (response.getStatus() != 201)
                throw new OrganizationCreateException(organization, response);

            var createdOrganizationRepresentation = realmResource.organizations().search(organization.getName(), true, 0, 1).getFirst();
            log.debug("Created organization in Keycloak successfully: ID [{}]", createdOrganizationRepresentation.getId());
            return createdOrganizationRepresentation;
        }
        catch (OrganizationCreateException e) { throw e; }
        catch (Exception e) { throw new OrganizationCreateException(organization, e); }
    }

    private void updateOrganizationRepresentation(OrganizationRepresentation organizationRepresentation)
    {
        log.debug("Update organization '{}' with ID [{}] in Keycloak", organizationRepresentation.getName(), organizationRepresentation.getId());
        try (var response = realmResource.organizations().get(organizationRepresentation.getId()).update(organizationRepresentation))
        {
            if (response.getStatus() != 204)
                throw new OrganizationUpdateException(organizationMapper.toOrganization(organizationRepresentation), response);
        }
        catch (OrganizationUpdateException e) { throw e; }
        catch (Exception e) { throw new OrganizationUpdateException(organizationMapper.toOrganization(organizationRepresentation), e); }

        log.debug("Updated organization '{}' successfully", organizationRepresentation.getName());
    }

    private void validateOrganization(Organization organization) throws OrganizationConstraintException, OrganizationCreateException
    {
        var violations = organization.validate();
        if (!violations.isEmpty())
            throw new OrganizationConstraintException(organization, violations);

        var orgWithName = findByName(organization.getName());
        if (orgWithName.isPresent() && !orgWithName.get().getId().equals(organization.getId()))
            throw new OrganizationCreateException(organization, "Name \"" + organization.getName() + "\" already exists");

        for (var alias : organization.getAliases())
        {
            var orgWithAlias = findByAlias(alias);
            if (orgWithAlias.isPresent() && !orgWithAlias.get().getId().equals(organization.getId()))
                throw new OrganizationCreateException(organization, "Alias \"" + alias + "\" already exists");
        }
    }

    private List<Organization> loggedOrganizations(List<Organization> organizations)
    {
        log.debug("Found {} organizations", organizations.size());
        if (log.isTraceEnabled()) organizations.forEach(organization -> log.trace("- {}", organization));
        return organizations;
    }

    private Optional<Organization> loggedOrganization(Organization organization)
    {
        log.debug("Found organization: {}", organization);
        return Optional.of(organization);
    }
}
