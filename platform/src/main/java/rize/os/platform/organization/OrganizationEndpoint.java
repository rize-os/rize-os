package rize.os.platform.organization;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.Endpoint;
import com.vaadin.hilla.exception.EndpointException;
import lombok.RequiredArgsConstructor;
import rize.os.commons.organization.OrganizationDto;

import java.util.List;

@Endpoint
@AnonymousAllowed
@RequiredArgsConstructor
public class OrganizationEndpoint
{
    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;

    /**
     * Returns a list of all organizations.
     * @return List of organizations
     */
    public List<OrganizationDto> findAll()
    {
        try
        {
            var organizations = organizationService.findAll();
            return organizations.stream().map(organizationMapper::toOrganizationDto).toList();
        }
        catch (Exception e) {
            throw new EndpointException(e.getMessage(), e);
        }
    }

    /**
     * Returns a list of organizations in the given region.
     * @param region Name of the region
     * @return List of organizations in the region
     */
    public List<OrganizationDto> findByRegion(String region)
    {
        try
        {
            var organizations = organizationService.findOrganizationsByRegion(region);
            return organizations.stream().map(organizationMapper::toOrganizationDto).toList();
        }
        catch (Exception e) {
            throw new EndpointException(e.getMessage(), e);
        }
    }

    /**
     * Returns a list of organizations by the given search term that is part of the name or display name.
     * @param searchTerm Search term
     * @return List of organizations
     */
    public List<OrganizationDto> findBySearchTerm(String searchTerm)
    {
        try
        {
            var organizations = organizationService.findOrganizationBySearchTerm(searchTerm);
            return organizations.stream().map(organizationMapper::toOrganizationDto).toList();
        }
        catch (Exception e) {
            throw new EndpointException(e.getMessage(), e);
        }
    }

    /**
     * Creates a new organization.
     * @param organizationDto Organization to create
     * @return The created organization
     */
    public OrganizationDto create(OrganizationDto organizationDto)
    {
        try
        {
            var organization = organizationMapper.toOrganization(organizationDto);
            var createdOrganization = organizationService.createOrganization(organization);
            return organizationMapper.toOrganizationDto(createdOrganization);
        }
        catch (OrganizationConstraintException e) {
            throw new EndpointException(e.getMessage(), e, e.getViolations().stream().map(v -> v.getPropertyPath() + " " + v.getMessage()).toList());
        }
        catch (Exception e) {
            throw new EndpointException(e.getMessage(), e);
        }
    }

    /**
     * Updates an organization.
     * @param organizationDto Organization to update
     * @return The updated organization
     */
    public OrganizationDto update(OrganizationDto organizationDto)
    {
        try
        {
            var organization = organizationMapper.toOrganization(organizationDto);
            var updatedOrganization = organizationService.updateOrganization(organization);
            return organizationMapper.toOrganizationDto(updatedOrganization);
        }
        catch (OrganizationConstraintException e) {
            throw new EndpointException(e.getMessage(), e, e.getViolations().stream().map(v -> v.getPropertyPath() + " " + v.getMessage()).toList());
        }
        catch (Exception e) {
            throw new EndpointException(e.getMessage(), e);
        }
    }

    /**
     * Deletes an organization.
     * @param id ID of the organization to delete
     */
    public void delete(String id)
    {
        try {
            organizationService.deleteOrganization(id);
        }
        catch (Exception e) {
            throw new EndpointException(e.getMessage(), e);
        }
    }
}
