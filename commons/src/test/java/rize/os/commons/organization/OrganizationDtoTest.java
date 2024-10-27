package rize.os.commons.organization;

import org.apache.avro.Schema;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationDtoTest
{
    @Test
    void shouldCreateOrganizationDtoFromBuilder()
    {
        var id = UUID.randomUUID().toString();
        var organizationDto = OrganizationDto.builder()
            .id(id)
            .name("org-name")
            .displayName("Org Name")
            .region("de")
            .enabled(true)
            .build();

        assertThat(organizationDto.getId()).isEqualTo(id);
        assertThat(organizationDto.get(0)).isEqualTo(id);
        assertThat(organizationDto.getName()).isEqualTo("org-name");
        assertThat(organizationDto.get(1)).isEqualTo("org-name");
        assertThat(organizationDto.getDisplayName()).isEqualTo("Org Name");
        assertThat(organizationDto.get(2)).isEqualTo("Org Name");
        assertThat(organizationDto.getRegion()).isEqualTo("de");
        assertThat(organizationDto.get(3)).isEqualTo("de");
        assertThat(organizationDto.isEnabled()).isTrue();
        assertThat(organizationDto.get(4)).isEqualTo(Boolean.TRUE);
    }

    @Test
    void shouldCreateOrganizationDtoFromAvroValues()
    {
        var id = UUID.randomUUID().toString();
        var organizationDto = new OrganizationDto();
        organizationDto.put(0, id);
        organizationDto.put(1, "org-name");
        organizationDto.put(2, "Org Name");
        organizationDto.put(3, "de");
        organizationDto.put(4, true);

        assertThat(organizationDto.getId()).isEqualTo(id);
        assertThat(organizationDto.get(0)).isEqualTo(id);
        assertThat(organizationDto.getName()).isEqualTo("org-name");
        assertThat(organizationDto.get(1)).isEqualTo("org-name");
        assertThat(organizationDto.getDisplayName()).isEqualTo("Org Name");
        assertThat(organizationDto.get(2)).isEqualTo("Org Name");
        assertThat(organizationDto.getRegion()).isEqualTo("de");
        assertThat(organizationDto.get(3)).isEqualTo("de");
        assertThat(organizationDto.isEnabled()).isTrue();
        assertThat(organizationDto.get(4)).isEqualTo(Boolean.TRUE);
    }

    @Test
    void shouldReturnSchema()
    {
        var organizationDto = new OrganizationDto();
        var schema = organizationDto.getSchema();

        assertThat(schema).isNotNull();
        assertThat(schema.getType()).isEqualTo(Schema.Type.RECORD);
        assertThat(schema.getName()).isEqualTo("OrganizationDto");
        assertThat(schema.getNamespace()).isEqualTo("rize.os.commons.organization");
        assertThat(schema.getFields()).hasSize(5);
        assertThat(schema.getFields().getFirst().name()).isEqualTo("id");
        assertThat(schema.getFields().getFirst().schema().getType()).isEqualTo(Schema.Type.STRING);
        assertThat(schema.getFields().get(1).name()).isEqualTo("name");
        assertThat(schema.getFields().get(1).schema().getType()).isEqualTo(Schema.Type.STRING);
        assertThat(schema.getFields().get(2).name()).isEqualTo("displayName");
        assertThat(schema.getFields().get(2).schema().getType()).isEqualTo(Schema.Type.STRING);
        assertThat(schema.getFields().get(3).name()).isEqualTo("region");
        assertThat(schema.getFields().get(3).schema().getType()).isEqualTo(Schema.Type.STRING);
        assertThat(schema.getFields().get(4).name()).isEqualTo("enabled");
        assertThat(schema.getFields().get(4).schema().getType()).isEqualTo(Schema.Type.BOOLEAN);
    }
}