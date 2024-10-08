package rize.os.access.manager.organization.exceptions;

import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rize.os.access.manager.organization.Organization;

@Slf4j
@Getter
public class OrganizationUpdateException extends RuntimeException
{
    private final Organization organization;
    private Response response;

    public OrganizationUpdateException(Organization organization,  Response response)
    {
        super("Failed to update organization: " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase() + " - " + response.readEntity(String.class));

        log.warn(getMessage());
        this.organization = organization;
        this.response = response;
    }

    public OrganizationUpdateException(Organization organization, Throwable cause)
    {
        super("Failed to update organization: " + cause.getMessage());

        log.warn(getMessage());
        this.organization = organization;
    }
}
