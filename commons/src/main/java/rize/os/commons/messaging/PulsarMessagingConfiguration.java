package rize.os.commons.messaging;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.common.policies.data.Policies;
import org.apache.pulsar.common.policies.data.RetentionPolicies;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.apache.pulsar.common.policies.data.TenantInfoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.core.PulsarAdministration;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Configuration
public class PulsarMessagingConfiguration
{
    public static final String RIZE_TENANT = "rize";
    public static final String RIZE_PLATFORM_NAMESPACE = "rize/platform";

    @Value("${spring.pulsar.allowed-clusters:standalone}")
    private String allowedClusters;

    @Bean
    PulsarAdmin pulsarAdmin(PulsarAdministration pulsarAdministration) throws PulsarClientException
    {
        return pulsarAdministration.createAdminClient();
    }

    @Bean
    TenantInfo rizeTenant(PulsarAdmin pulsarAdmin) throws PulsarAdminException
    {
        log.debug("Setting up tenant \"{}\" for Pulsar", RIZE_TENANT);
        var rizeTenant = getRizeTenant(pulsarAdmin).orElse(null);
        if (rizeTenant == null)
            rizeTenant = createRizeTenant(pulsarAdmin);

        createNamespace(RIZE_PLATFORM_NAMESPACE, platformNamespacePolicies(), pulsarAdmin);

        return rizeTenant;
    }

    private Optional<TenantInfo> getRizeTenant(PulsarAdmin pulsarAdmin) throws PulsarAdminException
    {
        if (!pulsarAdmin.tenants().getTenants().contains(RIZE_TENANT))
            return Optional.empty();

        log.debug("Tenant \"{}\" already exists", RIZE_TENANT);
        return Optional.of(pulsarAdmin.tenants().getTenantInfo(RIZE_TENANT));
    }

    private TenantInfo createRizeTenant(PulsarAdmin pulsarAdmin) throws PulsarAdminException
    {
        log.debug("Creating tenant \"{}\" in Pulsar", RIZE_TENANT);
        var tenantInfo = new TenantInfoImpl();
        tenantInfo.setAllowedClusters(allowedClusters());
        pulsarAdmin.tenants().createTenant(RIZE_TENANT, tenantInfo);
        return tenantInfo;
    }

    private Set<String> allowedClusters()
    {
        if (allowedClusters.contains(","))
            return Set.of(allowedClusters.split(","));
        return Set.of(allowedClusters);
    }

    private void createNamespace(String namespace, Policies policies, PulsarAdmin pulsarAdmin) throws PulsarAdminException
    {
        log.debug("Setting up namespace \"{}\" in Pulsar", namespace);
        if (pulsarAdmin.namespaces().getNamespaces(RIZE_TENANT).contains(namespace))
        {
            log.debug("Namespace \"{}\" already exists", namespace);
            return;
        }

        pulsarAdmin.namespaces().createNamespace(namespace, policies);
    }

    private Policies platformNamespacePolicies()
    {
        var policies = new Policies();
        policies.compaction_threshold = 500000L;
        policies.retention_policies = new RetentionPolicies(-1, -1);
        return policies;
    }
}
