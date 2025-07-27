package org.springframework.aop.framework;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    private final AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                advised.getTargetClass().getInterfaces(), // 这里直接调用 getTargetClass()
                this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodInterceptor interceptor = advised.getMethodInterceptor();
        return interceptor.invoke(new MethodInvocation(advised.getTarget(), method, args));
    }
}