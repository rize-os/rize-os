package rize.os.access.manager.organization;

import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.OrganizationRepresentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationMapperTest
{
    private final OrganizationMapper organizationMapper = new OrganizationMapper();

    @Test
    void shouldMapOrganizationRepresentationToOrganization()
    {
        // Given
        var organizationRepresentation = new OrganizationRepresentation();
        organizationRepresentation.setId(UUID.randomUUID().toString());
        organizationRepresentation.setName("test-organization-1");
        organizationRepresentation.setAttributes(new HashMap<>());
        organizationRepresentation.getAttributes().put("displayName", List.of("Test Organization 1"));
        organizationRepresentation.getAttributes().put("aliases", List.of("test-1", "test-2"));
        organizationRepresentation.getAttributes().put("imageId", List.of(UUID.randomUUID().toString()));
        organizationRepresentation.setEnabled(true);

        // When
        Organization organization = organizationMapper.toOrganization(organizationRepresentation);

        // Then
        assertThat(organization.getId()).isEqualTo(organizationRepresentation.getId());
        assertThat(organization.getName()).isEqualTo(organizationRepresentation.getName());
        assertThat(organization.getDisplayName()).isEqualTo(organizationRepresentation.getAttributes().get("displayName").getFirst());
        assertThat(organization.getAliases()).hasSize(2);
        assertThat(organization.getAliases()).contains("test-1", "test-2");
        assertThat(organization.getImageId()).isEqualTo(UUID.fromString(organizationRepresentation.getAttributes().get("imageId").getFirst()));
        assertThat(organization.isEnabled()).isEqualTo(organizationRepresentation.isEnabled());
    }

    @Test
    void shouldGetDisplayName()
    {
        var organizationRepresentation = new OrganizationRepresentation();
        organizationRepresentation.singleAttribute(Organization.DISPLAY_NAME_ATTRIBUTE, "Test Organization 1");

        var displayName = organizationMapper.getDisplayName(organizationRepresentation);
        assertThat(displayName).isEqualTo("Test Organization 1");
    }

    @Test
    void shouldGetEmptyDisplayName()
    {
        var displayName = organizationMapper.getDisplayName(new OrganizationRepresentation());
        assertThat(displayName).isEmpty();
    }

    @Test
    void shouldGetAliases()
    {
        var organizationRepresentation = new OrganizationRepresentation();
        organizationRepresentation.setAttributes(Map.of(Organization.ALIASES_ATTRIBUTE, List.of("test-1", "test-2")));

        var aliases = organizationMapper.getAliases(organizationRepresentation);
        assertThat(aliases).hasSize(2);
        assertThat(aliases).contains("test-1", "test-2");
    }

    @Test
    void shouldGetEmptyAliases()
    {
        var aliases = organizationMapper.getAliases(new OrganizationRepresentation());
        assertThat(aliases).isEmpty();
    }

    @Test
    void shouldGetImageId()
    {
        var organizationRepresentation = new OrganizationRepresentation();
        organizationRepresentation.singleAttribute(Organization.IMAGE_ID_ATTRIBUTE, UUID.randomUUID().toString());

        var imageId = organizationMapper.getImageId(organizationRepresentation);
        assertThat(imageId).isPresent();
        assertThat(imageId.get()).isEqualTo(UUID.fromString(organizationRepresentation.getAttributes().get(Organization.IMAGE_ID_ATTRIBUTE).getFirst()));
    }

    @Test
    void shouldGetEmptyImageId()
    {
        var imageId = organizationMapper.getImageId(new OrganizationRepresentation());
        assertThat(imageId).isEmpty();
    }

    @Test
    void shouldGetAttributesForOrganization()
    {
        var organization = Organization.builder()
                .displayName("Test Organization 1")
                .alias("test-1")
                .alias("test-2")
                .imageId(UUID.randomUUID())
                .build();

        var attributes = organizationMapper.getAttributesForOrganization(organization);

        assertThat(attributes).containsEntry(Organization.DISPLAY_NAME_ATTRIBUTE, List.of("Test Organization 1"));
        assertThat(attributes).containsEntry(Organization.ALIASES_ATTRIBUTE, List.of("test-1", "test-2"));
        assertThat(attributes).containsEntry(Organization.IMAGE_ID_ATTRIBUTE, List.of(organization.getImageId().toString()));
    }
}