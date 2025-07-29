package rize.os.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserTokenTest
{
    @Mock
    private AuthenticatedUser mockAuthenticatedUser;

    @Test
    @DisplayName("[AuthenticatedUserToken] Should create a valid, authenticated token with principal and authorities")
    void shouldCreateValidAuthenticatedToken()
    {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("organisation-a.admin")
        );
        doReturn(authorities).when(mockAuthenticatedUser).getAuthorities();

        AuthenticatedUserToken token = new AuthenticatedUserToken(mockAuthenticatedUser);

        assertThat(token.isAuthenticated()).isTrue();
        assertThat(token.getPrincipal()).isSameAs(mockAuthenticatedUser);
        assertThat(token.getCredentials()).isNull();
        assertThat(token.getAuthorities()).isEqualTo(authorities);
    }

    @Test
    @DisplayName("[AuthenticatedUserToken] Should correctly handle a user with no authorities")
    void shouldHandleUserWithNoAuthorities()
    {
        when(mockAuthenticatedUser.getAuthorities()).thenReturn(Collections.emptyList());

        AuthenticatedUserToken token = new AuthenticatedUserToken(mockAuthenticatedUser);

        assertThat(token.isAuthenticated()).isTrue();
        assertThat(token.getPrincipal()).isSameAs(mockAuthenticatedUser);
        assertThat(token.getAuthorities()).isEmpty();
    }
}