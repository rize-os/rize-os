package rize.os.platform.organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("aot-config")
@RequiredArgsConstructor
public class OrganizationAotConfiguration
{
    @Bean
    CommandLineRunner runConfiguration(OrganizationService organizationService)
    {
        return args ->
        {
            log.info("--------------------------------------------------------------------------------");
            log.info("--- Run methods from OrganizationService.class to generate AOT configuration ---");
            log.info("--------------------------------------------------------------------------------");

            var organizationToCreate = Organization.builder().name("organization-aot-configuration").displayName("Organization AOT Configuration").region("dummy").build();
            var organization = organizationService.createOrganization(organizationToCreate);

            organization.setDisplayName("Organization AOT Configuration Updated");
            organization = organizationService.updateOrganization(organization);

            organizationService.findAll();
            organizationService.findOrganizationById(organization.getId());
            organizationService.findOrganizationByName(organization.getName());
            organizationService.deleteOrganization(organization.getId());

            log.info("--------------------------------------------------------------------------------");
        };
    }
}
