package rize.os.platform.region;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * Exception thrown when a region has invalid values.
 */
@Slf4j
@Getter
public class RegionConstraintException extends IllegalArgumentException
{
    private final Region region;
    private final Collection<ConstraintViolation<Region>> violations;

    RegionConstraintException(Region region, Collection<ConstraintViolation<Region>> violations)
    {
        super("Invalid values in " + region + ": " +
                violations.stream()
                        .map(v -> v.getPropertyPath() + " " + v.getMessage())
                        .reduce((a, b) -> a + ", " + b).orElse(""));

        log.info(getMessage());
        this.region = region;
        this.violations = violations;
    }
}
