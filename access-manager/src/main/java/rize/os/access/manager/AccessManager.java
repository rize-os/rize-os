package rize.os.access.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.scheduling.annotation.EnableAsync;

@Theme("rize-os")
@EnableAsync
@SpringBootApplication
public class AccessManager implements AppShellConfigurator
{
    public static void main(String[] args)
    {
        SpringApplication.run(AccessManager.class, args);
    }
}
