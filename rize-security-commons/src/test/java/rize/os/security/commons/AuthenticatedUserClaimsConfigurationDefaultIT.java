package rize.os.security.commons;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = AuthenticatedUserClaimsConfigurationDefaultIT.TestConfig.class)
class AuthenticatedUserClaimsConfigurationDefaultIT
{
    @Configuration
    @EnableConfigurationProperties(AuthenticatedUserClaimsConfiguration.class)
    static class TestConfig {}

    @Autowired
    private AuthenticatedUserClaimsConfiguration config;

    @Test
    @DisplayName("[JwtUserClaimsConfiguration] should have default values")
    void shouldHaveDefaultValues()
    {
        assertThat(config.getUserId()).isEqualTo(StandardClaimNames.SUB);
        assertThat(config.getUsername()).isEqualTo(StandardClaimNames.PREFERRED_USERNAME);
        assertThat(config.getEmail()).isEqualTo(StandardClaimNames.EMAIL);
        assertThat(config.getEmailVerified()).isEqualTo(StandardClaimNames.EMAIL_VERIFIED);
        assertThat(config.getFullName()).isEqualTo(StandardClaimNames.NAME);
        assertThat(config.getFirstName()).isEqualTo(StandardClaimNames.GIVEN_NAME);
        assertThat(config.getMiddleName()).isEqualTo(StandardClaimNames.MIDDLE_NAME);
        assertThat(config.getLastName()).isEqualTo(StandardClaimNames.FAMILY_NAME);
        assertThat(config.getProfile()).isEqualTo(StandardClaimNames.PROFILE);
        assertThat(config.getPicture()).isEqualTo(StandardClaimNames.PICTURE);
        assertThat(config.getWebsite()).isEqualTo(StandardClaimNames.WEBSITE);
        assertThat(config.getGender()).isEqualTo(StandardClaimNames.GENDER);
        assertThat(config.getBirthdate()).isEqualTo(StandardClaimNames.BIRTHDATE);
        assertThat(config.getZoneInfo()).isEqualTo(StandardClaimNames.ZONEINFO);
        assertThat(config.getLocale()).isEqualTo(StandardClaimNames.LOCALE);
        assertThat(config.getPhoneNumber()).isEqualTo(StandardClaimNames.PHONE_NUMBER);
        assertThat(config.getPhoneNumberVerified()).isEqualTo(StandardClaimNames.PHONE_NUMBER_VERIFIED);
        assertThat(config.getAddress()).isEqualTo(StandardClaimNames.ADDRESS);
        assertThat(config.getUpdatedAt()).isEqualTo(StandardClaimNames.UPDATED_AT);
        assertThat(config.getSessionId()).isEqualTo("sid");
        assertThat(config.getClientRoles()).isEqualTo("resource_access.${clientId}.roles");
        assertThat(config.getClientId()).isEqualTo("azp");
    }
}