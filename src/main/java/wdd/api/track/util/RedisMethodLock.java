package wdd.api.track.util;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisMethodLock {

    boolean useArgs() default false;

    boolean useClass() default false;
}
