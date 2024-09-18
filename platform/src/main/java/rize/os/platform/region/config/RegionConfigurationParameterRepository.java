package rize.os.platform.region.config;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.lang.NonNull;

interface RegionConfigurationParameterRepository extends ListCrudRepository<RegionConfigurationParameter, String>
{
    @Override
    default void delete(@NonNull RegionConfigurationParameter entity) {
        // Configuration parameters cannot be deleted
    }

    @Override
    default void deleteAll(@NonNull Iterable<? extends RegionConfigurationParameter> entities) {
        // Configuration parameters cannot be deleted
    }

    @Override
    default void deleteAll() {
        // Configuration parameters cannot be deleted
    }

    @Override
    default void deleteById(@NonNull String id) {
        // Configuration parameters cannot be deleted
    }

    @Override
    default void deleteAllById(@NonNull Iterable<? extends String> ids) {
        // Configuration parameters cannot be deleted
    }
}
