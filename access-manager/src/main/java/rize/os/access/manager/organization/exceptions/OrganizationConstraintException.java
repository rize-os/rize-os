package rize.os.access.manager.organization.exceptions;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rize.os.access.manager.organization.Organization;

import java.util.Collection;

@Slf4j
@Getter
public class OrganizationConstraintException extends IllegalArgumentException
{
    private final Organization organization;
    private final Collection<ConstraintViolation<Organization>> violations;

    public OrganizationConstraintException(Organization organization, Collection<ConstraintViolation<Organization>> violations)
    {
        super("Invalid values in " + organization + ": " +
                violations.stream()
                        .map(v -> v.getPropertyPath() + " " + v.getMessage())
                        .reduce((a, b) -> a + ", " + b).orElse(""));

        log.warn(getMessage());
        this.organization = organization;
        this.violations = violations;
    }
}
