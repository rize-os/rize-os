package rize.os.access.manager.user;

import jakarta.annotation.Nonnull;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.OrganizationResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.stereotype.Service;
import rize.os.access.manager.organization.exceptions.OrganizationNotFoundException;
import rize.os.access.manager.usersession.UserSession;
import rize.os.access.manager.usersession.UserSessionService;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService
{
    private final RealmResource realmResource;
    private final UserSessionService userSessionService;
    private final UserMapper userMapper;

    /**
     * Find all users in Keycloak realm
     * @param offset pagination offset
     * @param size number of users for page
     * @return List of users of the given page
     */
    List<User> findAll(int offset, int size)
    {
        return find("", offset, size);
    }

    /**
     * Find users in Keycloak realm with search term
     * @param search search term
     * @param offset pagination offset
     * @param size number of users for page
     * @return List of users of the given page
     */
    List<User> find(String search, int offset, int size)
    {
        log.debug("Searching users in Keycloak with search term: '{}' [offset: {}, size: {}]", search, offset, size);
        var userRepresentations = realmResource.users().search(search, false, offset, size);
        var users = userRepresentations.stream().map(userMapper::toUser).toList();
        return loggedUsers(users);
    }

    /**
     * Find users for organization in Keycloak realm
     * @param organizationId ID of the organization
     * @param offset pagination offset
     * @param size number of users for page
     * @return List of users in the organization of the given page
     */
    List<User> findUsersForOrganization(String organizationId, int offset, int size)
    {
        return findUsersForOrganization(organizationId, "", offset, size);
    }

    /**
     * Find users for organization in Keycloak realm with search term
     * @param organizationId ID of the organization
     * @param search search term
     * @param offset pagination offset
     * @param size number of users for page
     * @return List of users in the organization of the given page
     */
    List<User> findUsersForOrganization(String organizationId, String search, int offset, int size)
    {
        log.debug("Finding users for organization with id [{}] and search term: '{}' [offset: {}, size: {}]", organizationId, search, offset, size);
        var userRepresentations = getOrganizationResource(organizationId).members().search(search, false, offset, size);

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

    private void addSessionInformationToUsers(List<User> users)
    {
        users.forEach(this::addSessionInformationToUser);
    }

    private void addSessionInformationToUser(User user)
    {
        var sessions = userSessionService.findByUser(user.getId());
        user.setIsOnline(!sessions.isEmpty());
        user.setOnlineSince(sessions.stream()
                .map(UserSession::getStartedAt)
                .min(Comparator.naturalOrder())
                .orElse(null));
    }

    private List<User> loggedUsers(List<User> users)
    {
        addSessionInformationToUsers(users);
        log.debug("Found {} users", users.size());
        if (log.isTraceEnabled()) users.forEach(user -> log.trace("- {}", user));
        return users;
    }
}
