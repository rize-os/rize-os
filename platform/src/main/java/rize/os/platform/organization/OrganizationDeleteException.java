package rize.os.platform.organization;

import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when an organization could not be deleted from Keycloak.
 */
@Slf4j
@Getter
public class OrganizationDeleteException extends RuntimeException
{
    private final Organization organization;
    private Response response;

    OrganizationDeleteException(Organization organization, Response response)
    {
        super("Failed to delete organization '" + organization.getName() + "' from Keycloak - Server responded with: " +
                response.getStatus() + " " + response.getStatusInfo().getReasonPhrase() + " - " + response.readEntity(String.class));

        log.error(getMessage());
        this.organization = organization;
        this.response = response;
    }

    OrganizationDeleteException(Organization organization, Throwable cause)
    {
        super("Failed to delete organization '" + organization.getName() + "' from Keycloak", cause);
        log.error(getMessage());
        this.organization = organization;
    }
}
