package rize.os.platform.datasource;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Configuration
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class DataSourceConfiguration
{
    @Bean
    DataSource dataSource(DataSourceProperties dataSourceProperties) throws SQLException
    {
        var dataSource = dataSourceProperties
                .initializeDataSourceBuilder()
                .build();

        log.info("Initialized data source connection to: '{}'", dataSourceProperties.getUrl());
        return dataSource;
    }

    @Bean
    SpringLiquibase liquibase(DataSource dataSource)
    {
        var liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");
        return liquibase;
    }
}
