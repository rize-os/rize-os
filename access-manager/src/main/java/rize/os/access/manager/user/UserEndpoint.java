package rize.os.access.manager.user;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.Endpoint;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNullApi;

import java.util.List;

@Endpoint
@AnonymousAllowed
@RequiredArgsConstructor
public class UserEndpoint
{
    private final UserService userService;

    public List<User> findUsersForOrganization(@Nonnull String organizationId, int page, int pageSize)
    {
        return userService.findUsersForOrganization(organizationId, page, pageSize);
    }

    public int getUserCountForOrganization(@Nonnull String organizationId)
    {
        return userService.getUserCountForOrganization(organizationId);
    }
}
