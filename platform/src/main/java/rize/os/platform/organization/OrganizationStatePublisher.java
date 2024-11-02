package rize.os.platform.organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.impl.schema.AvroSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Component;
import rize.os.commons.messaging.PulsarMessagingConfiguration;
import rize.os.commons.organization.OrganizationState;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrganizationStatePublisher
{
    private final PulsarTemplate<OrganizationState> pulsarTemplate;
    private final OrganizationMapper organizationMapper;

    @Value("${spring.application.name}")
    private String applicationName;

    @ApplicationModuleListener
    void onOrganizationCreated(OrganizationCreatedEvent event)
    {
        publishOrganizationState(null, event.organization(), OrganizationState.EventType.CREATED, event.organization().getId());
    }

    @ApplicationModuleListener
    void onOrganizationUpdated(OrganizationUpdatedEvent event)
    {
        publishOrganizationState(event.before(), event.after(), OrganizationState.EventType.UPDATED, event.after().getId());
    }

    @ApplicationModuleListener
    void onOrganizationDeleted(OrganizationDeletedEvent event)
    {
        publishOrganizationState(event.organization(), null, OrganizationState.EventType.DELETED, event.organization().getId());
    }

    private void publishOrganizationState(@Nullable Organization before, @Nullable Organization after, OrganizationState.EventType type, String organizationId)
    {
        var dtoBefore = before != null ? organizationMapper.toOrganizationDto(before) : null;
        var dtoAfter = after != null ? organizationMapper.toOrganizationDto(after) : null;
        var organizationState = new OrganizationState(dtoBefore, dtoAfter, type, applicationName);
        log.debug("Publishing new organization state: {}", organizationState);

        pulsarTemplate
                .newMessage(organizationState)
                .withTopic(PulsarMessagingConfiguration.ORGANIZATION_STATE_TOPIC)
                .withSchema(AvroSchema.of(OrganizationState.class))
                .withMessageCustomizer(mc -> mc.key(organizationId))
                .send();
    }
}
