package rize.os.commons.users;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticatedUserTest
{
    private AuthenticatedUser createAuthenticatedUser()
    {
        return new AuthenticatedUser(
                "user-123",
                "testuser",
                "test@example.com",
                true,
                "John",
                "Doe",
                "session-abc",
                Set.of("ADMIN", "USER"),
                "my-client",
                Set.of("org-a", "org-b"),
                "a.b.c"
        );
    }

    @Test
    @DisplayName("should replace null clientRoles with an empty set")
    void shouldHandleNullRoles()
    {
        var user = new AuthenticatedUser("uid", "u", "e", true, "f", "l", "s", null, "c", Set.of("o"), "t");

        assertThat(user.clientRoles()).isNotNull();
        assertThat(user.clientRoles()).isEmpty();
    }

    @Test
    @DisplayName("should replace null organizations with an empty set")
    void shouldHandleNullOrganizations()
    {
        var user = new AuthenticatedUser("uid", "u", "e", true, "f", "l", "s", Set.of("r"), "c", null, "t");

        assertThat(user.organizations()).isNotNull();
        assertThat(user.organizations()).isEmpty();
    }

    @Test
    @DisplayName("should replace null firstName with an empty string")
    void shouldHandleNullFirstName()
    {
        var user = new AuthenticatedUser("uid", "u", "e", true, null, "Doe", "s", Set.of("r"), "c", Set.of("o"), "t");

        assertThat(user.firstName()).isNotNull();
        assertThat(user.firstName()).isEmpty();
        assertThat(user.fullName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("should replace null lastName with an empty string")
    void shouldHandleNullLastName()
    {
        var user = new AuthenticatedUser("uid", "u", "e", true, "John", null, "s", Set.of("r"), "c", Set.of("o"), "t");

        assertThat(user.lastName()).isNotNull();
        assertThat(user.lastName()).isEmpty();
        assertThat(user.fullName()).isEqualTo("John");
    }

    @Test
    @DisplayName("should validate 'hasClientRole' method")
    void shouldValidateHasClientRole()
    {
        var user = createAuthenticatedUser();

        assertThat(user.hasClientRole("ADMIN")).isTrue();
        assertThat(user.hasClientRole("USER")).isTrue();
        assertThat(user.hasClientRole("GUEST")).isFalse();
    }

    @Test
    @DisplayName("should validate 'isMemberOfOrganization' method")
    void shouldValidateIsMemberOfOrganization()
    {
        var user = createAuthenticatedUser();

        assertThat(user.isMemberOfOrganization("org-a")).isTrue();
        assertThat(user.isMemberOfOrganization("org-b")).isTrue();
        assertThat(user.isMemberOfOrganization("org-c")).isFalse();
    }
}