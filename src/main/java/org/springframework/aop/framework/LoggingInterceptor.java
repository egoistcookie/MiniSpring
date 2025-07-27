package org.springframework.aop.framework;

public class LoggingInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("[AOP] Before: " + invocation.getMethod().getName());
        Object result = invocation.proceed();
        System.out.println("[AOP] After: " + invocation.getMethod().getName());
        return result;
    }
}
