package rize.os.commons.organization;

import org.apache.avro.Schema;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationEventTest
{
    @Test
    void shouldCreateOrganizationEventFromBuilder()
    {
        var organizationDto = OrganizationDto.builder()
                .id(UUID.randomUUID().toString())
                .name("org-name")
                .displayName("Org Name")
                .region("de")
                .enabled(true)
                .build();

        var payload = new OrganizationEvent.Payload(null, organizationDto);
        var organizationEvent = OrganizationEvent.builder()
                .type(OrganizationEvent.Type.CREATED)
                .source("source")
                .payload(payload)
                .build();

        assertThat(organizationEvent.getId()).isNotNull();
        assertThat(organizationEvent.get(0)).isNotNull();
        assertThat(organizationEvent.getTimestamp()).isNotNull();
        assertThat(organizationEvent.get(1)).isNotNull();
        assertThat(organizationEvent.getType()).isEqualTo(OrganizationEvent.Type.CREATED);
        assertThat(organizationEvent.get(2)).isEqualTo(OrganizationEvent.Type.CREATED);
        assertThat(organizationEvent.getSource()).isEqualTo("source");
        assertThat(organizationEvent.get(3)).isEqualTo("source");
        assertThat(organizationEvent.getPayload()).isEqualTo(payload);
        assertThat(organizationEvent.get(4)).isEqualTo(payload);
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

        var organizationEvent = new OrganizationEvent(null, organizationDto, OrganizationEvent.Type.CREATED, "source");

        assertThat(organizationEvent.getId()).isNotNull();
        assertThat(organizationEvent.get(0)).isNotNull();
        assertThat(organizationEvent.getTimestamp()).isNotNull();
        assertThat(organizationEvent.get(1)).isNotNull();
        assertThat(organizationEvent.getType()).isEqualTo(OrganizationEvent.Type.CREATED);
        assertThat(organizationEvent.get(2)).isEqualTo(OrganizationEvent.Type.CREATED);
        assertThat(organizationEvent.getSource()).isEqualTo("source");
        assertThat(organizationEvent.get(3)).isEqualTo("source");
        assertThat(organizationEvent.getPayload().before()).isNull();
        assertThat(organizationEvent.getPayload().after()).isEqualTo(organizationDto);
        assertThat(organizationEvent.get(4)).asInstanceOf(InstanceOfAssertFactories.type(OrganizationEvent.Payload.class)).satisfies(payload -> {
            assertThat(payload.before()).isNull();
            assertThat(payload.after()).isEqualTo(organizationDto);
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

        var payload = new OrganizationEvent.Payload(null, organizationDto);
        var organizationEvent = new OrganizationEvent();
        organizationEvent.put(2, OrganizationEvent.Type.CREATED.name());
        organizationEvent.put(3, "source");
        organizationEvent.put(4, payload);

        assertThat(organizationEvent.getId()).isNotNull();
        assertThat(organizationEvent.get(0)).isNotNull();
        assertThat(organizationEvent.getTimestamp()).isNotNull();
        assertThat(organizationEvent.get(1)).isNotNull();
        assertThat(organizationEvent.getType()).isEqualTo(OrganizationEvent.Type.CREATED);
        assertThat(organizationEvent.get(2)).isEqualTo(OrganizationEvent.Type.CREATED);
        assertThat(organizationEvent.getSource()).isEqualTo("source");
        assertThat(organizationEvent.get(3)).isEqualTo("source");
        assertThat(organizationEvent.getPayload()).isEqualTo(payload);
        assertThat(organizationEvent.get(4)).isEqualTo(payload);
    }

    @Test
    void shouldReturnSchema()
    {
        var organizationEvent = new OrganizationEvent();
        var schema = organizationEvent.getSchema();

        assertThat(schema).isNotNull();
        assertThat(schema.getType()).isEqualTo(Schema.Type.RECORD);
        assertThat(schema.getName()).isEqualTo("OrganizationEvent");
        assertThat(schema.getNamespace()).isEqualTo("rize.os.commons.organization");
        assertThat(schema.getFields()).hasSize(5);
        assertThat(schema.getFields().getFirst().name()).isEqualTo("id");
        assertThat(schema.getFields().getFirst().schema().getType()).isEqualTo(Schema.Type.STRING);
        assertThat(schema.getFields().get(1).name()).isEqualTo("timestamp");
        assertThat(schema.getFields().get(1).schema().getType()).isEqualTo(Schema.Type.LONG);
        assertThat(schema.getFields().get(2).name()).isEqualTo("type");
        assertThat(schema.getFields().get(2).schema().getType()).isEqualTo(Schema.Type.ENUM);
        assertThat(schema.getFields().get(2).schema().getEnumSymbols()).contains(Arrays.stream(OrganizationEvent.Type.values()).map(Enum::name).toArray(String[]::new));
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