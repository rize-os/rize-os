package rize.os.platform.keycloak;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakConfiguration
{
    @Bean
    Keycloak keycloak(KeycloakProperties keycloakProperties)
    {
        log.info("Initialing Keycloak Client with properties: {}", keycloakProperties);
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getUrl())
                .realm(keycloakProperties.getAdmin().getRealm())
                .grantType(keycloakProperties.getAdmin().getGrantType())
                .clientId(keycloakProperties.getAdmin().getClientId())
                .clientSecret(keycloakProperties.getAdmin().getClientSecret())
                .username(keycloakProperties.getAdmin().getUsername())
                .password(keycloakProperties.getAdmin().getPassword())
                .build();
    }

    @Bean
    RealmResource realmResource(Keycloak keycloak, KeycloakProperties keycloakProperties)
    {
        return keycloak.realm(keycloakProperties.getRealm());
    }

    @Bean
    CommandLineRunner testKeycloakConnection(KeycloakProperties keycloakProperties, RealmResource realmResource)
    {
        return args ->
        {
            log.debug("Testing connection to Keycloak server '{}'", keycloakProperties.getUrl());
            RealmRepresentation realm = null;

            try {
                realm = realmResource.toRepresentation();
            }
            catch (ProcessingException e) {
                log.error("Could not connect to Keycloak server '{}': {}", keycloakProperties.getUrl(), e.getMessage());
            }
            catch (NotFoundException e) {
                log.error("Could not find realm \"{}\" in Keycloak: {}", keycloakProperties.getRealm(), e.getMessage());
            }

            if (realm != null)
            {
                log.debug("Test connection to Keycloak server was successful");
                return;
            }

            log.error("Platform is shutting down due to Keycloak connection error");
            System.exit(1);
        };
    }
}
