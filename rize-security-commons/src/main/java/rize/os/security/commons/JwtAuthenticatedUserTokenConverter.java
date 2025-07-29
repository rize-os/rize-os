package rize.os.security.commons;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * A Spring {@link Converter} that converts a {@link Jwt} into an {@link AuthenticatedUserToken}.
 * <p>
 * This converter is used in the Spring Security configuration to transform the JWT provided by the identity provider
 * into an internal representation of the authenticated user. It relies on the {@link JwtAuthenticatedUserConverter}
 * to first extract the user details from the JWT.
 *
 * @author Dennis Schaefer (<a href="https://github.com/dennis-schaefer">...</a>)
 */
@Component
public class JwtAuthenticatedUserTokenConverter implements Converter<Jwt, AuthenticatedUserToken>
{
    private final JwtAuthenticatedUserConverter jwtAuthenticatedUserConverter;

    /**
     * Constructs a new {@code JwtAuthenticatedUserTokenConverter} with the specified converter.
     *
     * @param jwtAuthenticatedUserConverter The converter used to transform a {@link Jwt} into an {@link AuthenticatedUser}.
     *                                      This converter is responsible for mapping the claims from the JWT to the user's details.
     */
    public JwtAuthenticatedUserTokenConverter(JwtAuthenticatedUserConverter jwtAuthenticatedUserConverter)
    {
        this.jwtAuthenticatedUserConverter = jwtAuthenticatedUserConverter;
    }

    /**
     * Converts the given {@link Jwt} into an {@link AuthenticatedUserToken}.
     * <p>
     * This method first uses the {@link JwtAuthenticatedUserConverter} to convert the JWT into an {@link AuthenticatedUser},
     * which is then wrapped in an {@link AuthenticatedUserToken}.
     * </p>
     *
     * @param jwt The source {@link Jwt} object, must not be {@code null}.
     * @return The converted {@link AuthenticatedUserToken}, containing the authenticated user's details.
     */
    @NonNull
    @Override
    public AuthenticatedUserToken convert(@NonNull Jwt jwt)
    {
        return new AuthenticatedUserToken(jwtAuthenticatedUserConverter.convert(jwt));
    }
}
