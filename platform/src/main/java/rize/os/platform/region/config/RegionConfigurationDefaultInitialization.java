package rize.os.platform.region.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RegionConfigurationDefaultInitialization
{
    private final RegionConfigurationParameterRepository repository;

    @Bean
    CommandLineRunner regionConfigurationDefaultInitializationClr()
    {
        return args ->
        {
            log.debug("Initializing default values for region configuration");
            Arrays.stream(RegionConfigurationEntry.values()).toList().forEach(this::initDefaultConfiguration);
        };
    }

    private void initDefaultConfiguration(RegionConfigurationEntry entry)
    {
        var existingParameter = repository.findById(entry.toString());
        if (existingParameter.isPresent())
        {
            log.debug("Region configuration parameter '{}' already initialized - current value: {}", entry, existingParameter.get().getValue());
            return;
        }

        var parameter = RegionConfigurationParameter.builder()
                .name(entry.toString())
                .value(entry.getDefaultValue())
                .build();

        repository.save(parameter);
        log.info("Region configuration parameter '{}' initialized with default value: {}", entry, parameter.getValue());
    }
}
