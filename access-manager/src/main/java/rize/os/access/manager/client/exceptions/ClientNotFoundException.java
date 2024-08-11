package rize.os.access.manager.client.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientNotFoundException extends IllegalArgumentException
{
    public ClientNotFoundException(String id)
    {
        super("Client not found with id: " + id);
        log.warn(getMessage());
    }
}
