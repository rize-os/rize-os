package rize.os.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Provides access to the currently authenticated user in the security context.
 * <p>
 * This utility class encapsulates the logic for retrieving the {@link AuthenticatedUser}
 * from the {@link SecurityContextHolder}. It provides a simple and type-safe
 * method {@link #get()} to get the logged-in user.
 *
 * @author Dennis Schäfer (<a href="https://github.com/dennis-schaefer">...</a>)
 */
public final class AuthenticatedUserContext
{
    private static final Logger log = LoggerFactory.getLogger(AuthenticatedUserContext.class);

    private AuthenticatedUserContext() {}

     /**
     * Retrieves the currently authenticated user from the {@link org.springframework.security.core.context.SecurityContext}.
     * <p>
     * Retrieves the currently authenticated user from the {@link org.springframework.security.core.context.SecurityContext}.
     * <p>
     * This method checks the current security context for a valid and authenticated
     * {@link org.springframework.security.core.Authentication}. If one is present and
     * the {@code Principal} object is an instance of {@link AuthenticatedUser}, it is
     * returned in an {@link Optional}.
     * <p>
     * In all other cases – if no authentication is present, the authentication
     * is not valid (e.g., anonymous user), or the principal object is not of the expected type –
     * an empty {@link Optional} is returned.
     *
     * @return An {@link Optional} containing the {@link AuthenticatedUser} if present and authenticated,
     *         otherwise an empty {@link Optional}.
     */
    public static Optional<AuthenticatedUser> get()
    {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            return Optional.empty();

        if (authentication.getPrincipal() instanceof AuthenticatedUser)
            return Optional.of((AuthenticatedUser) authentication.getPrincipal());

        log.warn("Authentication principal is not an instance of AuthenticatedUser: {}", authentication.getPrincipal().getClass().getName());
        return Optional.empty();
    }
}
