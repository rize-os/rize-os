package rize.os.platform.organization;


import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import rize.os.platform.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class PulsarTenantHandlerIT
{
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private PulsarAdmin pulsarAdmin;

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @Test
    void shouldCreateAndDeleteTenantsForOrganization() throws InterruptedException, PulsarAdminException
    {
        var organizationToCreate = Organization.builder()
                .name("pulsar-tenant-handler")
                .displayName("Pulsar Tenant Handler")
                .region("de")
                .enabled(true)
                .build();
        var createdOrganization = organizationService.createOrganization(organizationToCreate);
        Thread.sleep(200);

        var tenants = pulsarAdmin.tenants().getTenants();
        assertThat(tenants).contains(organizationToCreate.getName());

        organizationService.deleteOrganization(createdOrganization.getId());
        Thread.sleep(200);

        tenants = pulsarAdmin.tenants().getTenants();
        assertThat(tenants).doesNotContain(organizationToCreate.getName());
    }
}