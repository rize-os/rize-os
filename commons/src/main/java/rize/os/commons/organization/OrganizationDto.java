package rize.os.commons.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDto implements Serializable
{
    private String id;
    private String name;
    private String displayName;
    private String region;
    private boolean enabled;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganizationDto that = (OrganizationDto) o;
        return Objects.equals(name, that.name) && Objects.equals(displayName, that.displayName) && Objects.equals(region, that.region);
    }

    @Override
    public int hashCode()
    {
        int result = Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(displayName);
        result = 31 * result + Objects.hashCode(region);
        return result;
    }

    @Override
    public String toString()
    {
        return "OrganizationDto{" + "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", region='" + region + '\'' +
                ", enabled=" + enabled + '}';
    }
}
