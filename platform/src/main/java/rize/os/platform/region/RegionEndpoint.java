package rize.os.platform.region;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.Endpoint;
import com.vaadin.hilla.exception.EndpointException;
import lombok.RequiredArgsConstructor;
import rize.os.commons.region.RegionDto;

@Endpoint
@AnonymousAllowed
@RequiredArgsConstructor
public class RegionEndpoint
{
    private final RegionService regionService;
    private final RegionMapper regionMapper;

    /**
     * Creates a new region.
     * @param regionDto Region to create
     * @return The created region
     */
    public RegionDto create(RegionDto regionDto)
    {
        try
        {
            var region = regionMapper.toRegion(regionDto);
            var createdRegion = regionService.createRegion(region);
            return regionMapper.toRegionDto(createdRegion);
        }
        catch (Exception e) {
            throw new EndpointException(e.getMessage(), e, e);
        }
    }
}
