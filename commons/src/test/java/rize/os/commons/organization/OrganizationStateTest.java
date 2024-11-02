package rize.os.commons.organization;

import org.apache.avro.Schema;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationStateTest
{
    @Test
    void shouldCreateOrganizationStateFromBuilder()
    {
        var organizationDto = OrganizationDto.builder()
                .id(UUID.randomUUID().toString())
                .name("org-name")
                .displayName("Org Name")
                .region("de")
                .enabled(true)
                .build();

        var payload = new OrganizationState.Payload(null, organizationDto);
        var organizationState = OrganizationState.builder()
                .eventType(OrganizationState.EventType.CREATED)
                .source("source")
                .payload(payload)
                .build();

        assertThat(organizationState.getId()).isNotNull();
        assertThat(organizationState.get(0)).isNotNull();
        assertThat(organizationState.getEventType()).isEqualTo(OrganizationState.EventType.CREATED);
        assertThat(organizationState.get(1)).isEqualTo(OrganizationState.EventType.CREATED);
        assertThat(organizationState.getOffsetTimestamp()).isNotNull();
        assertThat(OffsetDateTime.parse(organizationState.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME)).isNotNull();
        assertThat(organizationState.get(2)).isNotNull();
        assertThat(organizationState.getSource()).isEqualTo("source");
        assertThat(organizationState.get(3)).isEqualTo("source");
        assertThat(organizationState.getPayload()).isEqualTo(payload);
        assertThat(organizationState.get(4)).isEqualTo(payload);
    }

    @Test
    void shouldCreateOrganizationEventFromConstructor()
    {
        var organizationDto = OrganizationDto.builder()
                .id(UUID.randomUUID().toString())
                .name("org-name")
                .displayName("Org Name")
                .region("de")
                .enabled(true)
                .build();

        var organizationState = new OrganizationState(null, organizationDto, OrganizationState.EventType.CREATED, "source");

        assertThat(organizationState.getId()).isNotNull();
        assertThat(organizationState.get(0)).isNotNull();
        assertThat(organizationState.getEventType()).isEqualTo(OrganizationState.EventType.CREATED);
        assertThat(organizationState.get(1)).isEqualTo(OrganizationState.EventType.CREATED);
        assertThat(organizationState.getOffsetTimestamp()).isNotNull();
        assertThat(OffsetDateTime.parse(organizationState.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME)).isNotNull();
        assertThat(organizationState.get(2)).isNotNull();
        assertThat(organizationState.getSource()).isEqualTo("source");
        assertThat(organizationState.get(3)).isEqualTo("source");
        assertThat(organizationState.getPayload().getBefore()).isNull();
        assertThat(organizationState.getPayload().getAfter()).isEqualTo(organizationDto);
        assertThat(organizationState.get(4)).asInstanceOf(InstanceOfAssertFactories.type(OrganizationState.Payload.class)).satisfies(payload -> {
            assertThat(payload.getBefore()).isNull();
            assertThat(payload.getAfter()).isEqualTo(organizationDto);
        });
    }

    @Test
    void shouldCreateOrganizationEventFromAvroValues()
    {
        var organizationDto = OrganizationDto.builder()
                .id(UUID.randomUUID().toString())
                .name("org-name")
                .displayName("Org Name")
                .region("de")
                .enabled(true)
                .build();

        var payload = new OrganizationState.Payload(null, organizationDto);
        var organizationState = new OrganizationState();
        organizationState.put(1, OrganizationState.EventType.CREATED.name());
        organizationState.put(3, "source");
        organizationState.put(4, payload);

        assertThat(organizationState.getId()).isNotNull();
        assertThat(organizationState.get(0)).isNotNull();
        assertThat(organizationState.getEventType()).isEqualTo(OrganizationState.EventType.CREATED);
        assertThat(organizationState.get(1)).isEqualTo(OrganizationState.EventType.CREATED);
        assertThat(organizationState.getOffsetTimestamp()).isNotNull();
        assertThat(OffsetDateTime.parse(organizationState.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME)).isNotNull();;
        assertThat(organizationState.get(2)).isNotNull();
        assertThat(organizationState.getSource()).isEqualTo("source");
        assertThat(organizationState.get(3)).isEqualTo("source");
        assertThat(organizationState.getPayload()).isEqualTo(payload);
        assertThat(organizationState.get(4)).isEqualTo(payload);
    }

    @Test
    void shouldReturnSchema()
    {
        var organizationState = new OrganizationState();
        var schema = organizationState.getSchema();

        assertThat(schema).isNotNull();
        assertThat(schema.getType()).isEqualTo(Schema.Type.RECORD);
        assertThat(schema.getName()).isEqualTo("OrganizationState");
        assertThat(schema.getNamespace()).isEqualTo("rize.os.commons.organization");
        assertThat(schema.getFields()).hasSize(5);
        assertThat(schema.getFields().getFirst().name()).isEqualTo("id");
        assertThat(schema.getFields().getFirst().schema().getType()).isEqualTo(Schema.Type.STRING);
        assertThat(schema.getFields().get(1).name()).isEqualTo("eventType");
        assertThat(schema.getFields().get(1).schema().getType()).isEqualTo(Schema.Type.ENUM);
        assertThat(schema.getFields().get(1).schema().getEnumSymbols()).contains(Arrays.stream(OrganizationState.EventType.values()).map(Enum::name).toArray(String[]::new));
        assertThat(schema.getFields().get(2).name()).isEqualTo("timestamp");
        assertThat(schema.getFields().get(2).schema().getType()).isEqualTo(Schema.Type.STRING);
        assertThat(schema.getFields().get(3).name()).isEqualTo("source");
        assertThat(schema.getFields().get(3).schema().getTypes()).contains(Schema.create(Schema.Type.NULL), Schema.create(Schema.Type.STRING));
        assertThat(schema.getFields().get(4).name()).isEqualTo("payload");
        assertThat(schema.getFields().get(4).schema().getType()).isEqualTo(Schema.Type.RECORD);
        assertThat(schema.getFields().get(4).schema().getName()).isEqualTo("Payload");
        assertThat(schema.getFields().get(4).schema().getFields()).hasSize(2);
        assertThat(schema.getFields().get(4).schema().getFields().getFirst().name()).isEqualTo("before");
        assertThat(schema.getFields().get(4).schema().getFields().getFirst().schema().getTypes()).contains(Schema.create(Schema.Type.NULL), OrganizationDto.SCHEMA);
        assertThat(schema.getFields().get(4).schema().getFields().get(1).name()).isEqualTo("after");
        assertThat(schema.getFields().get(4).schema().getFields().get(1).schema().getTypes()).contains(Schema.create(Schema.Type.NULL), OrganizationDto.SCHEMA);
    }
}