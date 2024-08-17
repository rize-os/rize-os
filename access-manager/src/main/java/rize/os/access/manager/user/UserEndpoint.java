package rize.os.access.manager.user;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.Endpoint;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Endpoint
@AnonymousAllowed
@RequiredArgsConstructor
public class UserEndpoint
{
    private final UserService userService;

    public List<User> findUsersForOrganization(@Nonnull String organizationId, int offset, int pageSize)
    {
        return userService.findUsersForOrganization(organizationId, offset, pageSize);
    }

    public int getUserCountForOrganization(@Nonnull String organizationId)
    {
        return userService.getUserCountForOrganization(organizationId);
    }
}
