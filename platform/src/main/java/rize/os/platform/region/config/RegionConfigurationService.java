package rize.os.platform.region.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionConfigurationService
{
    private final RegionConfigurationParameterRepository repository;

    /**
     * Indicates whether the region feature is enabled.
     * @return true if the region feature is enabled, false otherwise
     */
    boolean isRegionFeatureEnabled()
    {
        return Boolean.parseBoolean(getRegionConfigurationValue(RegionConfigurationEntry.ENABLED));
    }

    /**
     * Enables or disables the region feature.
     * @param enabled true to enable the region feature, false to disable it
     */
    void setRegionFeatureEnabled(boolean enabled)
    {
        setRegionConfigurationValue(RegionConfigurationEntry.ENABLED, String.valueOf(enabled));
    }

    private String getRegionConfigurationValue(RegionConfigurationEntry entry)
    {
        var parameter = repository.findById(entry.toString());
        if (parameter.isEmpty())
        {
            log.error("Region configuration parameter '{}' not found - Returning default value: \"{}\"", entry, entry.getDefaultValue());
            return entry.getDefaultValue();
        }

        log.debug("Region configuration parameter '{}' found with value: \"{}\"", entry, parameter.get().getValue());
        return parameter.get().getValue();
    }

    private void setRegionConfigurationValue(RegionConfigurationEntry entry, String value)
    {
        if (!value.matches(entry.getValueRegex()))
            throw new InvalidRegionConfigurationValue(entry, value);

        var parameter = repository.findById(entry.toString())
                .orElseGet(() -> RegionConfigurationParameter
                        .builder()
                        .name(entry.toString())
                        .build());
        parameter.setValue(value);

        repository.save(parameter);
        log.info("Region configuration parameter '{}' updated with value: \"{}\"", entry, value);
    }
}
