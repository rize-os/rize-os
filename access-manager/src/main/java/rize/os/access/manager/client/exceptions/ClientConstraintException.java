package rize.os.access.manager.client.exceptions;

import jakarta.annotation.Nonnull;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rize.os.access.manager.client.Client;

import java.util.Collection;

@Slf4j
@Getter
public class ClientConstraintException extends IllegalArgumentException
{
    private final Client client;
    private final Collection<ConstraintViolation<Client>> violations;

    public ClientConstraintException(@Nonnull Client client, @Nonnull Collection<ConstraintViolation<Client>> violations)
    {
        super("Invalid values in " + client + ": " +
                violations.stream()
                        .map(v -> v.getPropertyPath() + " " + v.getMessage())
                        .reduce((a, b) -> a + ", " + b).orElse(""));

        log.warn(getMessage());
        this.client = client;
        this.violations = violations;
    }
}
