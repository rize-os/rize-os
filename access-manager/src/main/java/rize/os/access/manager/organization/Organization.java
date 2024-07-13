package rize.os.access.manager.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Organization
{
    private String id;

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    @Pattern(regexp = "^[a-z0-9-]*$")
    private String alias;

    @NotEmpty
    @Builder.Default
    private List<Domain> domains = new ArrayList<>();

    @Builder.Default
    private boolean enabled = true;

    static String nameToAlias(String name)
    {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Domain
    {
        private String name;

        @Builder.Default
        private boolean verified = false;

        @Override
        public String toString()
        {
            return name;
        }
    }
}