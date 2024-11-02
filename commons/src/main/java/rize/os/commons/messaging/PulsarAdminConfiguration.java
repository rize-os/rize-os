package rize.os.commons.messaging;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.core.PulsarAdministration;

@Configuration
public class PulsarAdminConfiguration
{
    @Bean
    PulsarAdmin pulsarAdmin(PulsarAdministration pulsarAdministration) throws PulsarClientException
    {
        return pulsarAdministration.createAdminClient();
    }
}
