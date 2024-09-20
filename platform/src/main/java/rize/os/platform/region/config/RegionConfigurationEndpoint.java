package rize.os.platform.region.config;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.Endpoint;
import com.vaadin.hilla.exception.EndpointException;
import lombok.RequiredArgsConstructor;

@Endpoint
@AnonymousAllowed
@RequiredArgsConstructor
public class RegionConfigurationEndpoint
{
    private final RegionConfigurationService regionConfigurationService;

    public boolean isRegionFeatureEnabled()
    {
        try {
            return regionConfigurationService.isRegionFeatureEnabled();
        }
        catch (Exception e) {
            throw new EndpointException(e.getMessage(), e, e);
        }
    }

    public void setRegionFeatureEnabled(boolean enabled)
    {
        try {
            regionConfigurationService.setRegionFeatureEnabled(enabled);
        }
        catch (Exception e) {
            throw new EndpointException(e.getMessage(), e, e);
        }
    }
}
