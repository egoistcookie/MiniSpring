package org.springframework.aop.framework;

// 方法拦截器（类似 Spring 的 MethodInterceptor）
public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}

