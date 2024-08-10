package rize.os.access.manager.client.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rize.os.access.manager.client.Client;

@Slf4j
@Getter
public class ClientUpdateException extends RuntimeException
{
    private final Client client;

    public ClientUpdateException(Client client, Throwable cause)
    {
        super("Failed to update client: " + cause.getMessage());

        log.warn(getMessage());
        this.client = client;
    }
}
