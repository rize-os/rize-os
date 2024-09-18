package rize.os.platform.region;

import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;
import java.util.UUID;

interface RegionRepository extends ListCrudRepository<Region, UUID>
{
    Optional<Region> findByName(String name);
}
