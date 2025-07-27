package org.springframework.aop.framework;

public class DefaultAopProxyFactory {
    public AopProxy createAopProxy(AdvisedSupport advised) {
        // 简单逻辑：如果目标有接口，用 JDK 代理；否则用 CGLIB（这里简化为只用 JDK）
        return new JdkDynamicAopProxy(advised);
    }
}