package rize.os.platform.region.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class InvalidRegionConfigurationValue extends IllegalArgumentException
{
    private final RegionConfigurationEntry entry;
    private final String value;

    public InvalidRegionConfigurationValue(RegionConfigurationEntry entry, String value)
    {
        super("Value '" + value + "' is invalid for region configuration parameter '" + entry + "' - Value must match: " + entry.getValueRegex());
        this.entry = entry;
        this.value = value;
        log.warn(getMessage());
    }
}
