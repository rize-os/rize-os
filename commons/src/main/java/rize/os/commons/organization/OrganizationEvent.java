package rize.os.commons.organization;

import lombok.*;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationEvent extends SpecificRecordBase implements Serializable, SpecificRecord
{
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();

    private Type type;
    private String source;
    private Payload payload;

    public OrganizationEvent(OrganizationDto before, OrganizationDto after, Type type, String source)
    {
        setId(UUID.randomUUID());
        setTimestamp(OffsetDateTime.now());
        setPayload(new Payload(before, after));
        setType(type);
        setSource(source);
    }

    @Override
    public void put(int field, Object value)
    {
        switch (field)
        {
            case 0 -> id = (UUID) value;
            case 1 -> timestamp = (OffsetDateTime) value;
            case 2 -> type = Type.valueOf((String) value);
            case 3 -> source = (String) value;
            case 4 -> payload = (Payload) value;
            default -> throw new IndexOutOfBoundsException("Invalid index: " + field);
        }
    }

    @Override
    public Object get(int field)
    {
        return switch (field)
        {
            case 0 -> id;
            case 1 -> timestamp;
            case 2 -> type;
            case 3 -> source;
            case 4 -> payload;
            default -> throw new IndexOutOfBoundsException("Invalid index: " + field);
        };
    }

    @Override
    public Schema getSchema()
    {
        return SCHEMA;
    }

    public enum Type
    {
        CREATED,
        UPDATED,
        DELETED
    }

    public record Payload(OrganizationDto before, OrganizationDto after)
    {
    }

    public static final Schema SCHEMA = new Schema.Parser().parse("""
        {
          "type": "record",
          "name": "OrganizationEvent",
          "namespace": "rize.os.commons.organization",
          "fields": [
            {
              "name": "id",
              "type": {
                "type": "string",
                "logicalType": "uuid"
              },
              "default": ""
            },
            {
              "name": "timestamp",
              "type": {
                "type": "long",
                "logicalType": "timestamp-millis"
              }
            },
            {
              "name": "type",
              "type": {
                "type": "enum",
                "name": "Type",
                "symbols": ["CREATED", "UPDATED", "DELETED"]
              }
            },
            {
              "name": "source",
              "type": ["null", "string"]
            },
            {
              "name": "payload",
              "type": {
                "type": "record",
                "name": "Payload",
                "fields": [
                  {
                    "name": "before",
                    "type": [
                      "null",
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
                    ],
                    "default": null
                  },
                  {
                    "name": "after",
                    "type": ["null", "rize.os.commons.organization.OrganizationDto"],
                    "default": null
                  }
                ]
              }
            }
          ]
        }
    """);
}
