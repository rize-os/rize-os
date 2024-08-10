package rize.os.access.manager.client;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.keycloak.representations.idm.ClientRepresentation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ClientMapper
{
    @Nonnull
    Client toClient(@Nonnull ClientRepresentation clientRepresentation)
    {
        var organizationId = getOrganizationId(clientRepresentation).orElse(null);
        return Client.builder()
                .id(clientRepresentation.getId())
                .clientId(clientRepresentation.getClientId())
                .name(clientRepresentation.getName())
                .organizationId(organizationId)
                .redirectUris(clientRepresentation.getRedirectUris())
                .build();
    }

    @Nonnull
    Optional<String> getOrganizationId(@Nonnull ClientRepresentation clientRepresentation)
    {
        if (clientRepresentation.getAttributes() == null || !clientRepresentation.getAttributes().containsKey(Client.ORGANIZATION_ID_ATTRIBUTE))
            return Optional.empty();

        return Optional.of(clientRepresentation.getAttributes().get(Client.ORGANIZATION_ID_ATTRIBUTE));
    }

    @Nonnull
    Map<String, String> getAttributesForClient(@Nonnull Client client, @Nullable Map<String, String> existingAttributes)
    {
        if (existingAttributes == null)
            existingAttributes = new HashMap<>();

        if (client.getOrganizationId() != null)
            existingAttributes.put(Client.ORGANIZATION_ID_ATTRIBUTE, client.getOrganizationId());

        return existingAttributes;
    }
}
