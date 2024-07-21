package rize.os.access.manager.user;

import jakarta.annotation.Nonnull;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.OrganizationResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.stereotype.Service;
import rize.os.access.manager.organization.exceptions.OrganizationNotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService
{
    private final RealmResource realmResource;
    private final UserMapper userMapper;

    /**
     * Find all users in Keycloak realm
     * @param page pagination offset
     * @param size number of users for page
     * @return List of users of the given page
     */
    List<User> findAll(int page, int size)
    {
        return find("", page, size);
    }

    /**
     * Find users in Keycloak realm with search term
     * @param search search term
     * @param page pagination offset
     * @param size number of users for page
     * @return List of users of the given page
     */
    List<User> find(String search, int page, int size)
    {
        log.debug("Searching users in Keycloak with search term: '{}' [page: {}, size: {}]", search, page, size);
        var userRepresentations = realmResource.users().search(search, false, page, size);
        var users = userRepresentations.stream().map(userMapper::toUser).toList();
        return loggedUsers(users);
    }

    /**
     * Find users for organization in Keycloak realm
     * @param organizationId ID of the organization
     * @param page pagination offset
     * @param size number of users for page
     * @return List of users in the organization of the given page
     */
    List<User> findUsersForOrganization(String organizationId, int page, int size)
    {
        return findUsersForOrganization(organizationId, "", page, size);
    }

    /**
     * Find users for organization in Keycloak realm with search term
     * @param organizationId ID of the organization
     * @param search search term
     * @param page pagination offset
     * @param size number of users for page
     * @return List of users in the organization of the given page
     */
    List<User> findUsersForOrganization(String organizationId, String search, int page, int size)
    {
        log.debug("Finding users for organization with id [{}] and search term: '{}' [page: {}, size: {}]", organizationId, search, page, size);
        var userRepresentations = getOrganizationResource(organizationId).members().search(search, false, page, size);

        var users = userRepresentations.stream().map(userMapper::toUser).toList();
        return loggedUsers(users);
    }

    /**
     * Returns the number of users in the given organization
     * @param organizationId ID of the organization
     * @return Number of users in the organization
     */
    int getUserCountForOrganization(@Nonnull String organizationId)
    {
        log.debug("Determine the number of users in the organization [{}]", organizationId);
        var count = realmResource.users().count(null, null, null, null, null, null, null, "kc.org:" + organizationId);
        log.debug("Found {} users in the organization [{}]", count, organizationId);
        return count;
    }

    private OrganizationResource getOrganizationResource(String organizationId)
    {
        try
        {
            log.debug("Searching organization in Keycloak with id: {}", organizationId);
            return realmResource.organizations().get(organizationId);
        }
        catch (NotFoundException e)
        {
            throw new OrganizationNotFoundException(organizationId);
        }
    }

    private List<User> loggedUsers(List<User> users)
    {
        log.debug("Found {} users", users.size());
        if (log.isTraceEnabled()) users.forEach(user -> log.trace("- {}", user));
        return users;
    }
}
