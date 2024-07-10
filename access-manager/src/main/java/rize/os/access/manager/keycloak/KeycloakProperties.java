package rize.os.access.manager.keycloak;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties
{
    private String url;
    private KeycloakAdminProperties admin;

    @Override
    public String toString()
    {
        return "{" + "url='" + url + '\'' +
                ", admin=" + admin + '}';
    }

    @Getter
    @Setter
    static class KeycloakAdminProperties
    {
        private String realm;
        private String grantType;
        private String clientId;
        private String clientSecret;
        private String username;
        private String password;

        @Override
        public String toString()
        {
            return "{" + "realm='" + realm + '\'' +
                    ", grantType='" + grantType + '\'' +
                    ", clientId='" + clientId + '\'' +
                    ", clientSecret='*****'" +
                    ", username='" + username + '\'' +
                    ", password='*****'" +
                    '}';
        }
    }
}
