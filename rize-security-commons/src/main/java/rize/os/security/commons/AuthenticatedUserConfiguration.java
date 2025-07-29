package rize.os.security.commons;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that is used to enable configuration properties for the
 * {@link AuthenticatedUserClaimsConfiguration}.
 *
 * @author Dennis Schaefer (<a href="https://github.com/dennis-schaefer">...</a>)
 */
@Configuration
@EnableConfigurationProperties(AuthenticatedUserClaimsConfiguration.class)
public class AuthenticatedUserConfiguration
{
}
