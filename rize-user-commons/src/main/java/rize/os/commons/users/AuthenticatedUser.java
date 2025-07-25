package rize.os.commons.users;

import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an authenticated user within the rize.os platform.
 * This record encapsulates key information extracted from the user's access token,
 * providing a centralized and consistent view of the user's identity, roles,
 * and organizational affiliations.
 *
 * @param userId The unique identifier of the user. Nullable if the user ID is not available.
 * @param username The username of the authenticated user. Nullable if the username is not available.
 * @param email The email address of the authenticated user. Nullable if the email is not available.
 * @param emailVerified Indicates whether the user's email address has been verified.
 * @param firstName The first name of the user. Defaults to an empty string if not provided.
 * @param lastName The last name of the user. Defaults to an empty string if not provided.
 * @param sessionId The session ID associated with the user's authentication. Nullable.
 * @param clientRoles A set of client-specific roles assigned to the user within the
 * current application (client). Defaults to an empty set if not provided.
 * These roles are defined in Keycloak for the application's client.
 * @param clientId The ID of the client (application) through which the user authenticated. Nullable.
 * @param organizations A set of unique organization names the user is a member of. Defaults to an empty set if not provided.
 * In rize.os, an organization corresponds to a Keycloak "Organization". A user can be a member of multiple organizations.
 * @param rawToken The raw access token string. Nullable.
 */
public record AuthenticatedUser(
        @Nullable String userId,
        @Nullable String username,
        @Nullable String email,
        boolean emailVerified,
        @Nullable String firstName,
        @Nullable String lastName,
        @Nullable String sessionId,
        @Nullable Set<String> clientRoles,
        @Nullable String clientId,
        @Nullable Set<String> organizations,
        @Nullable String rawToken)
{
    /**
     * Compact constructor for the {@code AuthenticatedUser} record.
     * Initializes default values for nullable fields to ensure non-null collections
     * and empty strings for names if they are not explicitly provided.
     */
    public AuthenticatedUser
    {
        firstName = Objects.requireNonNullElse(firstName, "");
        lastName = Objects.requireNonNullElse(lastName, "");
        clientRoles = Objects.requireNonNullElse(clientRoles, Collections.emptySet());
        organizations = Objects.requireNonNullElse(organizations, Collections.emptySet());
    }

    /**
     * Checks if the authenticated user possesses a specific client role.
     * These roles are application-specific and are managed in Keycloak.
     *
     * @param role The name of the client role to check.
     * @return {@code true} if the user has the specified client role, {@code false} otherwise.
     */
    public boolean hasClientRole(String role)
    {
        assert clientRoles != null; // Ensured by compact constructor
        return clientRoles.contains(role);
    }

    /**
     * Checks if the authenticated user is a member of a specific organization.
     * Users can be members of multiple organizations, and their permissions
     * can differ per organization.
     *
     * @param organizationName The unique name of the organization to check membership for.
     * @return {@code true} if the user is a member of the specified organization, {@code false} otherwise.
     */
    public boolean isMemberOfOrganization(String organizationName)
    {
        assert organizations != null; // Ensured by compact constructor
        return organizations.contains(organizationName);
    }

    /**
     * Returns the full name of the authenticated user, concatenating first and last names.
     *
     * @return The full name of the user, or an empty string if both first and last names are empty.
     */
    public String fullName()
    {
        return String.format("%s %s", firstName, lastName).trim();
    }

    /**
     * Provides a string representation of the authenticated user, useful for logging and debugging.
     *
     * @return A formatted string containing key user details.
     */
    @Override
    public String toString()
    {
        return String.format("AuthenticatedUser[userId='%s', username='%s', email='%s', fullName='%s', sessionId='%s', clientRoles=%s]",
                userId, username, email, fullName(), sessionId, clientRoles);
    }
}