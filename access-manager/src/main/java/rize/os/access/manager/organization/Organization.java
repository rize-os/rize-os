package rize.os.access.manager.organization;

import jakarta.annotation.Nonnull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization
{
    static final String DISPLAY_NAME_ATTRIBUTE = "displayName";
    static final String ALIASES_ATTRIBUTE = "aliases";
    static final String IMAGE_ID_ATTRIBUTE = "imageId";

    static final String NAME_PATTERN = "^[a-z0-9][a-z0-9-]{0,62}[a-z0-9]$";
    static final String ALIAS_PATTERN = NAME_PATTERN;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private String id;

    @NotBlank
    @Pattern(regexp = NAME_PATTERN)
    private String name;

    @NotBlank
    private String displayName;

    @NotEmpty
    @Singular
    private List<@Pattern(regexp = ALIAS_PATTERN) String> aliases;

    private UUID imageId;

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

    /**
     * Returns a description of the organization object that contains the display name and all aliases.
     * @return Description of the organization object
     */
    @Nonnull
    String getDescription()
    {
        return displayName + " " + aliases;
    }

    @Override
    public String toString()
    {
        return "Organization{" + "id='" + id + '\'' + ", " +
                "name='" + name + '\'' + ", " +
                "aliases=" + aliases + ", " +
                "displayName='" + displayName + '\'' + ", " +
                "enabled=" + enabled + '}';
    }
}