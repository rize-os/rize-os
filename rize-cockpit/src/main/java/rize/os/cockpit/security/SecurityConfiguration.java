package rize.os.cockpit.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import rize.os.security.oauth2.client.AuthenticatedUserOidcService;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration
{
    @Bean
    JwtDecoder jwtDecoder(@Value("${rize.cockpit.security.oauth2.issuer-uri:http://localhost:3000/realms/administration}") String issuerUri)
    {
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticatedUserOidcService authenticatedOidcUserService) throws Exception
    {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().hasRole(CockpitRoles.PLATFORM_ADMIN))
                .oauth2Login(oauth2Login ->
                        oauth2Login.userInfoEndpoint(userInfo -> userInfo.oidcUserService(authenticatedOidcUserService)));

        return http.build();
    }
}