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
     * Determines wether an organization with the given name already exists.
     * @param name Name of the organization to check
     * @return {@code true} if an organization with the given name exists, {@code false} otherwise
     */
    public boolean nameExists(@Nonnull String name)
    {
        return organizationService.find(name)
                .stream().anyMatch(organization -> organization.getName().equalsIgnoreCase(name));
    }

    /**
     * Determines wether an organization with the given alias already exists.
     * @param alias Alias of the organization to check
     * @return {@code true} if an organization with the given alias exists, {@code false} otherwise
     */
    public boolean aliasExists(@Nonnull String alias)
    {
        return organizationService.findAll()
                .stream().anyMatch(organization -> organization.getAlias().equalsIgnoreCase(alias));
    }

    /**
     * Creates a new organization with the given name and alias.
     * @param name Name of the organization
     * @param alias Alias of the organization
     * @return The created organization
     */
    @Nonnull
    public Organization create(@Nonnull String name, @Nonnull String alias)
    {
        var organization = Organization.builder().name(name).alias(alias).build();
        return organizationService.createOrganization(organization);
    }
}
