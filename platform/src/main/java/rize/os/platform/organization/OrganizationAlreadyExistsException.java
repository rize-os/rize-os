package rize.os.platform.organization;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when an organization with the same name already exists.
 */
@Slf4j
@Getter
public class OrganizationAlreadyExistsException extends IllegalArgumentException
{
    private final Organization organization;

    OrganizationAlreadyExistsException(Organization organization)
    {
        super("An organization with the name '" + organization.getName() + "' already exists");
        log.info(getMessage());
        this.organization = organization;
    }
}
