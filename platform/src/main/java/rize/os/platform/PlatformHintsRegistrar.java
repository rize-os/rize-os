package rize.os.platform;

import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.NonNull;
import rize.os.platform.organization.OrganizationEndpoint;

import java.util.Arrays;

public class PlatformHintsRegistrar implements RuntimeHintsRegistrar
{
    @Override
    public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader)
    {
        registerEndpoints(hints);
    }

    private void registerEndpoints(RuntimeHints hints)
    {
        registerEndpoint(hints, OrganizationEndpoint.class);
    }

    private void registerEndpoint(RuntimeHints hints, Class<?> endpointClass)
    {
        Arrays.stream(endpointClass.getMethods()).toList().forEach(method ->
                hints.reflection().registerMethod(method, ExecutableMode.INVOKE));
    }
}
