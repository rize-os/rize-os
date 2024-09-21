package rize.os.platform.region;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
     * Returns a region by its id.
     * @param id ID of the region
     * @return Object of the region with the given ID or empty if not found
     */
    Optional<Region> findById(UUID id)
    {
        log.debug("Loading region by id: {}", id);
        var region = regionRepository.findById(id);

        if (region.isEmpty())
        {
            log.debug("Region with id '{}' not found", id);
            return Optional.empty();
        }

        return loggedRegion(region.get());
    }

    /**
     * Returns a region by its name.
     * @param name Name of the region
     * @return Object of the region with the given name or empty if not found
     */
    Optional<Region> findByName(String name)
    {
        log.debug("Loading region by name: \"{}\"", name);
        var region = regionRepository.findByName(name);

        if (region.isEmpty())
        {
            log.debug("Region with name \"{}\" not found", name);
            return Optional.empty();
        }

        log.debug("Found region: {}", region.get());
        return region;
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
     * Updates an existing region. The region name cannot be changed.
     * @param region Object of the region to update
     * @return Object of the updated region
     */
    Region updateRegion(Region region)
    {
        log.debug("Updating region: {}", region);

        var existingRegion = regionRepository.findById(region.getId());
        if (existingRegion.isEmpty())
            throw new RegionNotFoundException(region.getId().toString());

        if (!existingRegion.get().getName().equals(region.getName()))
            throw new RegionUpdateException("Region name cannot be changed");

        region.setVersion(existingRegion.get().getVersion());
        validateRegion(region);

        var updatedRegion = regionRepository.save(region);
        log.info("Updated region: {}", updatedRegion);
        return updatedRegion;
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

    private Optional<Region> loggedRegion(Region region)
    {
        log.debug("Found region: {}", region);
        return Optional.of(region);
    }
}
