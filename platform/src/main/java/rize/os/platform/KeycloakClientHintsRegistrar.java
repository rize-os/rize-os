package rize.os.platform;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
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
        registerClass(ResteasyClientClassicProvider.class, hints);
        registerClass(ResteasyClientBuilderImpl.class, hints);
        registerClass(Messages_$bundle.class, hints);
    }

    private void registerClass(Class<?> clazz, RuntimeHints hints)
    {
        Arrays.stream(clazz.getConstructors()).toList().forEach(constructor -> hints.reflection().registerConstructor(constructor, ExecutableMode.INVOKE));
        Arrays.stream(clazz.getMethods()).toList().forEach(method -> hints.reflection().registerMethod(method, ExecutableMode.INVOKE));
    }
}
