package rize.os.platform.organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import rize.os.commons.messaging.PulsarMessagingConfiguration;

@Slf4j
@Component
@RequiredArgsConstructor
public class PulsarTenantHandler
{
    private final PulsarMessagingConfiguration pulsarMessagingConfiguration;

    @ApplicationModuleListener
    void onOrganizationCreated(OrganizationCreatedEvent event) throws PulsarAdminException
    {
        log.info("Creating new tenant for organization \"{}\" in Pulsar", event.organization().getName());
        pulsarMessagingConfiguration.createTenant(event.organization().getName());
    }

    @ApplicationModuleListener
    void onOrganizationDeleted(OrganizationDeletedEvent event) throws PulsarAdminException
    {
        log.info("Deleting tenant for organization \"{}\" in Pulsar", event.organization().getName());
        pulsarMessagingConfiguration.deleteTenant(event.organization().getName());
    }
}
