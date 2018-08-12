package wdd.api.track.aspect;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import wdd.api.track.util.RedisMethodLock;
import wdd.api.track.util.RedisOperation;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

@Aspect
@Component
public class RedisMethodLockAspect {

    @Resource
    private RedisOperation redisOperation;

    @Around("@annotation(redisMethodLock)")
    public Object lockAround(ProceedingJoinPoint joinPoint, RedisMethodLock redisMethodLock) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        boolean useArgs = redisMethodLock.useArgs();
        boolean useClass = redisMethodLock.useClass();

        String lockName;
        if (useClass) {
            if (useArgs) {
                lockName = methodSignature.getDeclaringTypeName() + "::" + method.getName() +
                        "(" + StringUtils.join(joinPoint.getArgs(), ",") + ")";
            } else {
                lockName = methodSignature.getDeclaringTypeName() + "::" + method.getName();
            }
        } else {
            if (useArgs) {
                lockName = method.getName() +
                        "(" + StringUtils.join(joinPoint.getArgs(), ",") + ")";
            } else {
                lockName = method.getName();
            }
        }

        Lock methodLock = redisOperation.getLock(lockName);
        methodLock.lock();

        Object result;
        try {
            result = joinPoint.proceed(joinPoint.getArgs());
        }  finally {
            methodLock.unlock();
        }
        return result;
    }
}
