package rize.os.cockpit;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration
{
    private static final String POSTGRES_IMAGE = "postgres:17.5";

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer()
    {
        return new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE));
    }

    /*
    @Bean
    @ServiceConnection
    PulsarContainer pulsarContainer()
    {
        return new PulsarContainer(DockerImageName.parse("apachepulsar/pulsar:latest"));
    }
    */
}
