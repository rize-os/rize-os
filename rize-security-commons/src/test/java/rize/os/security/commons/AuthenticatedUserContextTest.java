package rize.os.security.commons;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserContextTest
{
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private OidcIdToken idToken;

    @Mock
    private OidcUserInfo userInfo;

    @BeforeEach
    void setUp()
    {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("[AuthenticatedUserContext] should return AuthenticatedUser when user is authenticated")
    void get_shouldReturnAuthenticatedUser_whenUserIsAuthenticated()
    {
        var expectedUser = new AuthenticatedUser(userInfo, idToken, Set.of(), UUID.randomUUID().toString());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(expectedUser);

        var actualUser = AuthenticatedUserContext.get();

        assertThat(actualUser).isPresent();
        assertThat(actualUser.get()).isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("[AuthenticatedUserContext] should return empty when authentication is null")
    void get_shouldReturnEmpty_whenAuthenticationIsNull()
    {
        when(securityContext.getAuthentication()).thenReturn(null);

        var actualUser = AuthenticatedUserContext.get();

        assertThat(actualUser).isEmpty();
    }

    @Test
    @DisplayName("[AuthenticatedUserContext] should return empty when user is not authenticated")
    void get_shouldReturnEmpty_whenUserIsNotAuthenticated()
    {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        var actualUser = AuthenticatedUserContext.get();

        assertThat(actualUser).isEmpty();
    }

    @Test
    @DisplayName("[AuthenticatedUserContext] should return empty when principal is not an AuthenticatedUser")
    void get_shouldReturnEmpty_whenPrincipalIsNotAnAuthenticatedUser()
    {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(new Object());

        var actualUser = AuthenticatedUserContext.get();

        assertTrue(actualUser.isEmpty());
    }
}

