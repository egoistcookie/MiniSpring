package org.springframework.aop.framework;

/**
 * ProxyFactory 类是 Spring AOP 框架中用于创建 AOP 代理的核心工厂类。
 *
 * 该类继承自 AdvisedSupport，提供了创建代理对象的便捷方法。
 * 它封装了代理创建的复杂逻辑，是应用程序使用 Spring AOP 功能的主要入口点。
 *
 * 使用示例:
 * ProxyFactory proxyFactory = new ProxyFactory();
 * proxyFactory.setTarget(targetObject);
 * proxyFactory.addAdvice(advice);
 * Object proxy = proxyFactory.getProxy();
 */
public class ProxyFactory extends AdvisedSupport {

    /**
     * 创建并返回一个代理对象实例。
     *
     * 此方法是 ProxyFactory 的核心方法，它会根据配置的 advisors 和 targetSource
     * 来决定创建 JDK 动态代理还是 CGLIB 代理，并返回相应的代理对象。
     *
     * @return 代理对象实例，可以被安全地转换为目标对象实现的接口类型
     */
    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    /**
     * 创建 AopProxy 实例的私有方法。
     *
     * 该方法使用 DefaultAopProxyFactory 来创建适当的 AopProxy 实现，
     * 根据配置决定使用 JDK 动态代理还是 CGLIB 代理。
     *
     * @return AopProxy 实例，用于创建实际的代理对象
     */
    private AopProxy createAopProxy() {
        return new DefaultAopProxyFactory().createAopProxy(this);
    }
}
