package rize.os.access.manager.organization;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.Endpoint;
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
    public List<Organization> findAll()
    {
        return organizationService.findAll();
    }

    /**
     * Returns a list of organizations that matches the given search term
     * @param search Search term to filter organizations
     * @return List of organizations that matches the search term
     */
    public List<Organization> find(String search)
    {
        return organizationService.find(search);
    }
}
