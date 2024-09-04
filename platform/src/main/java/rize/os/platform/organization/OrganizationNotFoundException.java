package rize.os.platform.organization;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrganizationNotFoundException extends IllegalArgumentException
{
    public OrganizationNotFoundException(String searchBy)
    {
        super("Organization could not be found by: " + searchBy);
        log.warn(getMessage());
    }
}
