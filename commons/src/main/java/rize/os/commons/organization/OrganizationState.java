package rize.os.commons.organization;

import lombok.*;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationState extends SpecificRecordBase implements Serializable, SpecificRecord
{
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private EventType eventType;

    @Builder.Default
    private String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

    private String source;
    private Payload payload;

    public OrganizationState(OrganizationDto before, OrganizationDto after, EventType type, String source)
    {
        setId(UUID.randomUUID());
        setEventType(type);
        setOffsetTimestamp(OffsetDateTime.now());
        setSource(source);
        setPayload(new Payload(before, after));
    }

    public OffsetDateTime getOffsetTimestamp()
    {
        return OffsetDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
    }

    public void setOffsetTimestamp(OffsetDateTime timestamp)
    {
        this.timestamp = timestamp.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    public void put(int field, Object value)
    {
        switch (field)
        {
            case 0 -> id = (UUID) value;
            case 1 -> eventType = EventType.valueOf((String) value);
            case 2 -> timestamp = (String) value;
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
            case 1 -> eventType;
            case 2 -> timestamp;
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

    public enum EventType
    {
        CREATED,
        UPDATED,
        DELETED
    }

    @Data
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload implements Serializable
    {
        private OrganizationDto before;
        private OrganizationDto after;
    }

    public static final Schema SCHEMA = new Schema.Parser().parse("""
        {
          "type": "record",
          "name": "OrganizationState",
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
              "name": "eventType",
              "type": {
                "type": "enum",
                "name": "EventType",
                "symbols": ["CREATED", "UPDATED", "DELETED"]
              }
            },
            {
              "name": "timestamp",
              "type": "string"
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
