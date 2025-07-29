package rize.os.security.commons;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts a {@link Jwt} to an {@link AuthenticatedUser}.
 * <p>
 * This converter is responsible for mapping the claims from a given JSON Web Token (JWT)
 * into a custom {@link AuthenticatedUser} object. This object serves as the principal
 * within the application's security context.
 * <p>
 * It uses an {@link AuthenticatedUserClaimsConfiguration} to allow flexible mapping
 * of claim names from the JWT to the standard OIDC claims and custom attributes
 * of the {@link AuthenticatedUser}. This includes user information, roles, and other
 * security-relevant data.
 *
 * @see Converter
 * @see org.springframework.security.oauth2.jwt.Jwt
 * @see AuthenticatedUser
 * @see AuthenticatedUserClaimsConfiguration
 *
 * @author Dennis Schaefer (<a href="https://github.com/dennis-schaefer">...</a>)
 */
@Component
public class JwtAuthenticatedUserConverter implements Converter<Jwt, AuthenticatedUser>
{
    private final Logger log = LoggerFactory.getLogger(JwtAuthenticatedUserConverter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthenticatedUserClaimsConfiguration claimsConfiguration;

    /**
     * Constructs a new JwtAuthenticatedUserConverter with the specified claims configuration.
     *
     * @param claimsConfiguration The configuration that defines how to map claims from the JWT.
     */
    public JwtAuthenticatedUserConverter(AuthenticatedUserClaimsConfiguration claimsConfiguration)
    {
        this.claimsConfiguration = claimsConfiguration;
    }

    /**
     * Converts the source {@link Jwt} object into an {@link AuthenticatedUser}.
     * <p>
     * This method orchestrates the extraction of standard OIDC claims, custom claims,
     * and authorities (roles) from the JWT to fully populate the {@link AuthenticatedUser} object.
     *
     * @param jwt The source {@link Jwt} token. Cannot be {@code null}.
     * @return An {@link AuthenticatedUser} instance populated with data from the JWT. Never {@code null}.
     */
    @NonNull
    @Override
    public AuthenticatedUser convert(@NonNull Jwt jwt)
    {
        log.debug("Converting JWT to AuthenticatedUser");

        Map<String, Object> userInfoClaims = new HashMap<>();
        addClaim(userInfoClaims, StandardClaimNames.SUB, jwt, claimsConfiguration.getUserId(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.PREFERRED_USERNAME, jwt, claimsConfiguration.getUsername(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.EMAIL, jwt, claimsConfiguration.getEmail(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.EMAIL_VERIFIED, jwt, claimsConfiguration.getEmailVerified(), Boolean.class);
        addClaim(userInfoClaims, StandardClaimNames.GIVEN_NAME, jwt, claimsConfiguration.getFirstName(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.MIDDLE_NAME, jwt, claimsConfiguration.getMiddleName(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.FAMILY_NAME, jwt, claimsConfiguration.getLastName(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.NAME, jwt, claimsConfiguration.getFullName(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.PROFILE, jwt, claimsConfiguration.getProfile(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.PICTURE, jwt, claimsConfiguration.getPicture(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.WEBSITE, jwt, claimsConfiguration.getWebsite(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.GENDER, jwt, claimsConfiguration.getGender(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.BIRTHDATE, jwt, claimsConfiguration.getBirthdate(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.ZONEINFO, jwt, claimsConfiguration.getZoneInfo(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.LOCALE, jwt, claimsConfiguration.getLocale(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.PHONE_NUMBER, jwt, claimsConfiguration.getPhoneNumber(), String.class);
        addClaim(userInfoClaims, StandardClaimNames.PHONE_NUMBER_VERIFIED, jwt, claimsConfiguration.getPhoneNumberVerified(), Boolean.class);
        addClaim(userInfoClaims, StandardClaimNames.ADDRESS, jwt, claimsConfiguration.getAddress(), new TypeReference<Map<String, Object>>() {});
        addClaim(userInfoClaims, StandardClaimNames.UPDATED_AT, jwt, claimsConfiguration.getUpdatedAt(), String.class);

        OidcUserInfo userInfo = new OidcUserInfo(userInfoClaims);
        OidcIdToken idToken = new OidcIdToken(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getClaims());
        Set<GrantedAuthority> authorities = extractAuthorities(jwt);
        String sessionId = extractClaim(jwt, claimsConfiguration.getSessionId(), String.class).orElse(null);

        return new AuthenticatedUser(userInfo, idToken, authorities, sessionId);
    }

    private <T> void addClaim(Map<String, Object> claims, String key, Jwt jwt, String claimName, Class<T> type)
    {
        extractClaim(jwt, claimName, type).ifPresent(value -> claims.put(key, value));
    }

    private <T> void addClaim(Map<String, Object> claims, String key, Jwt jwt, String claimName, TypeReference<T> typeRef)
    {
        extractClaim(jwt, claimName, typeRef).ifPresent(value -> claims.put(key, value));
    }

    private Set<GrantedAuthority> extractAuthorities(Jwt jwt)
    {
        String clientId = extractClaim(jwt, claimsConfiguration.getClientId(), String.class).orElse("");
        String clientRolesClaimName = claimsConfiguration.getClientRoles().replace("${clientId}", clientId);

        Stream<String> clientRoles = extractClaim(jwt, clientRolesClaimName, new TypeReference<Collection<String>>() {})
                .stream()
                .flatMap(Collection::stream)
                .map(role -> "ROLE_" + role);

        // TODO: Hier auf die Organisations-Rollen des filtern

        return clientRoles.map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    private <T> Optional<T> extractClaim(Jwt jwt, String claimName, TypeReference<T> typeRef)
    {
        return extractClaim(jwt, claimName).flatMap(claimValue ->
        {
            try
            {
                T convertedValue = objectMapper.convertValue(claimValue, typeRef);
                return Optional.of(convertedValue);
            }
            catch (IllegalArgumentException e)
            {
                log.warn("Failed to convert claim '{}' to type '{}': {}", claimName, typeRef.getType(), e.getMessage());
                return Optional.empty();
            }
        });
    }

    private <T> Optional<T> extractClaim(Jwt jwt, String claimName, Class<T> type)
    {
        return extractClaim(jwt, claimName)
                .filter(type::isInstance)
                .map(type::cast);
    }

    private Optional<Object> extractClaim(Jwt jwt, String claimName)
    {
        log.trace("Extracting claim '{}' from JWT", claimName);

        Map<String, Object> claims = jwt.getClaims();
        var keys = claimName.split("\\.");
        Object currentValue = claims;

        for (var key : keys)
        {
            if (!(currentValue instanceof Map))
                return Optional.empty();

            currentValue = ((Map<?, ?>) currentValue).get(key);
            if (currentValue == null)
                return Optional.empty();
        }

        return Optional.ofNullable(currentValue);
    }
}
