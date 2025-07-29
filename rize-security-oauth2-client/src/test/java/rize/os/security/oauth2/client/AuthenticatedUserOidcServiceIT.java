package rize.os.security.oauth2.client;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import rize.os.security.commons.AuthenticatedUserClaimsConfiguration;
import rize.os.security.commons.JwtAuthenticatedUserConverter;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthenticatedUserOidcServiceIT
{
    private static final String CLIENT_ID = "rize-test-client";

    @Configuration
    static class TestConfig
    {
        private static final KeyPair keyPair = generateRsaKey();

        @Bean
        public JwtDecoder jwtDecoder()
        {
            return NimbusJwtDecoder
                    .withPublicKey((RSAPublicKey) keyPair.getPublic())
                    .build();
        }

        @Bean
        public AuthenticatedUserClaimsConfiguration authenticatedUserClaimsConfiguration()
        {
            return AuthenticatedUserClaimsConfiguration.createDefault();
        }

        @Bean
        public JwtAuthenticatedUserConverter jwtAuthenticatedUserConverter(AuthenticatedUserClaimsConfiguration config)
        {
            return new JwtAuthenticatedUserConverter(config);
        }

        @Bean
        public AuthenticatedUserOidcService authenticatedUserOidcService(JwtAuthenticatedUserConverter converter, JwtDecoder decoder)
        {
            return new AuthenticatedUserOidcService(converter, decoder);
        }

        public static KeyPair getKeyPair()
        {
            return keyPair;
        }

        private static KeyPair generateRsaKey()
        {
            try
            {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                return keyPairGenerator.generateKeyPair();
            }
            catch (Exception ex)
            {
                throw new IllegalStateException(ex);
            }
        }
    }

    @Autowired
    private AuthenticatedUserOidcService authenticatedUserOidcService;

    private String generateToken(Map<String, Object> claims) throws JOSEException
    {
        var keyPair = TestConfig.getKeyPair();
        var signer = new RSASSASigner(keyPair.getPrivate());

        var now = Instant.now();
        var claimsSetBuilder = new JWTClaimsSet.Builder()
                .subject("test-user-id")
                .issuer("https://idp.example.com")
                .audience("rize-os")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(3600)));

        claims.forEach(claimsSetBuilder::claim);

        var signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).build(), claimsSetBuilder.build());
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    @Test
    @DisplayName("[AuthenticatedUserOidcService] should load user from token to authenticated user")
    void shouldLoadUserFromTokenToAuthenticatedUser() throws JOSEException
    {
        var roles = List.of("user", "admin");
        var tokenClaims = Map.of(
                StandardClaimNames.PREFERRED_USERNAME, "testuser",
                StandardClaimNames.EMAIL, "testuser@rize.os",
                StandardClaimNames.GIVEN_NAME, "Test",
                StandardClaimNames.FAMILY_NAME, "User",
                "azp", CLIENT_ID,
                "resource_access", Map.of(CLIENT_ID, Map.of("roles", roles))
        );
        var tokenValue = generateToken(tokenClaims);

        var accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, tokenValue, Instant.now(), Instant.now().plusSeconds(3600));
        var idToken = new OidcIdToken(tokenValue, Instant.now(), Instant.now().plusSeconds(3600), Map.of(StandardClaimNames.SUB, "test-user-id"));
        var clientRegistration = ClientRegistration.withRegistrationId("rize-os")
                .clientId(CLIENT_ID)
                .clientSecret("secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://idp.example.com/auth")
                .tokenUri("https://idp.example.com/token")
                .build();
        var userRequest = new OidcUserRequest(clientRegistration, accessToken, idToken);

        var authenticatedUser = authenticatedUserOidcService.loadUser(userRequest);

        assertThat(authenticatedUser).isNotNull();
        assertThat(authenticatedUser.getUserId()).isEqualTo("test-user-id");
        assertThat(authenticatedUser.getUsername()).isEqualTo("testuser");
        assertThat(authenticatedUser.getEmail()).isEqualTo("testuser@rize.os");
        assertThat(authenticatedUser.getFirstName()).isEqualTo("Test");
        assertThat(authenticatedUser.getLastName()).isEqualTo("User");
        assertThat(authenticatedUser.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .hasSize(2)
                .containsExactlyInAnyOrder("ROLE_user", "ROLE_admin");
    }
}

