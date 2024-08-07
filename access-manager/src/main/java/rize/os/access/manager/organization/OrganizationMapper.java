package rize.os.access.manager.organization;

import jakarta.annotation.Nonnull;
import org.keycloak.representations.idm.OrganizationRepresentation;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
class OrganizationMapper
{
    /**
     * Maps an organization representation from Keycloak to an organization object
     * @param organizationRepresentation Organization representation from Keycloak
     * @return Organization object
     */
    @Nonnull
    Organization toOrganization(@Nonnull OrganizationRepresentation organizationRepresentation)
    {
        var displayName = getDisplayName(organizationRepresentation);
        var aliases = getAliases(organizationRepresentation);
        var imageId = getImageId(organizationRepresentation).orElse(null);

        return Organization.builder()
                .id(organizationRepresentation.getId())
                .name(organizationRepresentation.getName())
                .displayName(displayName)
                .aliases(aliases)
                .imageId(imageId)
                .enabled(organizationRepresentation.isEnabled())
                .build();
    }

    /**
     * Returns the display name of the organization representation from Keycloak
     * @param organizationRepresentation Organization representation
     * @return Display name of the organization; empty string if not present
     */
    @Nonnull
    String getDisplayName(@Nonnull OrganizationRepresentation organizationRepresentation)
    {
        if (organizationRepresentation.getAttributes() == null || !organizationRepresentation.getAttributes().containsKey(Organization.DISPLAY_NAME_ATTRIBUTE))
            return "";

        return organizationRepresentation.getAttributes().get(Organization.DISPLAY_NAME_ATTRIBUTE).getFirst();
    }

    /**
     * Returns the aliases of the organization representation from Keycloak
     * @param organizationRepresentation Organization representation
     * @return List of aliases of the organization; empty list if not present
     */
    @Nonnull
    List<String> getAliases(@Nonnull OrganizationRepresentation organizationRepresentation)
    {
        if (organizationRepresentation.getAttributes() == null || !organizationRepresentation.getAttributes().containsKey(Organization.ALIASES_ATTRIBUTE))
            return List.of();

        return organizationRepresentation.getAttributes().get(Organization.ALIASES_ATTRIBUTE);
    }

    /**
     * Returns the image ID of the organization representation from Keycloak
     * @param organizationRepresentation Organization representation
     * @return Image ID of the organization; empty optional if not present
     */
    @Nonnull
    Optional<UUID> getImageId(@Nonnull OrganizationRepresentation organizationRepresentation)
    {
        if (organizationRepresentation.getAttributes() == null || !organizationRepresentation.getAttributes().containsKey(Organization.IMAGE_ID_ATTRIBUTE))
            return Optional.empty();

        return Optional.of(UUID.fromString(organizationRepresentation.getAttributes().get(Organization.IMAGE_ID_ATTRIBUTE).getFirst()));
    }

    @Nonnull
    Map<String, List<String>> getAttributesForOrganization(@Nonnull Organization organization)
    {
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(Organization.DISPLAY_NAME_ATTRIBUTE, List.of(organization.getDisplayName()));
        attributes.put(Organization.ALIASES_ATTRIBUTE, organization.getAliases());
        if (organization.getImageId() != null)
            attributes.put(Organization.IMAGE_ID_ATTRIBUTE, List.of(organization.getImageId().toString()));
        return attributes;
    }
}
