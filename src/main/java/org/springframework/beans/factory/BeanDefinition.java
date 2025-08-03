package org.springframework.beans.factory;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean定义类
 *
 * 该类用于封装Bean的元数据信息，包括Bean的类型、作用域等配置信息。
 * 它是Spring IoC容器中Bean配置信息的内部表示形式。
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
     * 构造函数参数值列表
     */
    private ConstructorArgumentValues constructorArgumentValues;

    // 在 BeanDefinition.java 中添加
    private final MutablePropertyValues propertyValues = new MutablePropertyValues();

    public MutablePropertyValues getPropertyValues() {
        return propertyValues;
    }

    public BeanDefinition() {
        this.constructorArgumentValues = new ConstructorArgumentValues();
    }

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

    /**
     * 获取构造函数参数值
     * @return 构造函数参数值列表
     */
    public ConstructorArgumentValues getConstructorArgumentValues() {
        return constructorArgumentValues;
    }

    /**
     * 设置构造函数参数值
     * @param constructorArgumentValues 构造函数参数值列表
     */
    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }

    /**
     * 判断是否有构造函数参数
     * @return 如果有构造函数参数返回true，否则返回false
     */
    public boolean hasConstructorArgumentValues() {
        return constructorArgumentValues != null && !constructorArgumentValues.isEmpty();
    }

    /**
     * 构造函数参数值类
     */
    public static class ConstructorArgumentValues {
        private List<ValueHolder> argumentValues = new ArrayList<>();

        public void addGenericArgumentValue(Object value) {
            argumentValues.add(new ValueHolder(value));
        }

        public List<ValueHolder> getGenericArgumentValues() {
            return argumentValues;
        }

        public boolean isEmpty() {
            return argumentValues.isEmpty();
        }

        /**
         * 参数值持有者
         */
        public static class ValueHolder {
            private Object value;
            private String name;
            private String type;

            public ValueHolder(Object value) {
                this.value = value;
            }

            public Object getValue() {
                return value;
            }

            public void setValue(Object value) {
                this.value = value;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}

