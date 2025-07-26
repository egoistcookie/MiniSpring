package org.springframework.beans.factory;

/**
 * Bean工厂接口
 *
 * 该接口是Spring框架中最核心的接口之一，定义了IoC容器的基本功能。
 * 它是Bean容器的顶层接口，提供了获取Bean实例的标准方法。
 *
 * Spring框架通过该接口实现了控制反转（Inversion of Control）和依赖注入（Dependency Injection）。
 * 具体的实现类（如DefaultListableBeanFactory）负责Bean的创建、配置和生命周期管理。
 */
public interface BeanFactory {

    /**
     * 根据Bean名称获取Bean实例
     *
     * 这是Bean工厂的核心方法，用于从容器中获取指定名称的Bean实例。
     * 容器会根据Bean的配置信息创建或返回已有的Bean实例。
     *
     * 注意事项：
     * 1. 返回的是Object类型，使用者需要根据实际情况进行类型转换
     * 2. 如果指定名称的Bean不存在，通常会抛出异常
     * 3. 对于singleton作用域的Bean，返回的是共享实例
     * 4. 对于prototype作用域的Bean，每次调用都会返回新实例
     *
     * @param name Bean的名称，对应配置中的id或name属性
     * @return 返回指定名称的Bean实例
     */
    Object getBean(String name);
}
