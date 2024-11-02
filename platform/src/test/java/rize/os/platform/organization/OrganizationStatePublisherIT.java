package rize.os.platform.organization;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Schema;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.pulsar.core.PulsarConsumerFactory;
import org.springframework.pulsar.test.support.PulsarConsumerTestUtil;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.commons.messaging.PulsarMessagingConfiguration;
import rize.os.commons.organization.OrganizationState;
import rize.os.platform.TestcontainersConfiguration;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class OrganizationStatePublisherIT
{
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private PulsarConsumerFactory<OrganizationState> pulsarConsumerFactory;

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @Test
    void shouldPublishOrganizationStateEvents() throws InterruptedException
    {
        // Create
        var organizationToCreate = Organization.builder()
                .name("organization-state-publisher")
                .displayName("Organization State Publisher")
                .region("de")
                .enabled(true)
                .build();
        var createdOrganization = organizationService.createOrganization(organizationToCreate);
        var createdOrganizationDto = organizationMapper.toOrganizationDto(createdOrganization);

        Thread.sleep(100);

        // Update
        createdOrganization.setDisplayName("Organization State Publisher Updated");
        var updatedOrganization = organizationService.updateOrganization(createdOrganization);
        var updatedOrganizationDto = organizationMapper.toOrganizationDto(updatedOrganization);

        Thread.sleep(100);

        // Delete
        organizationService.deleteOrganization(updatedOrganization.getId());

        List<Message<OrganizationState>> messages = PulsarConsumerTestUtil.consumeMessages(pulsarConsumerFactory)
                .fromTopic(PulsarMessagingConfiguration.ORGANIZATION_STATE_TOPIC)
                .withSchema(Schema.AVRO(OrganizationState.class))
                .awaitAtMost(Duration.ofSeconds(5))
                .until(msg -> msg.size() == 3)
                .get();

        assertThat(messages).hasSize(3);
        //var createMessage = messages.stream().filter(msg -> msg.getValue().getEventType() == OrganizationState.EventType.CREATED).findFirst().orElseThrow();
        var createMessage = messages.getFirst();
        assertThat(createMessage.getKey()).isEqualTo(createdOrganization.getId());
        assertThat(createMessage.getValue().getPayload().getBefore()).isNull();
        assertThat(createMessage.getValue().getPayload().getAfter()).isEqualTo(createdOrganizationDto);

//        var updateMessage = messages.stream().filter(msg -> msg.getValue().getEventType() == OrganizationState.EventType.UPDATED).findFirst().orElseThrow();
        var updateMessage = messages.get(1);
        assertThat(updateMessage.getKey()).isEqualTo(updatedOrganization.getId());
        assertThat(updateMessage.getValue().getPayload().getBefore()).isEqualTo(createdOrganizationDto);
        assertThat(updateMessage.getValue().getPayload().getAfter()).isEqualTo(updatedOrganizationDto);

//        var deleteMessage = messages.stream().filter(msg -> msg.getValue().getEventType() == OrganizationState.EventType.DELETED).findFirst().orElseThrow();
        var deleteMessage = messages.get(2);
        assertThat(deleteMessage.getKey()).isEqualTo(updatedOrganization.getId());
        assertThat(deleteMessage.getValue().getPayload().getBefore()).isEqualTo(updatedOrganizationDto);
        assertThat(deleteMessage.getValue().getPayload().getAfter()).isNull();
    }
}