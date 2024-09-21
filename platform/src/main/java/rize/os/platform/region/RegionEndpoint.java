package rize.os.platform.region;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.Endpoint;
import com.vaadin.hilla.exception.EndpointException;
import lombok.RequiredArgsConstructor;
import rize.os.commons.region.RegionDto;

import java.util.List;
import java.util.UUID;

@Endpoint
@AnonymousAllowed
@RequiredArgsConstructor
public class RegionEndpoint
{
    private final RegionService regionService;
    private final RegionMapper regionMapper;

    /**
     * Returns a list of all regions.
     * @return List of regions
     */
    public List<RegionDto> findAll()
    {
        try
        {
            var regions = regionService.findAll();
            return regions.stream().map(regionMapper::toRegionDto).toList();
        }
        catch (Exception e) {
            throw new EndpointException(e.getMessage(), e, e);
        }
    }

    /**
     * Returns a region by its id.
     * @param id ID of the region
     * @return Region with the given ID
     */
    public RegionDto findById(UUID id)
    {
        try
        {
            var region = regionService.findById(id);
            return region.map(regionMapper::toRegionDto).orElseThrow(() -> new RegionNotFoundException(id.toString()));
        }
        catch (Exception e) {
            throw new EndpointException(e.getMessage(), e, e);
        }
    }

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

    /**
     * Updates a region.
     * @param regionDto Region to update
     * @return The updated region
     */
    public RegionDto update(RegionDto regionDto)
    {
        try
        {
            var region = regionMapper.toRegion(regionDto);
            var updatedRegion = regionService.updateRegion(region);
            return regionMapper.toRegionDto(updatedRegion);
        }
        catch (Exception e) {
            throw new EndpointException(e.getMessage(), e, e);
        }
    }
}
