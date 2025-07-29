package rize.os.security;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * An {@link org.springframework.security.core.Authentication} implementation that is granted to users who have been
 * authenticated.
 *
 * @author Dennis Schäfer (<a href="https://github.com/dennis-schaefer">...</a>)
 */
public class AuthenticatedUserToken extends AbstractAuthenticationToken
{
    private final AuthenticatedUser authenticatedUser;

    /**
     * Creates a new authenticated user token.
     *
     * @param authenticatedUser The authenticated user.
     */
    public AuthenticatedUserToken(@NonNull AuthenticatedUser authenticatedUser)
    {
        super(authenticatedUser.getAuthorities());
        this.authenticatedUser = authenticatedUser;
        setAuthenticated(true);
    }

    /**
     * Returns {@code null} as the user is already authenticated and credentials are no longer needed.
     *
     * @return {@code null}
     */
    @Nullable
    @Override
    public Object getCredentials()
    {
        return null;
    }

    /**
     * Returns the authenticated user.
     *
     * @return The authenticated user.
     */
    @NonNull
    @Override
    public AuthenticatedUser getPrincipal()
    {
        return authenticatedUser;
    }
}
