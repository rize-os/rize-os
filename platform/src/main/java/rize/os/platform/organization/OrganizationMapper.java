package rize.os.platform.organization;

import org.keycloak.representations.idm.OrganizationRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrganizationMapper
{
    /**
     * Returns a map of attributes for an organization representation.
     * @param organization Organization object
     * @return Map of attributes for the organization representation
     */
    Map<String, List<String>> toRepresentationAttributes(Organization organization)
    {
        return Map.of(
                Organization.DISPLAY_NAME_ATTRIBUTE, List.of(organization.getDisplayName()),
                Organization.REGION_ATTRIBUTE, List.of(organization.getRegion())
        );
    }

    /**
     * Maps an organization representation from Keycloak to an organization object
     * @param representation Representation of an organization from Keycloak
     * @return Organization object
     */
    Organization toOrganization(OrganizationRepresentation representation)
    {
        var displayName = getDisplayName(representation).orElse("");
        var region = getRegion(representation).orElse("");

        return Organization.builder()
                .id(representation.getId())
                .name(representation.getName())
                .displayName(displayName)
                .region(region)
                .enabled(representation.isEnabled())
                .build();
    }

    /**
     * Returns the display name of the organization representation from Keycloak
     * @param representation Representation of an organization from Keycloak
     * @return Display name of the organization; empty no display name is present as attribute in the representation
     */
    private Optional<String> getDisplayName(OrganizationRepresentation representation)
    {
        if (representation.getAttributes() == null || !representation.getAttributes().containsKey(Organization.DISPLAY_NAME_ATTRIBUTE))
            return Optional.empty();

        return Optional.of(representation.getAttributes().get(Organization.DISPLAY_NAME_ATTRIBUTE).getFirst());
    }

    /**
     * Returns the region of the organization representation from Keycloak
     * @param representation Representation of an organization from Keycloak
     * @return Region of the organization; empty string if not present
     */
    private Optional<String> getRegion(OrganizationRepresentation representation)
    {
        if (representation.getAttributes() == null || !representation.getAttributes().containsKey(Organization.REGION_ATTRIBUTE))
            return Optional.empty();

        return Optional.of(representation.getAttributes().get(Organization.REGION_ATTRIBUTE).getFirst());
    }
}
