package rize.os.access.manager.organization.exceptions;

import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rize.os.access.manager.organization.Organization;

@Slf4j
@Getter
public class OrganizationCreateException extends RuntimeException
{
    private final Organization organization;
    private Response response;

    public OrganizationCreateException(Organization organization, Response response)
    {
        super("Failed to create organization: " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase() + " - " + response.readEntity(String.class));

        log.warn(getMessage());
        this.organization = organization;
        this.response = response;
    }

    public OrganizationCreateException(Organization organization, Throwable cause)
    {
        super("Failed to create organization", cause);

        log.warn(getMessage());
        this.organization = organization;
    }
}
