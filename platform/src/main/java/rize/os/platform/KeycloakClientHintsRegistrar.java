package rize.os.platform;

import org.keycloak.admin.client.spi.ResteasyClientClassicProvider;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
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
    }

    private void registerClass(Class<?> clazz, RuntimeHints hints)
    {
        hints.reflection().registerType(clazz);
        Arrays.stream(clazz.getMethods()).toList().forEach(method -> hints.reflection().registerMethod(method, ExecutableMode.INVOKE));
    }
}
