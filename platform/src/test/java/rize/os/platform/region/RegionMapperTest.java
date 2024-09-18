package rize.os.platform.region;

import org.junit.jupiter.api.Test;
import rize.os.commons.region.RegionDto;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegionMapperTest
{
    private final RegionMapper regionMapper = new RegionMapper();

    @Test
    void shouldMapRegionToRegionDto()
    {
        var region = Region.builder()
                .id(UUID.randomUUID())
                .name("test-region")
                .displayName("Test Region")
                .build();

        var regionDto = regionMapper.toRegionDto(region);

        assertThat(regionDto.getId()).isEqualTo(region.getId());
        assertThat(regionDto.getName()).isEqualTo(region.getName());
        assertThat(regionDto.getDisplayName()).isEqualTo(region.getDisplayName());
    }

    @Test
    void shouldMapRegionDtoToRegion()
    {
        var regionDto = RegionDto.builder()
                .id(UUID.randomUUID())
                .name("test-region")
                .displayName("Test Region")
                .build();

        var region = regionMapper.toRegion(regionDto);

        assertThat(region.getId()).isEqualTo(regionDto.getId());
        assertThat(region.getName()).isEqualTo(regionDto.getName());
        assertThat(region.getDisplayName()).isEqualTo(regionDto.getDisplayName());
    }
}