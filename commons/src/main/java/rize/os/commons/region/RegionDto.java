package rize.os.commons.region;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RegionDto implements Serializable
{
    private UUID id;
    private String name;
    private String displayName;
}
