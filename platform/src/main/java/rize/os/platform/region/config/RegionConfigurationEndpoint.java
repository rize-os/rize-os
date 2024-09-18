package rize.os.platform.region.config;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.Endpoint;
import lombok.RequiredArgsConstructor;

@Endpoint
@AnonymousAllowed
@RequiredArgsConstructor
public class RegionConfigurationEndpoint
{
    private final RegionConfigurationService regionConfigurationService;

    public boolean isRegionFeatureEnabled()
    {
        return regionConfigurationService.isRegionFeatureEnabled();
    }

    public void setRegionFeatureEnabled(boolean enabled)
    {
        regionConfigurationService.setRegionFeatureEnabled(enabled);
    }
}
