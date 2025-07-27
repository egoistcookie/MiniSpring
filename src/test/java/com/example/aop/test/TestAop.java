package com.example.aop.test;

import com.example.aop.service.AopUser;
import com.example.aop.service.impl.AopUserImpl;
import org.springframework.aop.framework.LoggingInterceptor;
import org.springframework.aop.framework.ProxyFactory;

/**
 * 单独测试一个aop功能
 */
public class TestAop {
    public static void main(String[] args) {
        // 1. 创建目标对象
        AopUser target = new AopUserImpl();

        // 2. 创建 ProxyFactory 并配置
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target); // 会自动设置 targetClass
        proxyFactory.setMethodInterceptor(new LoggingInterceptor());

        // 3. 获取代理对象
        AopUser proxy = (AopUser) proxyFactory.getProxy();

        // 4. 调用方法
        proxy.sayHello();
    }
}
