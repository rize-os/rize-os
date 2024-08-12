package rize.os.access.manager.client;

import com.vaadin.hilla.Nullable;
import jakarta.annotation.Nonnull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client
{
    static final String ORGANIZATION_ID_ATTRIBUTE = "organizationId";

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private String id;

    @NotBlank
    private String clientId;

    @NotNull
    @Builder.Default
    private String name = "";

    @Nullable
    private String organizationId;

    @NotEmpty
    @Builder.Default
    private List<String> redirectUris = new ArrayList<>();


    /**
     * Validates the values of the client object. If the returned list of violations is empty, the values for the client are valid.
     * @return Violations of invalid values in the client object
     */
    @Nonnull
    Set<ConstraintViolation<Client>> validate()
    {
        return validator.validate(this);
    }

    @Override
    public String toString()
    {
        return "Client{" + "id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", name='" + name + '\'' +
                ", organizationId='" + organizationId + '\'' + '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;
        return Objects.equals(id, client.id) &&
                Objects.equals(clientId, client.clientId) &&
                Objects.equals(name, client.name) &&
                Objects.equals(organizationId, client.organizationId) &&
                Objects.equals(redirectUris, client.redirectUris);
    }

    @Override
    public int hashCode()
    {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(clientId);
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(organizationId);
        result = 31 * result + Objects.hashCode(redirectUris);
        return result;
    }
}
