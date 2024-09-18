package rize.os.platform.region.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("aot-config")
public class RegionConfigurationAotConfiguration
{
    @Bean
    CommandLineRunner regionConfigurationAotConfigurationClr(RegionConfigurationService regionConfigurationService)
    {
        return args ->
        {
            log.info("---------------------------------------------------------------------------------------");
            log.info("--- Run methods from RegionConfigurationService.class to generate AOT configuration ---");
            log.info("---------------------------------------------------------------------------------------");

            var initialRegionFeatureEnabled = regionConfigurationService.isRegionFeatureEnabled();
            regionConfigurationService.setRegionFeatureEnabled(!initialRegionFeatureEnabled);
            regionConfigurationService.setRegionFeatureEnabled(initialRegionFeatureEnabled);

            log.info("--------------------------------------------------------------------------------");
        };
    }
}
