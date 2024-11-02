package rize.os.commons.organization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;

@Data
@Builder
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDto extends SpecificRecordBase implements Serializable, SpecificRecord
{
    private String id;
    private String name;
    private String displayName;
    private String region;
    private boolean enabled;

    @Override
    public void put(int field, Object value)
    {
        switch (field)
        {
            case 0: id = (String) value; break;
            case 1: name = (String) value; break;
            case 2: displayName = (String) value; break;
            case 3: region = (String) value; break;
            case 4: enabled = (boolean) value; break;
            default: throw new IndexOutOfBoundsException("Invalid index: " + field);
        }
    }

    @Override
    public Object get(int field)
    {
        return switch (field)
        {
            case 0 -> id;
            case 1 -> name;
            case 2 -> displayName;
            case 3 -> region;
            case 4 -> enabled;
            default -> throw new IndexOutOfBoundsException("Invalid index: " + field);
        };
    }

    @Override
    @JsonIgnore
    public Schema getSchema()
    {
        return SCHEMA;
    }

    public static final Schema SCHEMA = new Schema.Parser().parse("""
        {
          "type": "record",
          "name": "OrganizationDto",
          "namespace": "rize.os.commons.organization",
          "fields": [
            { "name": "id", "type": "string" },
            { "name": "name", "type": "string" },
            { "name": "displayName", "type": "string" },
            { "name": "region", "type": "string" },
            { "name": "enabled", "type": "boolean" }
          ]
        }
    """);
}
