package rize.os.platform.region;

import jakarta.annotation.Nonnull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@ToString(exclude = "version")
@EqualsAndHashCode(exclude = "version")
@NoArgsConstructor
@AllArgsConstructor
public class Region
{
    static final String NAME_PATTERN = "^[a-z0-9][a-z0-9-]{0,62}[a-z0-9]$";

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Id
    @NotNull
    private UUID id;

    @Pattern(regexp = NAME_PATTERN)
    private String name;

    @NotBlank
    private String displayName;

    @Version
    private Long version;


    /**
     * Validates the values of the region object. If the returned list of violations is empty, the values for the region are valid.
     * @return Violations of invalid values in the region object
     */
    @Nonnull
    Set<ConstraintViolation<Region>> validate()
    {
        return validator.validate(this);
    }
}
