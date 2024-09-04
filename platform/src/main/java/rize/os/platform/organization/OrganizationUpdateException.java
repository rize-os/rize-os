package rize.os.platform.organization;

import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OrganizationUpdateException extends RuntimeException
{
    private final Organization organization;
    private Response response;

    OrganizationUpdateException(Organization organization, Response response)
    {
        super("Failed to update organization '" + organization.getName() + "' in Keycloak - Server responded with: " +
                response.getStatus() + " " + response.getStatusInfo().getReasonPhrase() + " - " + response.readEntity(String.class));

        log.error(getMessage());
        this.organization = organization;
        this.response = response;
    }

    OrganizationUpdateException(Organization organization, Throwable cause)
    {
        super("Failed to update organization '" + organization.getName() + "' in Keycloak", cause);
        log.error(getMessage());
        this.organization = organization;
    }

    public boolean hasResponse()
    {
        return response != null;
    }
}
