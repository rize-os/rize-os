package rize.os.access.manager.usersession;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.stereotype.Service;
import rize.os.access.manager.user.exceptions.UserNotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionService
{
    private final RealmResource realmResource;
    private final UserSessionMapper userSessionMapper;

    /**
     * Find all user sessions in Keycloak realm
     * @param userId ID of the user
     * @return List of user sessions
     * @throws UserNotFoundException if user with given ID is not found
     */
    public List<UserSession> findByUser(String userId) throws UserNotFoundException
    {
        try
        {
            log.debug("Finding sessions for user with id [{}]", userId);
            var userSessionRepresentations = realmResource.users().get(userId).getUserSessions();
            var userSessions = userSessionRepresentations.stream().map(userSessionMapper::toUserSession).toList();
            return loggedUserSessions(userSessions);
        }
        catch (NotFoundException e)
        {
            log.debug("Could not find user with id [{}]", userId);
            throw new UserNotFoundException(userId);
        }
    }

    private List<UserSession> loggedUserSessions(List<UserSession> userSessions)
    {
        log.debug("Found {} user sessions", userSessions.size());
        if (log.isTraceEnabled()) userSessions.forEach(userSession -> log.trace("- {}", userSession));
        return userSessions;
    }
}
