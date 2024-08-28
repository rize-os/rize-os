package rize.os.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;

@Theme("rize-os")
@SpringBootApplication
public class Platform implements AppShellConfigurator
{
    public static void main(String[] args)
    {
        SpringApplication.run(Platform.class, args);
    }
}
