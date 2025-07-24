package rize.os.cockpit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rize.os.commons.annotations.EnableRizeOS;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;

@EnableRizeOS
@Theme("rize-os")
@SpringBootApplication
public class RizeCockpit implements AppShellConfigurator
{
    public static void main(String[] args)
    {
        SpringApplication.run(RizeCockpit.class, args);
    }
}
