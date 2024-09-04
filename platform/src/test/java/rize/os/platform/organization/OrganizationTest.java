package rize.os.platform.organization;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationTest
{
    @Test
    void shouldBeValidOrganization()
    {
        Organization organization = Organization.builder()
                .name("test-organization-1")
                .displayName("Test")
                .region("europe-west-1")
                .build();

        assertThat(organization.validate()).isEmpty();
    }

    @Test
    void shouldHaveInvalidOrganizationName()
    {
        Organization organization = Organization.builder()
                .name("test-organization-")
                .displayName("Test")
                .region("europe-west-1")
                .build();
        assertThat(organization.validate()).hasSize(1);

        organization.setName("-test-organization");
        assertThat(organization.validate()).hasSize(1);

        organization.setName("Test Organization");
        assertThat(organization.validate()).hasSize(1);

        organization.setName("test_organization");
        assertThat(organization.validate()).hasSize(1);

        organization.setName("test organization");
        assertThat(organization.validate()).hasSize(1);
    }

    @Test
    void shouldHaveInvalidOrganizationDisplayName()
    {
        Organization organization = Organization.builder()
                .name("test-organization-1")
                .region("europe-west-1")
                .build();
        assertThat(organization.validate()).hasSize(1);

        organization.setDisplayName("");
        assertThat(organization.validate()).hasSize(1);
    }

    @Test
    void shouldHaveInvalidRegion()
    {
        Organization organization = Organization.builder()
                .name("test-organization-1")
                .displayName("Test")
                .build();
        assertThat(organization.validate()).hasSize(1);

        organization.setRegion("");
        assertThat(organization.validate()).hasSize(1);
    }

    @Test
    void shouldBeEqual()
    {
        Organization organization1 = Organization.builder()
                .name("test-organization-1")
                .displayName("Test")
                .region("europe-west-1")
                .build();
        Organization organization2 = Organization.builder()
                .name("test-organization-1")
                .displayName("Test")
                .region("europe-west-1")
                .build();
        assertThat(organization1).isEqualTo(organization2);
        assertThat(organization1.hashCode()).isEqualTo(organization2.hashCode());
        assertThat(organization1.toString()).isEqualTo(organization2.toString());
    }

    @Test
    void testDescription()
    {
        Organization organization = Organization.builder()
                .name("test-organization-1")
                .displayName("Test")
                .region("europe-west-1")
                .build();

        assertThat(organization.getDescription()).contains(organization.getName(), organization.getDisplayName(), organization.getRegion());
    }
}