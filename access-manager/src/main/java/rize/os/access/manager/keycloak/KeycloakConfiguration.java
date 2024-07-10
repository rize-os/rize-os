package rize.os.access.manager.keycloak;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
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
}
