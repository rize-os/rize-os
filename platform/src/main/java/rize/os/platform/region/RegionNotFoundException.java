package rize.os.platform.region;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegionNotFoundException extends IllegalArgumentException
{
    public RegionNotFoundException(String searchBy)
    {
        super("Region could not be found by: " + searchBy);
        log.warn(getMessage());
    }
}
