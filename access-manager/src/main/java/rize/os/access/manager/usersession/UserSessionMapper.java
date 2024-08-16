package rize.os.access.manager.usersession;

import jakarta.annotation.Nonnull;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class UserSessionMapper
{
    @Nonnull
    UserSession toUserSession(@Nonnull UserSessionRepresentation userSessionRepresentation)
    {
        return UserSession.builder()
                .id(userSessionRepresentation.getId())
                .userId(userSessionRepresentation.getUserId())
                .ipAddress(userSessionRepresentation.getIpAddress())
                .startedAt(millisToLocalDateTime(userSessionRepresentation.getStart()))
                .lastAccessAt(millisToLocalDateTime(userSessionRepresentation.getLastAccess()))
                .clients(userSessionRepresentation.getClients().values().stream().toList())
                .build();
    }

    private LocalDateTime millisToLocalDateTime(long millis)
    {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
