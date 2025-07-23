package rize.os.commons.annotations;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ComponentScan("rize.os")
public @interface EnableRizeOS
{
}
