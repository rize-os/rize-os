package rize.os.platform;

import com.vaadin.hilla.springnative.HillaHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.scheduling.annotation.EnableAsync;
import rize.os.commons.EnableRizeOS;

@EnableRizeOS
@EnableAsync
@Theme("rize-os")
@SpringBootApplication
@ImportRuntimeHints({ PlatformHintsRegistrar.class, HillaHintsRegistrar.class })
public class Platform implements AppShellConfigurator
{
    public static void main(String[] args)
    {
        SpringApplication.run(Platform.class, args);
    }
}
