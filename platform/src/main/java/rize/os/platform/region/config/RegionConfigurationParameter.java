package rize.os.platform.region.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionConfigurationParameter
{
    @Id
    @Pattern(regexp = "^[a-z0-9][a-z0-9-]{0,62}[a-z0-9]$")
    private String name;

    @NotBlank
    private String value;

    @Version
    private Long version;
}
