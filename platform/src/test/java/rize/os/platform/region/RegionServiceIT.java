package rize.os.platform.region;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.platform.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class RegionServiceIT
{
    @Autowired
    private RegionService regionService;

    @Autowired
    private RegionRepository regionRepository;

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @AfterEach
    void tearDown()
    {
        regionRepository.deleteAll();
    }


    @Test
    void shouldCreateRegion()
    {
        var regionToCreate = Region.builder()
                .name("should-create-region")
                .displayName("shouldCreateRegion")
                .build();

        var createdRegion = regionService.createRegion(regionToCreate);
        assertThat(createdRegion).isNotNull();
        assertThat(createdRegion.getName()).isEqualTo(regionToCreate.getName());
        assertThat(createdRegion.getDisplayName()).isEqualTo(regionToCreate.getDisplayName());
    }

    @Test
    void shouldFailToCreateRegionWithInvalidValues()
    {
        var regionWithInvalidName = Region.builder()
                .name("shouldFailToCreateRegionWithInvalidValues")
                .displayName("shouldFailToCreateRegionWithInvalidValues")
                .build();

        assertThatThrownBy(() -> regionService.createRegion(regionWithInvalidName))
                .isInstanceOf(RegionConstraintException.class);

        var regionWithInvalidDisplayName = Region.builder()
                .name("valid-name")
                .displayName("")
                .build();

        assertThatThrownBy(() -> regionService.createRegion(regionWithInvalidDisplayName))
                .isInstanceOf(RegionConstraintException.class);
    }

    @Test
    void shouldFailToCreateRegionWithSameName()
    {
        var regionToCreate = Region.builder()
                .name("should-fail-to-create-region-with-same-name")
                .displayName("shouldFailToCreateRegionWithSameName1")
                .build();

        regionService.createRegion(regionToCreate);

        var regionWithSameName = Region.builder()
                .name("should-fail-to-create-region-with-same-name")
                .displayName("shouldFailToCreateRegionWithSameName2")
                .build();

        assertThatThrownBy(() -> regionService.createRegion(regionWithSameName))
                .isInstanceOf(RegionAlreadyExistsException.class);
    }
}