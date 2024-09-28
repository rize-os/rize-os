package rize.os.platform.organization;

import jakarta.annotation.Nullable;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.OrganizationDomainRepresentation;
import org.keycloak.representations.idm.OrganizationRepresentation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService
{
    private final OrganizationMapper organizationMapper;
    private final RealmResource realmResource;
    private final ApplicationEventPublisher eventPublisher;


    /**
     * Returns a list of all organizations from Keycloak.
     * @return List of all organizations
     */
    List<Organization> findAll()
    {
        log.debug("Loading all organizations from Keycloak");
        var orgRepresentations = realmResource.organizations().search("", false, 0, Integer.MAX_VALUE);
        var organizations = orgRepresentations.stream().map(organizationMapper::toOrganization).toList();
        return loggedOrganizations(organizations);
    }

    /**
     * Returns a list of all organizations from Keycloak in the given region.
     * @param region The name of region to search for
     * @return List of all organizations in the given region
     */
    List<Organization> findOrganizationsByRegion(String region)
    {
        log.debug("Loading all organizations from Keycloak in region '{}'", region);
        var orgRepresentations = findByAttribute(Organization.REGION_ATTRIBUTE, region);
        var organizations = orgRepresentations.stream().map(organizationMapper::toOrganization).toList();
        return loggedOrganizations(organizations);
    }

    /**
     * Searches for an existing organization in Keycloak with the given ID.
     * @param id The ID of the organization to search for
     * @return Object of the organization if found, otherwise empty
     */
    Optional<Organization> findOrganizationById(String id)
    {
        var orgRepresentation = findOrganizationRepresentationById(id);
        if (orgRepresentation.isEmpty())
            return Optional.empty();

        var organization = organizationMapper.toOrganization(orgRepresentation.get());
        return loggedOrganization(organization);
    }

    /**
     * Searches for an existing organization in Keycloak with the given name.
     * @param name The name of the organization to search for
     * @return Object of the organization if found, otherwise empty
     */
    Optional<Organization> findOrganizationByName(String name)
    {
        var orgRepresentation = findOrganizationRepresentationByName(name);
        if (orgRepresentation.isEmpty())
            return Optional.empty();

        var organization = organizationMapper.toOrganization(orgRepresentation.get());
        return loggedOrganization(organization);
    }

    /**
     * Creates a new organization in Keycloak.
     * @param organization Object of the organization to create
     * @return Object of the created organization with an ID
     * @throws OrganizationConstraintException If the given organization has invalid values
     * @throws OrganizationAlreadyExistsException If an organization with the same name already exists
     * @throws OrganizationCreateException If the organization could not be created in Keycloak
     */
    @Transactional
    Organization createOrganization(Organization organization) throws OrganizationConstraintException, OrganizationAlreadyExistsException, OrganizationCreateException
    {
        log.info("Creating new organization in Keycloak: {}", organization);
        validateOrganization(organization);

        var orgRepresentation = createOrganizationRepresentation(organization);
        orgRepresentation.setAttributes(organizationMapper.toRepresentationAttributes(organization));

        try { updateOrganizationRepresentation(orgRepresentation); }
        catch (OrganizationUpdateException e)
        {
            try { deleteOrganization(orgRepresentation.getId()); }
            catch (Exception ignore) {}

            if (e.hasResponse())
                throw new OrganizationCreateException(organization, e.getResponse());
            throw new OrganizationCreateException(organization, e.getCause());
        }

        var createdOrganization = organizationMapper.toOrganization(orgRepresentation);
        log.info("Created organization successfully: {}", createdOrganization);
        eventPublisher.publishEvent(new OrganizationCreatedEvent(createdOrganization));
        return createdOrganization;
    }

    /**
     * Updates the given organization in Keycloak.
     * @param organization The organization to update with new values
     * @return The updated organization
     * @throws OrganizationNotFoundException If the organization with the given ID could not be found in Keycloak
     * @throws OrganizationConstraintException If the new values of the organization are invalid
     * @throws OrganizationAlreadyExistsException If the new name of the organization already exists
     * @throws OrganizationUpdateException If the organization could not be updated in Keycloak
     */
    @Transactional
    Organization updateOrganization(Organization organization)
            throws OrganizationNotFoundException, OrganizationConstraintException, OrganizationAlreadyExistsException, OrganizationUpdateException
    {
        log.info("Updating organization with ID [{}] in Keycloak to: {}", organization.getId(), organization);
        var orgRepresentationToUpdate = findOrganizationRepresentationById(organization.getId())
                .orElseThrow(() -> new OrganizationNotFoundException("id=" + organization.getId()));
        validateOrganization(organization);
        var organizationBefore = organizationMapper.toOrganization(orgRepresentationToUpdate);

        orgRepresentationToUpdate.setName(organization.getName());
        orgRepresentationToUpdate.setDescription(organization.getDescription());
        orgRepresentationToUpdate.setEnabled(organization.isEnabled());
        orgRepresentationToUpdate.setAttributes(organizationMapper.toRepresentationAttributes(organization));
        updateOrganizationRepresentation(orgRepresentationToUpdate);

        var updatedOrganization = organizationMapper.toOrganization(orgRepresentationToUpdate);
        log.info("Updated organization successfully: {}", updatedOrganization);
        eventPublisher.publishEvent(new OrganizationUpdatedEvent(organizationBefore, updatedOrganization));
        return updatedOrganization;
    }

    /**
     * Deletes the organization with the given ID from Keycloak.
     * @param id The ID of the organization to delete
     * @throws OrganizationNotFoundException If the organization with the given ID could not be found in Keycloak
     * @throws OrganizationDeleteException If the organization could not be deleted in Keycloak
     */
    @Transactional
    void deleteOrganization(String id) throws OrganizationNotFoundException, OrganizationDeleteException
    {
        var organization = findOrganizationById(id).orElseThrow(() -> new OrganizationNotFoundException("id=" + id));
        deleteOrganization(organization);
    }

    /**
     * Deletes the given organization from Keycloak.
     * @param organization The organization to delete
     * @throws OrganizationNotFoundException If the organization with the given ID could not be found in Keycloak
     * @throws OrganizationDeleteException If the organization could not be deleted in Keycloak
     */
    @Transactional
    void deleteOrganization(Organization organization) throws OrganizationNotFoundException, OrganizationDeleteException
    {
        log.info("Deleting organization '{}' from Keycloak", organization.getName());

        try (var response = realmResource.organizations().get(organization.getId()).delete())
        {
            if (response.getStatus() == 404)
                throw new OrganizationNotFoundException("id=" + organization.getId());
            if (response.getStatus() != 204)
                throw new OrganizationDeleteException(organization, response);
        }
        catch (OrganizationNotFoundException | OrganizationDeleteException e) { throw e; }
        catch (Exception e) { throw new OrganizationDeleteException(organization, e); }

        log.info("Deleted organization successfully: {}", organization);
        eventPublisher.publishEvent(new OrganizationDeletedEvent(organization));
    }

    /**
     * Creates the given Organization in Keycloak. The creation in Keycloak will only set the name and description of the organization.
     * @param organization The organization to create
     * @return The representation of the created organization in Keycloak
     * @throws OrganizationCreateException If the organization could not be created in Keycloak
     */
    private OrganizationRepresentation createOrganizationRepresentation(Organization organization) throws OrganizationCreateException
    {
        log.debug("Creating organization with name '{}' in Keycloak", organization.getName());
        var orgRepresentation = new OrganizationRepresentation();
        orgRepresentation.setName(organization.getName());
        orgRepresentation.setEnabled(organization.isEnabled());
        orgRepresentation.setDescription(organization.getDescription());
        orgRepresentation.addDomain(new OrganizationDomainRepresentation(organization.getName()));

        try (var response = realmResource.organizations().create(orgRepresentation))
        {
            if (response.getStatus() != 201)
                throw new OrganizationCreateException(organization, response);

            var createdOrgRepresentation = realmResource.organizations().search(organization.getName(), true, 0, 1).getFirst();
            log.debug("Created organization '{}', in Keycloak successfully: ID [{}]", createdOrgRepresentation.getName(), createdOrgRepresentation.getId());
            return createdOrgRepresentation;
        }
        catch (OrganizationCreateException e) { throw e; }
        catch (Exception e) { throw new OrganizationCreateException(organization, e); }
    }

    /**
     * Updates the given organization representation in Keycloak. The update will contain all values including attributes of the organization.
     * @param organizationRepresentation The representation organization to update in Keycloak
     * @throws OrganizationUpdateException If the organization could not be updated in Keycloak
     */
    private void updateOrganizationRepresentation(OrganizationRepresentation organizationRepresentation) throws OrganizationUpdateException
    {
        log.debug("Update values for organization '{}' with ID [{}] in Keycloak", organizationRepresentation.getName(), organizationRepresentation.getId());
        log.debug(" - Attributes: {}", organizationRepresentation.getAttributes());

        try (var response = realmResource.organizations().get(organizationRepresentation.getId()).update(organizationRepresentation))
        {
            if (response.getStatus() != 204)
                throw new OrganizationUpdateException(organizationMapper.toOrganization(organizationRepresentation), response);
        }
        catch (OrganizationUpdateException e) { throw e; }
        catch (Exception e) { throw new OrganizationUpdateException(organizationMapper.toOrganization(organizationRepresentation), e); }

        log.debug("Updated organization '{}' in Keycloak successfully", organizationRepresentation.getName());
    }

    /**
     * Searches for an existing organization in Keycloak with the given ID.
     * @param id The ID of the organization to search for
     * @return The representation of the organization if found, otherwise empty
     */
    private Optional<OrganizationRepresentation> findOrganizationRepresentationById(String id)
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

    /**
     * Searches for an existing organization in Keycloak with the given name.
     * @param name The name of the organization to search for
     * @return The representation of the organization if found, otherwise empty
     */
    private Optional<OrganizationRepresentation> findOrganizationRepresentationByName(@Nullable String name)
    {
        log.debug("Searching for organization with name '{}' in Keycloak", name);
        var organizations = realmResource.organizations().search(name == null ? "" : name, true, 0, 1);

        if (organizations.isEmpty())
        {
            log.debug("Organization with name '{}' not found in Keycloak", name);
            return Optional.empty();
        }

        log.debug("Found organization with name '{}' in Keycloak: ID [{}]", name, organizations.getFirst().getId());
        return Optional.of(organizations.getFirst());
    }

    /**
     * Searches for organizations in Keycloak with the given attribute and value.
     * @param attribute The attribute to search for
     * @param value The value of the attribute to search for
     * @return List of representations of organizations with the given attribute and value
     */
    private List<OrganizationRepresentation> findByAttribute(String attribute, String value)
    {
        log.debug("Searching for organizations in Keycloak with attribute '{}' = '{}'", attribute, value);
        return realmResource.organizations().searchByAttribute(attribute + ":" + value, 0, Integer.MAX_VALUE);
    }

    /**
     * Validates the values of the given organization and checks if an organization with the same name already exists.
     * @param organization The organization to validate
     * @throws OrganizationConstraintException If the organization has invalid values
     * @throws OrganizationAlreadyExistsException If an organization with the same name already exists
     */
    private void validateOrganization(Organization organization) throws OrganizationConstraintException, OrganizationAlreadyExistsException
    {
        var violations = organization.validate();
        if (!violations.isEmpty())
            throw new OrganizationConstraintException(organization, violations);

        var organizationWithSameName = findOrganizationRepresentationByName(organization.getName());
        if (organizationWithSameName.isPresent() && !organizationWithSameName.get().getId().equals(organization.getId()))
            throw new OrganizationAlreadyExistsException(organization);
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
