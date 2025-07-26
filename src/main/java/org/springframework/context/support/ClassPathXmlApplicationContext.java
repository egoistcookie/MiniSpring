package org.springframework.context.support;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.XmlBeanDefinitionReader;
import java.io.InputStream;

/**
 * 类路径XML应用上下文实现
 *
 * 该类是Spring应用上下文的一个简化实现，专门用于从类路径加载XML配置文件。
 * 它整合了Bean工厂和XML解析功能，为用户提供了一个简单的IoC容器入口。
 *
 * 主要功能包括：
 * 1. 从类路径加载XML配置文件
 * 2. 解析XML中的Bean定义
 * 3. 创建和管理Bean实例
 * 4. 提供获取Bean实例的统一接口
 *
 * 使用示例:
 * ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
 * MyService myService = (MyService) context.getBean("myService");
 */
public class ClassPathXmlApplicationContext {

    /**
     * 默认的可列出Bean工厂实例
     * 作为应用上下文的核心组件，负责Bean的创建、配置和管理
     */
    private final DefaultListableBeanFactory beanFactory;

    /**
     * 构造函数，初始化应用上下文
     *
     * 在构造过程中会完成以下操作：
     * 1. 创建默认的Bean工厂实例
     * 2. 创建XML Bean定义读取器
     * 3. 从类路径加载指定的配置文件
     * 4. 解析配置文件中的Bean定义并注册到Bean工厂
     *
     * @param configLocation 配置文件路径，相对于类路径根目录（如："applicationContext.xml"）
     * @throws RuntimeException 当配置文件加载或解析失败时抛出运行时异常
     */
    public ClassPathXmlApplicationContext(String configLocation) {
        // 创建Bean工厂实例
        this.beanFactory = new DefaultListableBeanFactory();

        // 注册AutowiredAnnotationBeanPostProcessor以支持@Autowired注解
        // 这是实现依赖注入的关键组件
        AutowiredAnnotationBeanPostProcessor autowiredProcessor = new AutowiredAnnotationBeanPostProcessor(beanFactory);
        // 将后处理器添加到Bean工厂中，使其在Bean初始化过程中生效
        beanFactory.addBeanPostProcessor(autowiredProcessor);

        // 创建XML Bean定义读取器
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        // 从类路径加载配置文件
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configLocation);
        try {
            // 解析并加载Bean定义
            reader.loadBeanDefinitions(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据Bean名称获取Bean实例
     *
     * 这是应用上下文对外提供的核心方法，用于获取容器中管理的Bean实例。
     * 实际上是委托给内部的Bean工厂来完成具体操作。
     *
     * @param name Bean的名称，对应配置文件中bean元素的id属性
     * @return 返回指定名称的Bean实例
     * @see DefaultListableBeanFactory#getBean(String)
     */
    public Object getBean(String name) {
        return beanFactory.getBean(name);
    }
}
