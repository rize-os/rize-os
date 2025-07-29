package rize.os.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import java.util.Objects;

/**
 * This class is used to configure the mapping between the claims of a JWT and the properties of an authenticated user.
 * <p>
 * The properties of this class can be set in the application.yml or application.properties file.
 * The prefix for the properties is {@code rize.security.jwt.authenticated-user-claims}.
 * <p>
 * If a property is not set, a default value is used. The default values are the standard claim names from OpenID Connect.
 *
 * @see StandardClaimNames
 * @author Dennis Schäfer (<a href="https://github.com/dennis-schaefer">...</a>)
 */
@ConfigurationProperties(prefix = "rize.security.jwt.authenticated-user-claims")
public class AuthenticatedUserClaimsConfiguration
{
    private final String userId;
    private final String username;
    private final String email;
    private final String emailVerified;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String fullName;
    private final String profile;
    private final String picture;
    private final String website;
    private final String gender;
    private final String birthdate;
    private final String zoneInfo;
    private final String locale;
    private final String phoneNumber;
    private final String phoneNumberVerified;
    private final String address;
    private final String updatedAt;
    private final String sessionId;
    private final String clientRoles;
    private final String clientId;

    /**
     * Creates a new instance of the {@link AuthenticatedUserClaimsConfiguration}.
     *
     * @param userId              The name of the claim that contains the user ID. Defaults to {@link StandardClaimNames#SUB}.
     * @param username            The name of the claim that contains the username. Defaults to {@link StandardClaimNames#PREFERRED_USERNAME}.
     * @param email               The name of the claim that contains the email. Defaults to {@link StandardClaimNames#EMAIL}.
     * @param emailVerified       The name of the claim that contains the email verified status. Defaults to {@link StandardClaimNames#EMAIL_VERIFIED}.
     * @param firstName           The name of the claim that contains the first name. Defaults to {@link StandardClaimNames#GIVEN_NAME}.
     * @param middleName          The name of the claim that contains the middle name. Defaults to {@link StandardClaimNames#MIDDLE_NAME}.
     * @param lastName            The name of the claim that contains the last name. Defaults to {@link StandardClaimNames#FAMILY_NAME}.
     * @param fullName            The name of the claim that contains the full name. Defaults to {@link StandardClaimNames#NAME}.
     * @param profile             The name of the claim that contains the profile URL. Defaults to {@link StandardClaimNames#PROFILE}.
     * @param picture             The name of the claim that contains the picture URL. Defaults to {@link StandardClaimNames#PICTURE}.
     * @param website             The name of the claim that contains the website URL. Defaults to {@link StandardClaimNames#WEBSITE}.
     * @param gender              The name of the claim that contains the gender. Defaults to {@link StandardClaimNames#GENDER}.
     * @param birthdate           The name of the claim that contains the birthdate. Defaults to {@link StandardClaimNames#BIRTHDATE}.
     * @param zoneInfo            The name of the claim that contains the zone info. Defaults to {@link StandardClaimNames#ZONEINFO}.
     * @param locale              The name of the claim that contains the locale. Defaults to {@link StandardClaimNames#LOCALE}.
     * @param phoneNumber         The name of the claim that contains the phone number. Defaults to {@link StandardClaimNames#PHONE_NUMBER}.
     * @param phoneNumberVerified The name of the claim that contains the phone number verified status. Defaults to {@link StandardClaimNames#PHONE_NUMBER_VERIFIED}.
     * @param address             The name of the claim that contains the address. Defaults to {@link StandardClaimNames#ADDRESS}.
     * @param updatedAt           The name of the claim that contains the updated at timestamp. Defaults to {@link StandardClaimNames#UPDATED_AT}.
     * @param sessionId           The name of the claim that contains the session ID. Defaults to {@code sid}.
     * @param clientId            The name of the claim that contains the client ID. Defaults to {@code azp}.
     * @param clientRoles         The name of the claim that contains the client roles. Defaults to {@code resource_access.${clientId}.roles}.
     */
    public AuthenticatedUserClaimsConfiguration(@Nullable String userId,
                                                @Nullable String username,
                                                @Nullable String email,
                                                @Nullable String emailVerified,
                                                @Nullable String firstName,
                                                @Nullable String middleName,
                                                @Nullable String lastName,
                                                @Nullable String fullName,
                                                @Nullable String profile,
                                                @Nullable String picture,
                                                @Nullable String website,
                                                @Nullable String gender,
                                                @Nullable String birthdate,
                                                @Nullable String zoneInfo,
                                                @Nullable String locale,
                                                @Nullable String phoneNumber,
                                                @Nullable String phoneNumberVerified,
                                                @Nullable String address,
                                                @Nullable String updatedAt,
                                                @Nullable String sessionId,
                                                @Nullable String clientId,
                                                @Nullable String clientRoles)
    {
        this.userId = Objects.requireNonNullElse(userId, StandardClaimNames.SUB);
        this.username = Objects.requireNonNullElse(username, StandardClaimNames.PREFERRED_USERNAME);
        this.email = Objects.requireNonNullElse(email, StandardClaimNames.EMAIL);
        this.emailVerified = Objects.requireNonNullElse(emailVerified, StandardClaimNames.EMAIL_VERIFIED);
        this.fullName = Objects.requireNonNullElse(fullName, StandardClaimNames.NAME);
        this.firstName = Objects.requireNonNullElse(firstName, StandardClaimNames.GIVEN_NAME);
        this.middleName = Objects.requireNonNullElse(middleName, StandardClaimNames.MIDDLE_NAME);
        this.lastName = Objects.requireNonNullElse(lastName, StandardClaimNames.FAMILY_NAME);
        this.profile = Objects.requireNonNullElse(profile, StandardClaimNames.PROFILE);
        this.picture = Objects.requireNonNullElse(picture, StandardClaimNames.PICTURE);
        this.website = Objects.requireNonNullElse(website, StandardClaimNames.WEBSITE);
        this.gender = Objects.requireNonNullElse(gender, StandardClaimNames.GENDER);
        this.birthdate = Objects.requireNonNullElse(birthdate, StandardClaimNames.BIRTHDATE);
        this.zoneInfo = Objects.requireNonNullElse(zoneInfo, StandardClaimNames.ZONEINFO);
        this.locale = Objects.requireNonNullElse(locale, StandardClaimNames.LOCALE);
        this.phoneNumber = Objects.requireNonNullElse(phoneNumber, StandardClaimNames.PHONE_NUMBER);
        this.phoneNumberVerified = Objects.requireNonNullElse(phoneNumberVerified, StandardClaimNames.PHONE_NUMBER_VERIFIED);
        this.address = Objects.requireNonNullElse(address, StandardClaimNames.ADDRESS);
        this.updatedAt = Objects.requireNonNullElse(updatedAt, StandardClaimNames.UPDATED_AT);
        this.sessionId = Objects.requireNonNullElse(sessionId, "sid");
        this.clientId = Objects.requireNonNullElse(clientId, "azp");
        this.clientRoles = Objects.requireNonNullElse(clientRoles, "resource_access.${clientId}.roles");
    }

