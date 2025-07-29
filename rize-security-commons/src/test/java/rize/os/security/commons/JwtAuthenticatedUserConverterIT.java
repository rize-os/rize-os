package rize.os.security.commons;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {JwtAuthenticatedUserConverter.class, AuthenticatedUserClaimsConfiguration.class})
class JwtAuthenticatedUserConverterIT
{
    @Autowired
    private JwtAuthenticatedUserConverter converter;

    @Test
    @DisplayName("[JwtAuthenticatedUserConverter] should convert JWT to AuthenticatedUser")
    void shouldConvertJwtToAuthenticatedUser()
    {
        final String clientId = "rize-app";
        final String sessionId = UUID.randomUUID().toString();
        final String userId = UUID.randomUUID().toString();

        Map<String, Object> claims = Map.of(
                "sub", userId,
                "preferred_username", "testuser",
                "email", "test@rize.os",
                "email_verified", true,
                "given_name", "Test",
                "family_name", "User",
                "name", "Test User",
                "sid", sessionId,
                "azp", clientId,
                "resource_access", Map.of(clientId, Map.of("roles", List.of("admin", "viewer"))
                )
        );
        Jwt jwt = createJwt(claims);

        AuthenticatedUser authenticatedUser = converter.convert(jwt);

        assertThat(authenticatedUser).isNotNull();
        assertThat(authenticatedUser.getUserId()).isEqualTo(userId);
        assertThat(authenticatedUser.getUsername()).isEqualTo("testuser");
        assertThat(authenticatedUser.getEmail()).isEqualTo("test@rize.os");
        assertThat(authenticatedUser.isEmailVerified()).isTrue();
        assertThat(authenticatedUser.getFirstName()).isEqualTo("Test");
        assertThat(authenticatedUser.getLastName()).isEqualTo("User");
        assertThat(authenticatedUser.getFullName()).isEqualTo("Test User");
        assertThat(authenticatedUser.getSessionId()).isEqualTo(sessionId);
        assertThat(authenticatedUser.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .hasSize(2)
                .containsExactlyInAnyOrder("ROLE_admin", "ROLE_viewer");
    }

    private Jwt createJwt(Map<String, Object> claims)
    {
        return new Jwt(
                "dummy-token-value",
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("alg", "none"),
                claims
        );
    }
}