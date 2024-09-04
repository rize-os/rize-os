package rize.os.platform.organization;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * Exception thrown when an organization has invalid values.
 */
@Slf4j
@Getter
public class OrganizationConstraintException extends IllegalArgumentException
{
    private final Organization organization;
    private final Collection<ConstraintViolation<Organization>> violations;

    OrganizationConstraintException(Organization organization, Collection<ConstraintViolation<Organization>> violations)
    {
        super("Invalid values in " + organization + ": " +
                violations.stream()
                        .map(v -> v.getPropertyPath() + " " + v.getMessage())
                        .reduce((a, b) -> a + ", " + b).orElse(""));

        log.info(getMessage());
        this.organization = organization;
        this.violations = violations;
    }
}
