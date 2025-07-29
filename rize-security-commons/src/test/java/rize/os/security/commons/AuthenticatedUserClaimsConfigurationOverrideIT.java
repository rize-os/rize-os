package rize.os.security.commons;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = AuthenticatedUserClaimsConfigurationOverrideIT.TestConfig.class,
        properties =
        {
            "rize.security.jwt.authenticated-user-claims.user-id=custom_user_id",
            "rize.security.jwt.authenticated-user-claims.username=custom_username",
            "rize.security.jwt.authenticated-user-claims.email=custom_email",
            "rize.security.jwt.authenticated-user-claims.email-verified=custom_email_verified",
            "rize.security.jwt.authenticated-user-claims.full-name=custom_full_name",
            "rize.security.jwt.authenticated-user-claims.first-name=custom_first_name",
            "rize.security.jwt.authenticated-user-claims.middle-name=custom_middle_name",
            "rize.security.jwt.authenticated-user-claims.last-name=custom_last_name",
            "rize.security.jwt.authenticated-user-claims.profile=custom_profile",
            "rize.security.jwt.authenticated-user-claims.picture=custom_picture",
            "rize.security.jwt.authenticated-user-claims.website=custom_website",
            "rize.security.jwt.authenticated-user-claims.gender=custom_gender",
            "rize.security.jwt.authenticated-user-claims.birthdate=custom_birthdate",
            "rize.security.jwt.authenticated-user-claims.zone-info=custom_zone_info",
            "rize.security.jwt.authenticated-user-claims.locale=custom_locale",
            "rize.security.jwt.authenticated-user-claims.phone-number=custom_phone_number",
            "rize.security.jwt.authenticated-user-claims.phone-number-verified=custom_phone_number_verified",
            "rize.security.jwt.authenticated-user-claims.address=custom_address",
            "rize.security.jwt.authenticated-user-claims.updated-at=custom_updated_at",
            "rize.security.jwt.authenticated-user-claims.session-id=custom_sid",
            "rize.security.jwt.authenticated-user-claims.client-roles=custom_client_roles",
            "rize.security.jwt.authenticated-user-claims.client-id=custom_client_id"
        }
)
public class AuthenticatedUserClaimsConfigurationOverrideIT
{
    @Configuration
    @EnableConfigurationProperties(AuthenticatedUserClaimsConfiguration.class)
    static class TestConfig {}

    @Autowired
    private AuthenticatedUserClaimsConfiguration config;

    @Test
    @DisplayName("[JwtUserClaimsConfiguration] should have overridden values")
    void shouldHaveOverriddenValues()
    {
        assertThat(config.getUserId()).isEqualTo("custom_user_id");
        assertThat(config.getUsername()).isEqualTo("custom_username");
        assertThat(config.getEmail()).isEqualTo("custom_email");
        assertThat(config.getEmailVerified()).isEqualTo("custom_email_verified");
        assertThat(config.getFullName()).isEqualTo("custom_full_name");
        assertThat(config.getFirstName()).isEqualTo("custom_first_name");
        assertThat(config.getMiddleName()).isEqualTo("custom_middle_name");
        assertThat(config.getLastName()).isEqualTo("custom_last_name");
        assertThat(config.getProfile()).isEqualTo("custom_profile");
        assertThat(config.getPicture()).isEqualTo("custom_picture");
        assertThat(config.getWebsite()).isEqualTo("custom_website");
        assertThat(config.getGender()).isEqualTo("custom_gender");
        assertThat(config.getBirthdate()).isEqualTo("custom_birthdate");
        assertThat(config.getZoneInfo()).isEqualTo("custom_zone_info");
        assertThat(config.getLocale()).isEqualTo("custom_locale");
        assertThat(config.getPhoneNumber()).isEqualTo("custom_phone_number");
        assertThat(config.getPhoneNumberVerified()).isEqualTo("custom_phone_number_verified");
        assertThat(config.getAddress()).isEqualTo("custom_address");
        assertThat(config.getUpdatedAt()).isEqualTo("custom_updated_at");
        assertThat(config.getSessionId()).isEqualTo("custom_sid");
        assertThat(config.getClientRoles()).isEqualTo("custom_client_roles");
        assertThat(config.getClientId()).isEqualTo("custom_client_id");
    }
}
