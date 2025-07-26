package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinition;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认的可列出Bean工厂实现类
 * 
 * 该类是Spring框架中最核心的IoC容器实现之一，提供了Bean定义的注册、Bean实例的创建和管理功能。
 * 它支持单例(Singleton)和原型(Prototype)两种作用域的Bean管理。
 * 
 * 主要功能包括：
 * 1. Bean定义的注册与存储
 * 2. 根据Bean定义创建Bean实例
 * 3. 单例Bean的缓存管理
 * 4. 根据名称获取Bean实例
 */
public class DefaultListableBeanFactory implements BeanFactory {
    
    /**
     * 存储Bean定义的映射表，key为Bean名称，value为对应的Bean定义
     * 用于存储从配置文件或注解中解析到的Bean元数据信息
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    
    /**
     * 存储单例Bean实例的映射表，key为Bean名称，value为对应的Bean实例
     * 用于缓存已经创建的单例Bean，避免重复创建，提高性能
     */
    private final Map<String, Object> singletonObjects = new HashMap<>();

    /**
     * 存储 Bean 后处理器的列表，用于在 Bean 初始化过程中执行自定义的处理逻辑。
     */
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    /**
     * 添加Bean后处理器
     *
     * @param beanPostProcessor Bean后处理器实例，用于在Bean初始化前后执行自定义逻辑
     */
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.add(beanPostProcessor);
    }


    /**
     * 注册Bean定义到容器中
     *
     * 将Bean的元数据信息（BeanDefinition）注册到容器中，供后续创建Bean实例时使用。
     * 如果已存在相同名称的Bean定义，新定义会覆盖旧定义。
     *
     * @param name Bean的名称，作为唯一标识符用于后续获取Bean实例
     * @param beanDefinition Bean的定义信息，包含Bean的类信息、作用域、依赖等元数据
     */
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(name, beanDefinition);
    }

    /**
     * 根据bean名称获取对应的bean实例
     * 
     * 这是BeanFactory接口的核心方法，负责根据Bean名称返回对应的Bean实例。
     * 该方法会根据Bean的作用域决定是从缓存中获取还是创建新的实例。
     *
     * @param name bean的名称，用于在容器中查找对应的Bean定义
     * @return 返回指定名称的bean实例，类型为Object，需要使用者进行类型转换
     * @throws RuntimeException 当指定名称的bean未定义时抛出异常
     */
    @Override
    public Object getBean(String name) {
        // 从bean定义映射中获取指定名称的bean定义
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (beanDefinition == null) {
            throw new RuntimeException("No bean named '" + name + "' is defined");
        }

        // 根据作用域类型创建或获取bean实例
        if ("singleton".equals(beanDefinition.getScope())) {
            // 单例bean处理：先从单例池中获取，不存在则创建并缓存
            Object bean = singletonObjects.get(name);
            if (bean == null) {
                bean = createBean(beanDefinition);
                singletonObjects.put(name, bean);
            }
            return bean;
        } else {
            // 原型bean处理：每次直接创建新的实例
            return createBean(beanDefinition);
        }
    }

    /**
     * 根据BeanDefinition创建Bean实例
     *
     * 通过Java反射机制创建Bean的实例，这是IoC容器的核心功能之一。
     * 该方法会调用Bean类的无参构造函数来创建实例。
     *
     * @param beanDefinition Bean定义信息，包含Bean的类信息，用于获取要实例化的类
     * @return 创建的Bean实例对象，类型为Object
     * @throws RuntimeException 当Bean创建失败时抛出运行时异常，如类未找到、无法访问构造函数等
     */
    private Object createBean(BeanDefinition beanDefinition) {
        try {
            // 通过反射创建Bean实例
            Object bean = beanDefinition.getBeanClass().newInstance();
            return initializeBean(bean, beanDefinition.getBeanClass().getSimpleName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean", e);
        }
    }

    /**
     * 初始化Bean实例
     *
     * @param bean 需要初始化的Bean实例
     * @param beanName Bean的名称
     * @return 初始化后的Bean实例
     */
    private Object initializeBean(Object bean, String beanName) {
        // 执行所有Bean后处理器的初始化前处理逻辑
        for (BeanPostProcessor processor : beanPostProcessors) {
            bean = processor.postProcessBeforeInitialization(bean, beanName);
        }
        return bean;
    }

}
