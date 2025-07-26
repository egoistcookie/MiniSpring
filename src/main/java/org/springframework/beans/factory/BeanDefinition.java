package org.springframework.beans.factory;

/**
 * Bean定义类
 *
 * 该类用于封装Bean的元数据信息，包括Bean的类型、作用域等配置信息。
 * 它是Spring IoC容器中Bean配置信息的内部表示形式。
 *
 * 主要包含以下元数据：
 * 1. Bean的Class对象（用于实例化）
 * 2. Bean的作用域（singleton或prototype）
 * 3. 未来可扩展的其他配置信息
 */
public class BeanDefinition {

    /**
     * Bean的Class对象
     * 用于通过反射创建Bean实例，包含了Bean的所有类型信息
     */
    private Class<?> beanClass;

    /**
     * Bean的作用域
     * 默认为"singleton"（单例），也可设置为"prototype"（原型）
     * singleton: 在容器中只创建一个实例，所有请求共享同一个实例
     * prototype: 每次请求都会创建一个新的实例
     */
    private String scope = "singleton"; // 默认单例

    /**
     * 获取Bean的Class对象
     *
     * @return Bean的Class对象，用于实例化Bean
     */
    public Class<?> getBeanClass() {
        return beanClass;
    }

    /**
     * 设置Bean的Class对象
     *
     * @param beanClass Bean的Class对象
     */
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * 获取Bean的作用域
     *
     * @return Bean的作用域字符串，"singleton"或"prototype"
     */
    public String getScope() {
        return scope;
    }

    /**
     * 设置Bean的作用域
     *
     * @param scope Bean的作用域，通常为"singleton"或"prototype"
     */
    public void setScope(String scope) {
        this.scope = scope;
    }
}
