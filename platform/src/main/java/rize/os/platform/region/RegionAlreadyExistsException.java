package rize.os.platform.region;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when a region with the same name already exists.
 */
@Slf4j
@Getter
public class RegionAlreadyExistsException extends IllegalArgumentException
{
    private final Region region;

    RegionAlreadyExistsException(Region region)
    {
        super("A region with the name '" + region.getName() + "' already exists");
        log.info(getMessage());
        this.region = region;
    }
}
