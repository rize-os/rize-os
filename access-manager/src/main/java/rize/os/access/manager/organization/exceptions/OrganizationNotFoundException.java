package rize.os.access.manager.organization.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OrganizationNotFoundException extends IllegalArgumentException
{
    public OrganizationNotFoundException(String id)
    {
        super("Organization not found with id: " + id);
        log.warn(getMessage());
    }
}
