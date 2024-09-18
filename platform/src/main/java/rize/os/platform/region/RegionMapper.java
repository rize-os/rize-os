package rize.os.platform.region;

import org.springframework.stereotype.Service;
import rize.os.commons.region.RegionDto;

@Service
public class RegionMapper
{
    /**
     * Maps a region object to a region DTO
     * @param region Region object
     * @return Region DTO
     */
    RegionDto toRegionDto(Region region)
    {
        return RegionDto.builder()
                .id(region.getId())
                .name(region.getName())
                .displayName(region.getDisplayName())
                .build();
    }

    /**
     * Maps a region DTO to a region object
     * @param dto Region DTO
     * @return Region object
     */
    Region toRegion(RegionDto dto)
    {
        return Region.builder()
                .id(dto.getId())
                .name(dto.getName())
                .displayName(dto.getDisplayName())
                .build();
    }
}
