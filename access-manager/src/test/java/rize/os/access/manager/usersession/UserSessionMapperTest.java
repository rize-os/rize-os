package rize.os.access.manager.usersession;

import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserSessionRepresentation;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserSessionMapperTest
{
    private final UserSessionMapper userSessionMapper = new UserSessionMapper();

    @Test
    void shouldMapUserSessionRepresentationToUserSession()
    {
        // Given
        var userSessionRepresentation = new UserSessionRepresentation();
        userSessionRepresentation.setId(UUID.randomUUID().toString());
        userSessionRepresentation.setUserId(UUID.randomUUID().toString());
        userSessionRepresentation.setIpAddress("127.0.0.1");
        userSessionRepresentation.setClients(Map.of(UUID.randomUUID().toString(), "first-client", UUID.randomUUID().toString(), "second-client"));
        userSessionRepresentation.setStart(946681200000L); // 2000-01-01 00:00:00
        userSessionRepresentation.setLastAccess(946767600000L); // 2000-01-02 00:00:00

        // When
        var userSession = userSessionMapper.toUserSession(userSessionRepresentation);

        // Then
        assertThat(userSession.getId()).isEqualTo(userSessionRepresentation.getId());
        assertThat(userSession.getUserId()).isEqualTo(userSessionRepresentation.getUserId());
        assertThat(userSession.getIpAddress()).isEqualTo(userSessionRepresentation.getIpAddress());
        assertThat(userSession.getClients()).hasSize(2);
        assertThat(userSession.getClients()).contains("first-client", "second-client");
        assertThat(userSession.getStartedAt()).isEqualTo("2000-01-01T00:00:00");
        assertThat(userSession.getLastAccessAt()).isEqualTo("2000-01-02T00:00:00");
    }
}