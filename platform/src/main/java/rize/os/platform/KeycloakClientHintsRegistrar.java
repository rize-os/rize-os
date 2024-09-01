package rize.os.platform;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages_$bundle;
import org.keycloak.admin.client.spi.ResteasyClientClassicProvider;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.NonNull;

import java.util.Arrays;

public class KeycloakClientHintsRegistrar implements RuntimeHintsRegistrar
{
    @Override
    public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader)
    {
        registerClass(hints, ResteasyClientClassicProvider.class);
        registerClass(hints, ResteasyClientBuilderImpl.class);
        registerClass(hints, LogMessages.class);
        registerClass(hints, Messages_$bundle.class);
        registerFields(hints, Messages_$bundle.class, "INSTANCE");
    }

    private void registerClass(RuntimeHints hints, Class<?> clazz)
    {
        Arrays.stream(clazz.getConstructors()).toList().forEach(constructor -> hints.reflection().registerConstructor(constructor, ExecutableMode.INVOKE));
        Arrays.stream(clazz.getMethods()).toList().forEach(method -> hints.reflection().registerMethod(method, ExecutableMode.INVOKE));
    }

    private void registerFields(RuntimeHints hints, Class<?> clazz, String... fields)
    {
        try
        {
            for (String field : fields)
                hints.reflection().registerField(clazz.getField(field));
        }
        catch (Exception ignore) { }
    }
}
