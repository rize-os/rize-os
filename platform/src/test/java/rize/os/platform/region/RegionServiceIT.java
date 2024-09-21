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

import java.util.UUID;

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
    void shouldFindAllRegions()
    {
        var region1 = Region.builder()
                .name("should-find-all-regions-1")
                .displayName("shouldFindAllRegions1")
                .build();

        var region2 = Region.builder()
                .name("should-find-all-regions-2")
                .displayName("shouldFindAllRegions2")
                .build();

        regionService.createRegion(region1);
        regionService.createRegion(region2);

        var regions = regionService.findAll();
        assertThat(regions).hasSize(2);
        assertThat(regions).contains(region1, region2);
    }

    @Test
    void shouldFindRegionById()
    {
        var regionToFind = Region.builder()
                .name("should-find-region-by-id")
                .displayName("shouldFindRegionById")
                .build();

        var createdRegion = regionService.createRegion(regionToFind);

        var foundRegion = regionService.findById(createdRegion.getId());
        assertThat(foundRegion).isPresent();
        assertThat(foundRegion.get()).isEqualTo(regionToFind);
    }

    @Test
    void shouldNotFindRegionById()
    {
        var foundRegion = regionService.findById(UUID.randomUUID());
        assertThat(foundRegion).isEmpty();
    }

    @Test
    void shouldFindRegionByName()
    {
        var regionToFind = Region.builder()
                .name("should-find-region-by-name")
                .displayName("shouldFindRegionByName")
                .build();

        regionService.createRegion(regionToFind);

        var foundRegion = regionService.findByName(regionToFind.getName());
        assertThat(foundRegion).isPresent();
        assertThat(foundRegion.get()).isEqualTo(regionToFind);
    }

    @Test
    void shouldNotFindRegionByName()
    {
        var foundRegion = regionService.findByName("should-not-find-region-by-name");
        assertThat(foundRegion).isEmpty();
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

    @Test
    void shouldUpdateRegion()
    {
        var regionToUpdate = Region.builder()
                .name("should-update-region")
                .displayName("shouldUpdateRegion")
                .build();

        var createdRegion = regionService.createRegion(regionToUpdate);

        var updatedRegion = Region.builder()
                .id(createdRegion.getId())
                .name("should-update-region")
                .displayName("shouldUpdateRegionUpdated")
                .build();

        var result = regionService.updateRegion(updatedRegion);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(createdRegion.getId());
        assertThat(result.getName()).isEqualTo(createdRegion.getName());
        assertThat(result.getDisplayName()).isEqualTo(updatedRegion.getDisplayName());
    }

    @Test
    void shouldFailToUpdateRegionName()
    {
        var regionToUpdate = Region.builder()
                .name("should-fail-to-update-region-name")
                .displayName("shouldFailToUpdateRegionName")
                .build();

        var createdRegion = regionService.createRegion(regionToUpdate);

        var updatedRegion = Region.builder()
                .id(createdRegion.getId())
                .name("should-fail-to-update-region-name-updated")
                .displayName("shouldFailToUpdateRegionNameUpdated")
                .build();

        assertThatThrownBy(() -> regionService.updateRegion(updatedRegion))
                .isInstanceOf(RegionUpdateException.class);
    }

    @Test
    void shouldFailToUpdateNonExistingRegion()
    {
        var regionToUpdate = Region.builder()
                .id(UUID.randomUUID())
                .name("should-fail-to-update-non-existing-region")
                .displayName("shouldFailToUpdateNonExistingRegion")
                .build();

        assertThatThrownBy(() -> regionService.updateRegion(regionToUpdate))
                .isInstanceOf(RegionNotFoundException.class);
    }
}