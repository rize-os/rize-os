package rize.os.platform.datasource;

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

        log.info("Initialized data source connection to: '{}'", dataSource.getConnection().getMetaData().getURL());
        return dataSource;
    }
}
