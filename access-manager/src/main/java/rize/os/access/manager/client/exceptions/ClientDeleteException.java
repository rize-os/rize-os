package rize.os.access.manager.client.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rize.os.access.manager.client.Client;

@Slf4j
@Getter
public class ClientDeleteException extends RuntimeException
{
    private final Client client;

    public ClientDeleteException(Client client, Throwable cause)
    {
        super("Failed to delete client", cause);

        log.warn(getMessage());
        this.client = client;
    }
}
