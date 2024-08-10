package rize.os.access.manager.client.exceptions;

import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rize.os.access.manager.client.Client;

@Slf4j
@Getter
public class ClientCreateException extends RuntimeException
{
    private final Client client;
    private Response response;

    public ClientCreateException(Client client, Response response)
    {
        super("Failed to create client: " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase() + " - " + response.readEntity(String.class));

        log.warn(getMessage());
        this.client = client;
        this.response = response;
    }

    public ClientCreateException(Client client, Throwable cause)
    {
        super("Failed to create client", cause);

        log.warn(getMessage());
        this.client = client;
    }

    public ClientCreateException(Client client, String message)
    {
        super("Failed to create client: " + message);

        log.warn(getMessage());
        this.client = client;
    }
}
