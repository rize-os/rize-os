package rize.os.platform.region;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegionUpdateException extends RuntimeException
{
    public RegionUpdateException(String message)
    {
        super(message);
        log.error(message);
    }
}