    /**
     * @return The name of the claim that contains the user ID.
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * @return The name of the claim that contains the username.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @return The name of the claim that contains the email.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @return The name of the claim that contains the email verified status.
     */
    public String getEmailVerified()
    {
        return emailVerified;
    }

    /**
     * @return The name of the claim that contains the first name.
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * @return The name of the claim that contains the middle name.
     */
    public String getMiddleName()
    {
        return middleName;
    }

    /**
     * @return The name of the claim that contains the last name.
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * @return The name of the claim that contains the full name.
     */
    public String getFullName()
    {
        return fullName;
    }

    /**
     * @return The name of the claim that contains the profile URL.
     */
    public String getProfile()
    {
        return profile;
    }

    /**
     * @return The name of the claim that contains the picture URL.
     */
    public String getPicture()
    {
        return picture;
    }

    /**
     * @return The name of the claim that contains the website URL.
     */
    public String getWebsite()
    {
        return website;
    }

    /**
     * @return The name of the claim that contains the gender.
     */
    public String getGender()
    {
        return gender;
    }

    /**
     * @return The name of the claim that contains the birthdate.
     */
    public String getBirthdate()
    {
        return birthdate;
    }

    /**
     * @return The name of the claim that contains the zone info.
     */
    public String getZoneInfo()
    {
        return zoneInfo;
    }

    /**
     * @return The name of the claim that contains the locale.
     */
    public String getLocale()
    {
        return locale;
    }

    /**
     * @return The name of the claim that contains the phone number.
     */
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    /**
     * @return The name of the claim that contains the phone number verified status.
     */
    public String getPhoneNumberVerified()
    {
        return phoneNumberVerified;
    }

    /**
     * @return The name of the claim that contains the address.
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * @return The name of the claim that contains the updated at timestamp.
     */
    public String getUpdatedAt()
    {
        return updatedAt;
    }

    /**
     * @return The name of the claim that contains the session ID.
     */
    public String getSessionId()
    {
        return sessionId;
    }

    /**
     * @return The name of the claim that contains the client ID.
     */
    public String getClientId()
    {
        return clientId;
    }

    /**
     * @return The name of the claim that contains the client roles.
     */
    public String getClientRoles()
    {
        return clientRoles;
    }

    static AuthenticatedUserClaimsConfiguration createDefault()
    {
        return new AuthenticatedUserClaimsConfiguration(
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null);
    }
}
