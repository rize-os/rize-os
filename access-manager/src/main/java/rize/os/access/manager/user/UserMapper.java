package rize.os.access.manager.user;

import jakarta.annotation.Nonnull;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper
{
    @Nonnull
    User toUser(@Nonnull UserRepresentation userRepresentation)
    {
        return User.builder()
                .id(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .firstName(userRepresentation.getFirstName() == null ? "" : userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName() == null ? "" : userRepresentation.getLastName())
                .email(userRepresentation.getEmail() == null ? "" : userRepresentation.getEmail())
                .emailVerified(userRepresentation.isEmailVerified())
                .enabled(userRepresentation.isEnabled())
                .organizationIds(getOrganizationIdsForUser(userRepresentation))
                .build();
    }

    private List<String> getOrganizationIdsForUser(UserRepresentation userRepresentation)
    {
        var attributes = userRepresentation.getAttributes();
        if (attributes.containsKey("kc.org"))
            return attributes.get("kc.org");
        return List.of();
    }
}
