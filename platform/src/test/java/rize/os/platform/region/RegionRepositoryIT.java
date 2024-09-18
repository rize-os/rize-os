package rize.os.platform.region;

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
class RegionRepositoryIT
{
    @Autowired
    private RegionRepository regionRepository;

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }


    @Test
    void shouldCreateFindAndDeleteRegion()
    {
        var regionToCreate = Region.builder()
                .id(UUID.randomUUID())
                .name("should-create-find-and-delete-region")
                .displayName("shouldCreateFindAndDeleteRegion")
                .build();

        assertThat(regionRepository.findById(regionToCreate.getId())).isEmpty();

        var createdRegion = regionRepository.save(regionToCreate);
        assertThat(createdRegion).isNotNull();
        assertThat(createdRegion.getName()).isEqualTo(regionToCreate.getName());
        assertThat(createdRegion.getDisplayName()).isEqualTo(regionToCreate.getDisplayName());

        var foundRegion = regionRepository.findById(createdRegion.getId());
        assertThat(foundRegion).isPresent();
        assertThat(foundRegion.get().getName()).isEqualTo(regionToCreate.getName());
        assertThat(foundRegion.get().getDisplayName()).isEqualTo(regionToCreate.getDisplayName());

        regionRepository.delete(createdRegion);
        var deletedRegion = regionRepository.findById(createdRegion.getId());
        assertThat(deletedRegion).isEmpty();
    }

    @Test
    void shouldFailToCreateRegionWithSameName()
    {
        var regionToCreate = Region.builder()
                .id(UUID.randomUUID())
                .name("should-fail-to-create-region-with-same-name")
                .displayName("shouldFailToCreateRegionWithSameName1")
                .build();

        var createdRegion = regionRepository.save(regionToCreate);
        assertThat(createdRegion).isNotNull();
        assertThat(createdRegion.getName()).isEqualTo(regionToCreate.getName());
        assertThat(createdRegion.getDisplayName()).isEqualTo(regionToCreate.getDisplayName());

        var regionToCreateWithSameName = Region.builder()
                .id(UUID.randomUUID())
                .name("should-fail-to-create-region-with-same-name")
                .displayName("shouldFailToCreateRegionWithSameName2")
                .build();

        assertThatThrownBy(() -> regionRepository.save(regionToCreateWithSameName))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldFindRegionByName()
    {
        var regionToCreate = Region.builder()
                .id(UUID.randomUUID())
                .name("should-find-region-by-name")
                .displayName("shouldFindRegionByName")
                .build();

        assertThat(regionRepository.findByName(regionToCreate.getName())).isEmpty();

        var createdRegion = regionRepository.save(regionToCreate);
        assertThat(createdRegion).isNotNull();
        assertThat(createdRegion.getName()).isEqualTo(regionToCreate.getName());
        assertThat(createdRegion.getDisplayName()).isEqualTo(regionToCreate.getDisplayName());

        var foundRegion = regionRepository.findByName(createdRegion.getName());
        assertThat(foundRegion).isPresent();
        assertThat(foundRegion.get().getName()).isEqualTo(regionToCreate.getName());
        assertThat(foundRegion.get().getDisplayName()).isEqualTo(regionToCreate.getDisplayName());
    }
}