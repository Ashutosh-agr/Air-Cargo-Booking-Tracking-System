package com.aircargo.booking.lock;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(withDistributedLock)")
    public Object around(ProceedingJoinPoint pjp, WithDistributedLock withDistributedLock) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        var method = sig.getMethod();
        var context = new MethodBasedEvaluationContext(pjp.getTarget(), method, pjp.getArgs(), discoverer);
        String key = parser.parseExpression(withDistributedLock.key()).getValue(context, String.class);

        RLock lock = redissonClient.getLock(key);
        boolean acquired = lock.tryLock(withDistributedLock.waitTime(), withDistributedLock.leaseTime(), withDistributedLock.unit());

        if (!acquired) {
            throw new IllegalStateException("Could not acquire lock for key=" + key);
        }
        try {
            return pjp.proceed();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
