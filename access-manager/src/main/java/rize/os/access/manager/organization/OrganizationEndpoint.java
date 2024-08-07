package rize.os.access.manager.organization;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.Endpoint;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Endpoint
@AnonymousAllowed
@RequiredArgsConstructor
public class OrganizationEndpoint
{
    private final OrganizationService organizationService;

    /**
     * Returns all organizations
     * @return List of organizations
     */
    @Nonnull
    public List<Organization> findAll()
    {
        return organizationService.findAll();
    }

    /**
     * Returns a list of organizations that matches the given search term
     * @param search Search term to filter organizations
     * @return List of organizations that matches the search term
     */
    @Nonnull
    public List<Organization> find(@Nonnull String search)
    {
        return organizationService.find(search);
    }

    /**
     * Determines if an organization with the given name already exists.
     * @param name Name of the organization to check
     * @return {@code true} if an organization with the given name exists, {@code false} otherwise
     */
    public boolean nameExists(@Nonnull String name)
    {
        return organizationService.findByName(name).isPresent();
    }

    /**
     * Creates a new organization with the given display name and name
     * @param displayName Display name of the organization
     * @param name Name of the organization
     * @return The created organization
     */
    @Nonnull
    public Organization create(@Nonnull String displayName, @Nonnull String name)
    {
        var organization = Organization.builder().displayName(displayName).name(name).build();
        return organizationService.createOrganization(organization);
    }

    /**
     * Updates the given organization
     * @param organization Organization to update
     * @return The updated organization
     */
    @Nonnull
    public Organization update(@Nonnull Organization organization)
    {
        return organizationService.updateOrganization(organization);
    }
}
