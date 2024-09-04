package rize.os.platform.organization;

import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when an organization could not be created in Keycloak.
 */
@Slf4j
@Getter
public class OrganizationCreateException extends RuntimeException
{
    private final Organization organization;
    private Response response;

    OrganizationCreateException(Organization organization, Response response)
    {
        super("Failed to create organization '" + organization.getName() + "' in Keycloak - Server responded with: " +
                response.getStatus() + " " + response.getStatusInfo().getReasonPhrase() + " - " + response.readEntity(String.class));

        log.error(getMessage());
        this.organization = organization;
        this.response = response;
    }

    OrganizationCreateException(Organization organization, Throwable cause)
    {
        super("Failed to create organization '" + organization.getName() + "' in Keycloak", cause);
        log.error(getMessage());
        this.organization = organization;
    }
}
