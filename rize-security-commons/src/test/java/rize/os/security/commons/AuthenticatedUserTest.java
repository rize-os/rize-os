package rize.os.security.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserTest
{
    @Mock
    private OidcUserInfo mockUserInfo;
    @Mock
    private OidcIdToken mockIdToken;

    private Set<GrantedAuthority> authorities;
    private Map<String, Object> claims;
    private String sessionId;
    private String userId;

    @BeforeEach
    void setUp()
    {
        userId = UUID.randomUUID().toString();
        sessionId = UUID.randomUUID().toString();
        authorities = Set.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        claims = Map.of(
                "sub", userId,
                "preferred_username", "testuser",
                "name", "Test User",
                "given_name", "Test",
                "family_name", "User",
                "email", "test.user@rize.os",
                "email_verified", true
        );
    }

    @Test
    @DisplayName("[AuthenticatedUser] Should construct correctly with valid data")
    void shouldConstructWithValidData()
    {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(mockUserInfo, mockIdToken, authorities, sessionId);

        assertThat(authenticatedUser).isNotNull();
        assertThat(authenticatedUser.getAuthorities()).isEqualTo(authorities);
        assertThat(authenticatedUser.getSessionId()).isEqualTo(sessionId);
        assertThat(authenticatedUser.getUserInfo()).isEqualTo(mockUserInfo);
        assertThat(authenticatedUser.getIdToken()).isEqualTo(mockIdToken);
    }

    @Test
    @DisplayName("[AuthenticatedUser] Should handle null authorities gracefully by providing an empty set")
    void shouldHandleNullAuthorities()
    {
        AuthenticatedUser userWithNullAuthorities = new AuthenticatedUser(mockUserInfo, mockIdToken, null, sessionId);

        assertThat(userWithNullAuthorities.getAuthorities())
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("[AuthenticatedUser] Should delegate simple getter methods to OidcUserInfo")
    void shouldDelegateGettersToUserInfo()
    {
        when(mockUserInfo.getSubject()).thenReturn(userId);
        when(mockUserInfo.getPreferredUsername()).thenReturn("testuser");
        when(mockUserInfo.getFullName()).thenReturn("Test User");
        when(mockUserInfo.getGivenName()).thenReturn("Test");
        when(mockUserInfo.getFamilyName()).thenReturn("User");
        when(mockUserInfo.getEmail()).thenReturn("test.user@rize.os");
        when(mockUserInfo.getPhoneNumber()).thenReturn("+49123456789");

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(mockUserInfo, mockIdToken, authorities, sessionId);

        assertThat(authenticatedUser.getUserId()).isEqualTo(userId);
        assertThat(authenticatedUser.getUsername()).isEqualTo("testuser");
        assertThat(authenticatedUser.getFullName()).isEqualTo("Test User");
        assertThat(authenticatedUser.getFirstName()).isEqualTo("Test");
        assertThat(authenticatedUser.getLastName()).isEqualTo("User");
        assertThat(authenticatedUser.getEmail()).isEqualTo("test.user@rize.os");
        assertThat(authenticatedUser.getPhoneNumber()).isEqualTo("+49123456789");
    }

    @Test
    @DisplayName("[AuthenticatedUser] Should correctly return boolean verification status")
    void shouldReturnVerificationStatus()
    {
        when(mockUserInfo.getEmailVerified()).thenReturn(true);
        when(mockUserInfo.getPhoneNumberVerified()).thenReturn(false);

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(mockUserInfo, mockIdToken, authorities, sessionId);

        assertThat(authenticatedUser.isEmailVerified()).isTrue();
        assertThat(authenticatedUser.isPhoneNumberVerified()).isFalse();
    }

    @Test
    @DisplayName("[AuthenticatedUser] Should return false for isEmailVerified when the claim is null")
    void shouldReturnFalseForEmailVerifiedWhenNull()
    {
        when(mockUserInfo.getEmailVerified()).thenReturn(null);
        AuthenticatedUser user = new AuthenticatedUser(mockUserInfo, mockIdToken, authorities, sessionId);

        assertThat(user.isEmailVerified()).isFalse();
    }

    @Test
    @DisplayName("[AuthenticatedUser] Should return false for isPhoneNumberVerified when the claim is null")
    void shouldReturnFalseForPhoneNumberVerifiedWhenNull()
    {
        when(mockUserInfo.getPhoneNumberVerified()).thenReturn(null);
        AuthenticatedUser user = new AuthenticatedUser(mockUserInfo, mockIdToken, authorities, sessionId);

        assertThat(user.isPhoneNumberVerified()).isFalse();
    }

    @Test
    @DisplayName("[AuthenticatedUser] Should implement OidcUser interface correctly")
    void shouldImplementOidcUserInterface()
    {
        when(mockUserInfo.getPreferredUsername()).thenReturn("testuser");
        when(mockIdToken.getClaims()).thenReturn(claims);

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(mockUserInfo, mockIdToken, authorities, sessionId);

        assertThat(authenticatedUser.getName()).isEqualTo("testuser");
        assertThat(authenticatedUser.getAttributes()).isEqualTo(claims);
        assertThat(authenticatedUser.getClaims()).isEqualTo(claims);
    }

    @Test
    @DisplayName("[AuthenticatedUser] Should delegate getTokenValue to OidcIdToken")
    void shouldDelegateGetTokenValue()
    {
        when(mockIdToken.getTokenValue()).thenReturn("dummy-token-value");

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(mockUserInfo, mockIdToken, authorities, sessionId);

        assertThat(authenticatedUser.getTokenValue()).isEqualTo("dummy-token-value");
    }
}