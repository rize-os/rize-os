package rize.os.cockpit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class RizeCockpitTests
{
    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @Test
    void contextLoads()
    {
    }
}
