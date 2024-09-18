package rize.os.platform.region.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.platform.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class RegionConfigurationParameterRepositoryIT
{
    @Autowired
    private RegionConfigurationParameterRepository regionConfigurationParameterRepository;

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }


    @Test
    void shouldNotDeleteAnyConfigurationParameter()
    {
        var countBefore = regionConfigurationParameterRepository.count();
        regionConfigurationParameterRepository.deleteAll();
        var countAfter = regionConfigurationParameterRepository.count();
        assertThat(countAfter).isEqualTo(countBefore);
    }
}