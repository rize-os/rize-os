package rize.os.platform.region.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.platform.TestcontainersConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class RegionConfigurationDefaultInitializationIT
{
    @Autowired
    private RegionConfigurationParameterRepository regionConfigurationParameterRepository;

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }


    @Test
    void shouldHaveInitializedDefaultParameters()
    {
        var count = regionConfigurationParameterRepository.count();
        assertEquals(RegionConfigurationEntry.values().length, count);
    }
}