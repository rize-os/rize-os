package rize.os.platform.region;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService
{
    private final RegionRepository regionRepository;


    /**
     * Returns a list of all regions.
     * @return List of all regions
     */
    List<Region> findAll()
    {
        log.debug("Loading all regions from database");
        var regions = regionRepository.findAll();
        return loggedRegions(regions);
    }

    /**
     * Creates a new region. The region must have a unique name.
     * @param region Object of the region to create
     * @return Object of the created organization
     * @throws RegionConstraintException If the region has invalid values
     * @throws RegionAlreadyExistsException If a region with the same name already exists
     */
    Region createRegion(Region region) throws RegionConstraintException, RegionAlreadyExistsException
    {
        region.setId(UUID.randomUUID());
        log.info("Creating new region: {}", region);
        validateRegion(region);

        var createdRegion = regionRepository.save(region);
        log.info("Created new region: {}", createdRegion);
        return createdRegion;
    }

    /**
     * Validates the values of the given region and checks if a region with the same name already exists.
     * @param region The region to validate
     * @throws RegionConstraintException If the region has invalid values
     * @throws RegionAlreadyExistsException If a region with the same name already exists
     */
    private void validateRegion(Region region) throws RegionConstraintException, RegionAlreadyExistsException
    {
        var violations = region.validate();
        if (!violations.isEmpty())
            throw new RegionConstraintException(region, violations);

        var regionWithSameName = regionRepository.findByName(region.getName());
        if (regionWithSameName.isPresent() && !regionWithSameName.get().getId().equals(region.getId()))
            throw new RegionAlreadyExistsException(region);
    }

    private List<Region> loggedRegions(List<Region> regions)
    {
        log.debug("Found {} regions", regions.size());
        if (log.isTraceEnabled()) regions.forEach(region -> log.trace("- {}", region));
        return regions;
    }
}
