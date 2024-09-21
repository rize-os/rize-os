package rize.os.platform.region;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.commons.region.RegionDto;
import rize.os.platform.TestcontainersConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class RegionEndpointIT
{
    @Autowired
    private RegionEndpoint regionEndpoint;

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
                .id(UUID.randomUUID())
                .name("should-find-all-regions-1")
                .displayName("shouldFindAllRegions1")
                .build();

        var region2 = Region.builder()
                .id(UUID.randomUUID())
                .name("should-find-all-regions-2")
                .displayName("shouldFindAllRegions2")
                .build();

        regionRepository.save(region1);
        regionRepository.save(region2);

        var regions = regionEndpoint.findAll();
        assertThat(regions).hasSize(2);
        assertThat(regions).extracting(RegionDto::getName).contains(region1.getName(), region2.getName());
    }

    @Test
    void shouldFindRegionById()
    {
        var regionToFind = Region.builder()
                .id(UUID.randomUUID())
                .name("should-find-region-by-id")
                .displayName("shouldFindRegionById")
                .build();
        regionRepository.save(regionToFind);

        var foundRegion = regionEndpoint.findById(regionToFind.getId());
        assertThat(foundRegion).isNotNull();
        assertThat(foundRegion.getId()).isEqualTo(regionToFind.getId());
        assertThat(foundRegion.getName()).isEqualTo(regionToFind.getName());
        assertThat(foundRegion.getDisplayName()).isEqualTo(regionToFind.getDisplayName());
    }

    @Test
    void shouldCreateRegion()
    {
        var regionToCreate = RegionDto.builder().name("region-endpoint-create").displayName("shouldCreateRegion").build();
        var createdRegion = regionEndpoint.create(regionToCreate);
        assertThat(createdRegion).isNotNull();
        assertThat(createdRegion.getId()).isNotNull();
        assertThat(createdRegion.getName()).isEqualTo(regionToCreate.getName());
        assertThat(createdRegion.getDisplayName()).isEqualTo(regionToCreate.getDisplayName());
    }
}