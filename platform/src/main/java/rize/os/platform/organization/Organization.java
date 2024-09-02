package rize.os.platform.organization;

import jakarta.annotation.Nonnull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization
{
    static final String DISPLAY_NAME_ATTRIBUTE = "displayName";
    static final String REGION_ATTRIBUTE = "region";
    static final String NAME_PATTERN = "^[a-z0-9][a-z0-9-]{0,62}[a-z0-9]$";

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private String id;

    @Pattern(regexp = NAME_PATTERN)
    private String name;

    @NotBlank
    private String displayName;

    @NotBlank
    private String region;

    @Builder.Default
    private boolean enabled = true;


    /**
     * Validates the values of the organization object. If the returned list of violations is empty, the values for the organization are valid.
     * @return Violations of invalid values in the organization object
     */
    @Nonnull
    Set<ConstraintViolation<Organization>> validate()
    {
        return validator.validate(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Organization that = (Organization) o;
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
        return "Organization{" + "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", region='" + region + '\'' +
                ", enabled=" + enabled + '}';
    }
}
