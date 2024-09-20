package rize.os.platform.region;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultRegionInitialization
{
    @Bean
    CommandLineRunner initDefaultRegion(RegionService regionService)
    {
        return args ->
        {
            if (regionService.findByName(Region.DEFAULT_REGION_NAME).isPresent())
                return;

            var defaultRegion = Region.builder()
                    .name(Region.DEFAULT_REGION_NAME)
                    .displayName(Region.DEFAULT_REGION_DISPLAY_NAME)
                    .build();

            log.debug("Initializing default region: '{}'", defaultRegion.getName());
            regionService.createRegion(defaultRegion);
        };
    }
}
