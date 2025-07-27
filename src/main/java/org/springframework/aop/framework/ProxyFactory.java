package org.springframework.aop.framework;

public class ProxyFactory extends AdvisedSupport {
    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    private AopProxy createAopProxy() {
        return new DefaultAopProxyFactory().createAopProxy(this);
    }
}