package org.springframework.aop.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 基于JDK动态代理实现的AOP代理类。
 * 该类实现了 {@link AopProxy} 接口用于创建代理对象，
 * 同时实现了 {@link InvocationHandler} 接口来处理方法调用。
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    /**
     * 包含目标对象、拦截器链和其他AOP相关配置的 AdvisedSupport 实例。
     * 该对象保存了创建代理所需的所有信息。
     */
    private final AdvisedSupport advised;

    /**
     * 构造函数，初始化 JdkDynamicAopProxy 实例。
     *
     * @param advised 包含AOP配置信息的 {@link AdvisedSupport} 对象
     */
    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    /**
     * 创建并返回一个代理对象。
     * 使用JDK的 {@link java.lang.reflect.Proxy#newProxyInstance(ClassLoader, Class[], InvocationHandler)} 方法生成代理实例。
     *
     * @return 代理对象实例
     */
    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(
                getClass().getClassLoader(),              // 使用当前类的类加载器
                advised.getTargetClass().getInterfaces(), // 获取目标类实现的所有接口
                this                                      // 当前实例作为调用处理器
        );
    }

    /**
     * 处理代理对象上的方法调用。
     * 当代理对象的方法被调用时，会进入此方法进行统一处理。
     *
     * @param proxy  代理对象本身
     * @param method 被调用的方法对象
     * @param args   方法调用时传入的参数数组
     * @return 方法执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取方法拦截器（通常是一个包含了拦截逻辑的对象）
        MethodInterceptor interceptor = advised.getMethodInterceptor();

        // 将目标对象、方法和参数封装成 MethodInvocation 对象，并传递给拦截器执行
        return interceptor.invoke(new MethodInvocation(advised.getTarget(), method, args));
    }
}
