package rize.os.security;

import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

/**
 * This service is responsible for loading the {@link AuthenticatedUser} after a successful OIDC authentication.
 * <p>
 * It uses a {@link JwtDecoder} to decode the access token from the {@link OidcUserRequest} and then uses the
 * {@link JwtAuthenticatedUserConverter} to convert the resulting JWT into an {@link AuthenticatedUser} object.
 * This class extends {@link OidcUserService} to integrate with Spring Security's OIDC login flow.
 *
 * @author Dennis Schaefer (<a href="https://github.com/dennis-schaefer">...</a>)
 */
@Service
public class AuthenticatedUserOidcService extends OidcUserService
{
    private final JwtAuthenticatedUserConverter jwtAuthenticatedUserConverter;
    private final JwtDecoder jwtDecoder;

    /**
     * Constructs a new {@code AuthenticatedUserOidcService} with the given dependencies.
     *
     * @param jwtAuthenticatedUserConverter the converter to use for converting the JWT to an {@link AuthenticatedUser}
     * @param jwtDecoder                    the decoder to use for decoding the access token
     */
    public AuthenticatedUserOidcService(JwtAuthenticatedUserConverter jwtAuthenticatedUserConverter,
                                        JwtDecoder jwtDecoder)
    {
        this.jwtAuthenticatedUserConverter = jwtAuthenticatedUserConverter;
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Loads the user from the given OIDC user request.
     * <p>
     * This method is called by Spring Security after a successful OIDC authentication. It decodes the access token
     * from the user request, converts the resulting JWT to an {@link AuthenticatedUser}, and returns it.
     *
     * @param userRequest the OIDC user request containing the access token
     * @return the authenticated user
     * @throws OAuth2AuthenticationException if an error occurs while processing the OIDC user request
     */
    @NonNull
    @Override
    public AuthenticatedUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException
    {
        var jwt = jwtDecoder.decode(userRequest.getAccessToken().getTokenValue());
        return jwtAuthenticatedUserConverter.convert(jwt);
    }
}
