package rize.os.commons.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.common.policies.data.Policies;
import org.apache.pulsar.common.policies.data.RetentionPolicies;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.apache.pulsar.common.policies.data.TenantInfoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.core.PulsarTopic;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PulsarMessagingConfiguration
{
    public static final String RIZE_TENANT = "rize";
    public static final String RIZE_PLATFORM_NAMESPACE = "rize/platform";

    public static final String ORGANIZATION_STATE_TOPIC = "persistent://rize/platform/organization.state";

    private final PulsarAdmin pulsarAdmin;

    @Value("${spring.pulsar.allowed-clusters:standalone}")
    private String allowedClusters;


    /**
     * Creates a tenant in Pulsar. If the tenant already exists, it will not be created again.
     * @param tenant Name of the tenant
     * @return TenantInfo object representing the tenant
     * @throws PulsarAdminException If an error occurs while creating the tenant
     */
    public TenantInfo createTenant(String tenant) throws PulsarAdminException
    {
        var tenantInfo = new TenantInfoImpl();
        tenantInfo.setAllowedClusters(allowedClusters());
        return createTenant(tenant, tenantInfo);
    }

    /**
     * Creates a tenant in Pulsar. If the tenant already exists, it will not be created again.
     * @param tenant Name of the tenant
     * @param tenantInfo TenantInfo with tenants policies
     * @return TenantInfo object representing the tenant
     * @throws PulsarAdminException If an error occurs while creating the tenant
     */
    public TenantInfo createTenant(String tenant, TenantInfo tenantInfo) throws PulsarAdminException
    {
        log.debug("Creating tenant \"{}\" in Pulsar", tenant);
        pulsarAdmin.tenants().createTenant(tenant, tenantInfo);
        return tenantInfo;
    }

    /**
     * Deletes a tenant in Pulsar
     * @param tenant Name of the tenant
     * @throws PulsarAdminException If an error occurs while deleting the tenant
     */
    public void deleteTenant(String tenant) throws PulsarAdminException
    {
        pulsarAdmin.tenants().deleteTenant(tenant);
    }

    /**
     * Creates a namespace in Pulsar. If the namespace already exists, it will not be created again.
     * @param namespace Name of the namespace
     * @param policies Policies for the namespace
     * @throws PulsarAdminException If an error occurs while creating the namespace
     */
    public void createNamespace(String tenant, String namespace, Policies policies) throws PulsarAdminException
    {
        log.debug("Setting up namespace \"{}\" in Pulsar", namespace);
        if (pulsarAdmin.namespaces().getNamespaces(tenant).contains(namespace))
        {
            log.debug("Namespace \"{}\" already exists", namespace);
            return;
        }

        pulsarAdmin.namespaces().createNamespace(namespace, policies);
    }

    /**
     * Creates a Topic in Pulsar. If the topic already exists, it will not be created again.
     * @param namespace Namespace in which the topic should be created
     * @param topic Name of the topic
     * @param numPartitions Number of partitions for the topic
     * @return PulsarTopic object representing the topic
     * @throws PulsarAdminException If an error occurs while creating the topic
     */
    public PulsarTopic createTopic(String namespace, String topic, int numPartitions) throws PulsarAdminException
    {
        log.debug("Setting up topic \"{}\" for Pulsar", topic);
        var pulsarTopic = PulsarTopic.builder(topic).numberOfPartitions(numPartitions).build();
        if (pulsarAdmin.topics().getList(namespace).stream().anyMatch(t -> t.startsWith(topic)))
        {
            log.debug("Topic \"{}\" already exists", topic);
            return pulsarTopic;
        }

        pulsarAdmin.topics().createPartitionedTopic(topic, numPartitions);
        return pulsarTopic;
    }

    @Bean
    TenantInfo rizeTenant() throws PulsarAdminException
    {
        log.debug("Setting up tenant \"{}\" for Pulsar", RIZE_TENANT);
        var rizeTenant = getRizeTenant(pulsarAdmin).orElse(null);
        if (rizeTenant == null)
            rizeTenant = createTenant(RIZE_TENANT);

        createNamespace(RIZE_TENANT, RIZE_PLATFORM_NAMESPACE, platformNamespacePolicies());

        return rizeTenant;
    }

    @Bean
    PulsarTopic organizationStateTopic(TenantInfo rizeTenant) throws PulsarAdminException
    {
        return createTopic(RIZE_PLATFORM_NAMESPACE, ORGANIZATION_STATE_TOPIC, 1);
    }

    private Optional<TenantInfo> getRizeTenant(PulsarAdmin pulsarAdmin) throws PulsarAdminException
    {
        if (!pulsarAdmin.tenants().getTenants().contains(RIZE_TENANT))
            return Optional.empty();

        log.debug("Tenant \"{}\" already exists", RIZE_TENANT);
        return Optional.of(pulsarAdmin.tenants().getTenantInfo(RIZE_TENANT));
    }

    private Set<String> allowedClusters()
    {
        if (allowedClusters.contains(","))
            return Set.of(allowedClusters.split(","));
        return Set.of(allowedClusters);
    }

    private Policies platformNamespacePolicies()
    {
        var policies = new Policies();
        policies.compaction_threshold = 30000L;
        policies.retention_policies = new RetentionPolicies(-1, -1);
        return policies;
    }
}
