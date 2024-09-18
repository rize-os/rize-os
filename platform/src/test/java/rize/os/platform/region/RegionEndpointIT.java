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