package rize.os.security.commons;

import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.AddressStandardClaim;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an authenticated user in the system, wrapping OIDC user information.
 * This class implements the {@link OidcUser} interface, providing a standardized
 * way to access user details obtained from an OIDC-compliant identity provider.
 * It holds user information (claims), the ID token, and the user's authorities.
 *
 * @author Dennis Schäfer (<a href="https://github.com/dennis-schaefer">...</a>)
 */
public class AuthenticatedUser implements OidcUser
{
    /**
     * Contains the user information claims, such as name, email, etc.
     */
    private final OidcUserInfo userInfo;
    /**
     * The OIDC ID token that authenticates the user.
     */
    private final OidcIdToken idToken;
    /**
     * The set of authorities (e.g., roles) granted to the user.
     */
    private final Set<GrantedAuthority> authorities;
    /**
     * The session ID associated with the user's session.
     */
    private final String sessionId;

    /**
     * Constructs a new {@code AuthenticatedUser}.
     *
     * @param userInfo    The OIDC user info. Must not be {@code null}.
     * @param idToken     The OIDC ID token. Must not be {@code null}.
     * @param authorities The authorities granted to the user. If {@code null}, an empty set is used.
     * @param sessionId   The session ID for the user.
     */
    public AuthenticatedUser(@NonNull OidcUserInfo userInfo, @NonNull OidcIdToken idToken, Set<GrantedAuthority> authorities, String sessionId)
    {
        this.userInfo = userInfo;
        this.idToken = idToken;
        this.authorities = Objects.requireNonNullElse(authorities, Set.of());
        this.sessionId = sessionId;
    }

    /**
     * Returns the subject identifier for the user.
     *
     * @return The user's unique subject identifier.
     */
    public String getUserId()
    {
        return userInfo.getSubject();
    }

    /**
     * Returns the preferred username of the user.
     *
     * @return The user's preferred username.
     */
    public String getUsername()
    {
        return userInfo.getPreferredUsername();
    }

    /**
     * Returns the email address of the user.
     *
     * @return The user's email address.
     */
    public String getEmail()
    {
        return userInfo.getEmail();
    }

    /**
     * Checks if the user's email address has been verified.
     *
     * @return {@code true} if the email is verified, {@code false} otherwise.
     */
    public boolean isEmailVerified()
    {
        return Objects.requireNonNullElse(userInfo.getEmailVerified(), false);
    }

    /**
     * Returns the user's first name.
     *
     * @return The user's first name.
     */
    public String getFirstName()
    {
        return userInfo.getGivenName();
    }

    /**
     * Returns the user's middle name.
     *
     * @return The user's middle name.
     */
    public String getMiddleName()
    {
        return userInfo.getMiddleName();
    }

    /**
     * Returns the user's last name.
     *
     * @return The user's last name.
     */
    public String getLastName()
    {
        return userInfo.getFamilyName();
    }

    /**
     * Returns the user's full name.
     *
     * @return The user's full name.
     */
    public String getFullName()
    {
        return userInfo.getFullName();
    }

    /**
     * Returns the URL of the user's profile page.
     *
     * @return The profile URL.
     */
    public String getProfile()
    {
        return userInfo.getProfile();
    }

    /**
     * Returns the URL of the user's profile picture.
     *
     * @return The picture URL.
     */
    public String getPicture()
    {
        return userInfo.getPicture();
    }

    /**
     * Returns the URL of the user's web page or blog.
     *
     * @return The website URL.
     */
    public String getWebsite()
    {
        return userInfo.getWebsite();
    }

    /**
     * Returns the user's gender.
     *
     * @return The user's gender.
     */
    public String getGender()
    {
        return userInfo.getGender();
    }

    /**
     * Returns the user's birthdate.
     *
     * @return The user's birthdate.
     */
    public String getBirthdate()
    {
        return userInfo.getBirthdate();
    }

    /**
     * Returns the user's time zone information.
     *
     * @return The user's time zone.
     */
    public String getZoneInfo()
    {
        return userInfo.getZoneInfo();
    }

    /**
     * Returns the user's locale.
     *
     * @return The user's locale.
     */
    public String getLocale()
    {
        return userInfo.getLocale();
    }

    /**
     * Returns the user's phone number.
     *
     * @return The user's phone number.
     */
    public String getPhoneNumber()
    {
        return userInfo.getPhoneNumber();
    }

    /**
     * Checks if the user's phone number has been verified.
     *
     * @return {@code true} if the phone number is verified, {@code false} otherwise.
     */
    public Boolean isPhoneNumberVerified()
    {
        return Objects.requireNonNullElse(userInfo.getPhoneNumberVerified(), false);
    }

    /**
     * Returns the user's postal address.
     *
     * @return The user's address.
     */
    public AddressStandardClaim getAddress()
    {
        return userInfo.getAddress();
    }

    /**
     * Returns the time the user's information was last updated.
     *
     * @return The last update timestamp.
     */
    public Instant getUpdatedAt()
    {
        return userInfo.getUpdatedAt();
    }

    /**
     * Returns the session ID for the user's session.
     *
     * @return The session ID.
     */
    public String getSessionId()
    {
        return sessionId;
    }

    /**
     * Returns the raw token value of the OIDC token.
     *
     * @return The string representation of the OIDC token.
     */
    public String getTokenValue()
    {
        return idToken.getTokenValue();
    }

    /**
     * Returns the claims from the OIDC token.
     *
     * @return A map of claims.
     */
    @Override
    public Map<String, Object> getClaims()
    {
        return idToken.getClaims();
    }

    /**
     * Returns the OIDC user info.
     *
     * @return The {@link OidcUserInfo}.
     */
    @Override
    public OidcUserInfo getUserInfo()
    {
        return userInfo;
    }

    /**
     * Returns the OIDC token.
     *
     * @return The {@link OidcIdToken}.
     */
    @Override
    public OidcIdToken getIdToken()
    {
        return idToken;
    }

    /**
     * Returns the attributes of the user, which are the claims from the OIDC token.
     *
     * @return A map of attributes.
     */
    @Override
    public Map<String, Object> getAttributes()
    {
        return getClaims();
    }

    /**
     * Returns the authorities granted to the user.
     *
     * @return A collection of granted authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return authorities;
    }

    /**
     * Returns the name of the user, which is their preferred username.
     *
     * @return The user's name.
     */
    @Override
    public String getName()
    {
        return getUsername();
    }

    /**
     * Returns a string representation of the user object.
     *
     * @return A string containing key user details.
     */
    @Override
    public String toString()
    {
        return "AuthenticatedUser[" +
                "userId='" + getUserId() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", authorities=" + authorities + ']';
    }
}
