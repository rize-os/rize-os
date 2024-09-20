package rize.os.platform.region;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.platform.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class DefaultRegionInitializationIT
{
    @Autowired
    private RegionRepository regionRepository;

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @Test
    void shouldInitializeDefaultRegion()
    {
        var regions = regionRepository.findAll();

        assertThat(regions.size()).isGreaterThanOrEqualTo(1);
        var defaultRegion = regions.stream().filter(region -> region.getName().equals(Region.DEFAULT_REGION_NAME)).findFirst();

        assertThat(defaultRegion).isPresent();
        assertThat(defaultRegion.get().getName()).isEqualTo(Region.DEFAULT_REGION_NAME);
        assertThat(defaultRegion.get().getDisplayName()).isEqualTo(Region.DEFAULT_REGION_DISPLAY_NAME);
    }
}