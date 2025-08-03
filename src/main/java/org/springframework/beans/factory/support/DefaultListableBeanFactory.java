package org.springframework.beans.factory.support;

import org.springframework.aop.framework.LoggingInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

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
        System.out.println("Getting bean: " + name);

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
                System.out.println("Creating new singleton bean: " + name);
                bean = createBean(beanDefinition);
                // 设置属性值
                System.out.println("Applying property values to: " + name);
                applyPropertyValues(bean, beanDefinition);
                singletonObjects.put(name, bean);
                System.out.println("Stored singleton bean: " + name);
            } else {
                System.out.println("Returning existing singleton bean: " + name);
            }
            System.out.println("Final bean '" + name + "': " + bean + " with dataSource: " +
                    (bean instanceof DataSourceTransactionManager ?
                            ((DataSourceTransactionManager) bean).getDataSource() : "N/A"));
            return bean;
        } else {
            // 原型bean处理：每次直接创建新的实例
            System.out.println("Creating prototype bean: " + name);
            return createBean(beanDefinition);
        }
    }

    // 应用属性值
    // 简化版的属性设置方法
    private void applyPropertyValues(Object bean, BeanDefinition beanDefinition) {
        try {
            MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();

            System.out.println("=== Applying property values to bean: " + bean.getClass().getName() + " ===");
            System.out.println("Property count: " + propertyValues.getPropertyValueList().size());

            for (PropertyValue propertyValue : propertyValues.getPropertyValueList()) {
                String propertyName = propertyValue.getName();
                Object value = propertyValue.getValue();

                System.out.println("Processing property: " + propertyName);
                System.out.println("Raw value: " + value + " (type: " + (value != null ? value.getClass().getName() : "null") + ")");

                // 处理引用类型的属性值
                Object actualValue;
                if (value instanceof RuntimeBeanReference) {
                    String refBeanName = ((RuntimeBeanReference) value).getBeanName();
                    System.out.println("Resolving reference: " + refBeanName);
                    actualValue = getBean(refBeanName);
                    System.out.println("Resolved to: " + actualValue + " (type: " + (actualValue != null ? actualValue.getClass().getName() : "null") + ")");
                } else {
                    actualValue = value;
                    System.out.println("Using literal value: " + actualValue);
                }

                // 使用更通用的方法设置属性
                setPropertyOnBean(bean, propertyName, actualValue);
            }
            System.out.println("=== Finished applying property values ===");
        } catch (Exception e) {
            System.err.println("Failed to apply property values: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to apply property values", e);
        }
    }

    // 设置Bean属性 - 改进版本
    private void setPropertyOnBean(Object bean, String propertyName, Object value) throws Exception {
        System.out.println("Setting property '" + propertyName + "' on bean: " + bean.getClass().getName() +
                " with value: " + value + " (type: " + (value != null ? value.getClass().getName() : "null") + ")");

        // 构造setter方法名
        String setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) +
                propertyName.substring(1);

        System.out.println("Looking for method: " + setterName);

        // 获取目标类，如果是代理对象则获取原始类
        Class<?> targetClass = getTargetClass(bean);

        // 获取目标类的所有方法
        Method[] methods = targetClass.getMethods();

        Method targetMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                Class<?> paramType = method.getParameterTypes()[0];
                System.out.println("Found matching method: " + method.getName() +
                        " with param type: " + paramType.getName());

                // 检查参数类型兼容性
                if (value == null && !paramType.isPrimitive()) {
                    targetMethod = method;
                    break;
                } else if (value != null && isCompatibleType(paramType, value.getClass())) {
                    targetMethod = method;
                    break;
                }
            }
        }

        if (targetMethod != null) {
            targetMethod.setAccessible(true);
            Object convertedValue = convertValue(targetMethod.getParameterTypes()[0], value);
            System.out.println("Invoking method: " + targetMethod.getName() + " with value: " + convertedValue);
            targetMethod.invoke(bean, convertedValue);
            System.out.println("Successfully set property: " + propertyName);
        } else {
            System.err.println("No suitable setter method found for property: " + propertyName +
                    " on class: " + targetClass.getName());
            // 列出所有方法帮助调试
            System.err.println("Available methods:");
            for (Method method : methods) {
                if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                    System.err.println("  " + method.getName() + "(" + method.getParameterTypes()[0].getName() + ")");
                }
            }
            throw new RuntimeException("No suitable setter method found for property: " + propertyName);
        }
    }

    // 改进类型兼容性检查
    private boolean isCompatibleType(Class<?> targetType, Class<?> valueType) {
        if (valueType == null) {
            return !targetType.isPrimitive();
        }

        // 直接匹配
        if (targetType.isAssignableFrom(valueType)) {
            return true;
        }

        // 基本类型和包装类型
        if (targetType.isPrimitive()) {
            return isPrimitiveMatch(targetType, valueType);
        }

        return false;
    }

    // 获取目标类，正确处理代理对象
    private Class<?> getTargetClass(Object bean) {
        // 检查是否为代理对象
        if (isProxyObject(bean)) {
            // 如果是自定义的ProxyFactory创建的代理，尝试获取目标对象
            try {
                Method getTargetMethod = bean.getClass().getMethod("getTarget");
                if (getTargetMethod != null) {
                    getTargetMethod.setAccessible(true);
                    Object target = getTargetMethod.invoke(bean);
                    if (target != null) {
                        return target.getClass();
                    }
                }
            } catch (Exception e) {
                // 如果无法通过target字段获取，尝试其他方式
                System.out.println("Could not get target object through reflection: " + e.getMessage());
                System.out.println("Could not get target object through reflection: " + e);
            }
        }
        return bean.getClass();
    }

    // 检查是否为代理对象
    private boolean isProxyObject(Object bean) {
        String className = bean.getClass().getName();
        return className.contains("proxy") || className.contains("Proxy") ||
                bean.getClass().getInterfaces().length > 0;
    }

    // 获取实际目标类
    private Class<?> getActualTargetClass(Object proxy) {
        // 如果是代理对象，直接返回ProxyFactory中的目标类
        // 或者通过其他方式获取原始类

        // 对于JDK代理，可以通过接口获取实现类信息
        Class<?>[] interfaces = proxy.getClass().getInterfaces();

        // 由于我们无法直接访问代理的内部结构，这里采用一种启发式方法
        // 检查代理对象是否包含特定的属性设置方法
        try {
            // 如果是自定义代理，可以添加特定的获取目标类的方法
            Method targetClassMethod = proxy.getClass().getMethod("getTargetClass");
            if (targetClassMethod != null) {
                targetClassMethod.setAccessible(true);
                return (Class<?>) targetClassMethod.invoke(proxy);
            }
        } catch (Exception e) {
            // 忽略异常
        }

        // 如果无法确定，返回代理类本身
        return proxy.getClass();
    }

    // 在类层次结构中查找方法
    private Method findMethodInHierarchy(Class<?> clazz, String methodName, Object value) {
        // 从当前类开始，向上遍历继承层次
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            try {
                // 获取当前类声明的所有方法
                Method[] methods = currentClass.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
                        Class<?> paramType = method.getParameterTypes()[0];
                        if (value == null && !paramType.isPrimitive()) {
                            return method;
                        } else if (value != null && isCompatibleType(paramType, value.getClass())) {
                            return method;
                        }
                    }
                }
            } catch (Exception e) {
                // 继续检查父类
            }
            currentClass = currentClass.getSuperclass();
        }
        return null;
    }

    // 检查类型兼容性
    private boolean isCompatibleType(Class<?> paramType, Object value) {
        if (value == null) {
            return !paramType.isPrimitive();
        }

        Class<?> valueType = value.getClass();

        // 直接匹配
        if (paramType.isAssignableFrom(valueType)) {
            return true;
        }

        // 基本类型和包装类型匹配
        if (paramType.isPrimitive()) {
            return isPrimitiveMatch(paramType, valueType);
        }

        // 字符串转换
        if (paramType == String.class && valueType == String.class) {
            return true;
        }

        return false;
    }

    // 检查基本类型匹配
    private boolean isPrimitiveMatch(Class<?> primitiveType, Class<?> valueType) {
        if (primitiveType == int.class) return valueType == Integer.class;
        if (primitiveType == long.class) return valueType == Long.class;
        if (primitiveType == double.class) return valueType == Double.class;
        if (primitiveType == float.class) return valueType == Float.class;
        if (primitiveType == boolean.class) return valueType == Boolean.class;
        if (primitiveType == byte.class) return valueType == Byte.class;
        if (primitiveType == char.class) return valueType == Character.class;
        if (primitiveType == short.class) return valueType == Short.class;
        return false;
    }

    // 转换值类型
    private Object convertValue(Class<?> targetType, Object value) {
        if (value == null) return null;

        if (targetType == String.class) {
            return value.toString();
        }

        if (value instanceof String) {
            String stringValue = (String) value;
            if (targetType == int.class || targetType == Integer.class) {
                return Integer.parseInt(stringValue);
            }
            if (targetType == long.class || targetType == Long.class) {
                return Long.parseLong(stringValue);
            }
            if (targetType == boolean.class || targetType == Boolean.class) {
                return Boolean.parseBoolean(stringValue);
            }
            if (targetType == short.class || targetType == Short.class) {
                return Short.parseShort(stringValue);
            }
            if (targetType == byte.class || targetType == Byte.class) {
                return Byte.parseByte(stringValue);
            }
            if (targetType == float.class || targetType == Float.class) {
                return Float.parseFloat(stringValue);
            }
            if (targetType == double.class || targetType == Double.class) {
                return Double.parseDouble(stringValue);
            }
        }

        // 对于其他类型，直接返回原值
        return value;
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
            Class<?> beanClass = beanDefinition.getBeanClass();
            Object bean;

            // 检查是否为接口或抽象类
            if (beanClass.isInterface() || java.lang.reflect.Modifier.isAbstract(beanClass.getModifiers())) {
                // 尝试从已注册的bean中查找实现该接口的bean
                bean = findImplementationForType(beanClass);
                if (bean == null) {
                    throw new RuntimeException("Cannot instantiate interface or abstract class: " + beanClass.getName() +
                            ". No implementation found.");
                }
            } else {
                // 根据是否有构造函数参数决定创建方式
                if (beanDefinition.hasConstructorArgumentValues()) {
                    // 使用有参构造函数创建实例
                    bean = createBeanWithArguments(beanDefinition);
                } else {
                    // 使用无参构造函数创建实例
                    bean = beanClass.newInstance();
                }
            }
            return initializeBean(bean, beanClass.getSimpleName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean", e);
        }
    }
    /**
     * 使用构造函数参数创建Bean实例
     *
     * @param beanDefinition Bean定义信息，包含构造函数参数
     * @return 创建的Bean实例
     * @throws Exception 创建过程中可能抛出的异常
     */
    /**
     * 使用构造函数参数创建Bean实例
     *
     * @param beanDefinition Bean定义信息，包含构造函数参数
     * @return 创建的Bean实例
     * @throws Exception 创建过程中可能抛出的异常
     */
    private Object createBeanWithArguments(BeanDefinition beanDefinition) throws Exception {
        // 获取构造函数参数
        BeanDefinition.ConstructorArgumentValues constructorArgs = beanDefinition.getConstructorArgumentValues();
        List<BeanDefinition.ConstructorArgumentValues.ValueHolder> argumentValues =
                constructorArgs.getGenericArgumentValues();

        // 准备参数值和类型
        Object[] args = new Object[argumentValues.size()];
        Class<?>[] argTypes = new Class[argumentValues.size()];

        System.out.println("Preparing constructor arguments for: " + beanDefinition.getBeanClass().getName());
        System.out.println("Argument count: " + argumentValues.size());

        for (int i = 0; i < argumentValues.size(); i++) {
            Object value = argumentValues.get(i).getValue();

            System.out.println("Processing argument[" + i + "]: " + value +
                    " (type: " + (value != null ? value.getClass().getName() : "null") + ")");

            // 如果参数值是Bean引用（RuntimeBeanReference）
            if (value instanceof RuntimeBeanReference) {
                String refBeanName = ((RuntimeBeanReference) value).getBeanName();
                System.out.println("Resolving reference: " + refBeanName);
                args[i] = getBean(refBeanName); // 递归获取依赖的Bean
                argTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
                System.out.println("Resolved to: " + args[i] + " (type: " + argTypes[i].getName() + ")");
            } else {
                args[i] = value;
                argTypes[i] = value != null ? value.getClass() : Object.class;
                System.out.println("Using literal value: " + args[i] + " (type: " + argTypes[i].getName() + ")");
            }
        }

        // 查找匹配的构造函数
        Class<?> beanClass = beanDefinition.getBeanClass();

        // 尝试获取所有构造函数（包括非公共的）
        Constructor<?>[] constructors = beanClass.getConstructors();
        if (constructors.length == 0) {
            constructors = beanClass.getDeclaredConstructors();
        }

        System.out.println("Found " + constructors.length + " constructors for " + beanClass.getName());

        // 尝试每个构造函数，直到找到一个可以工作的
        for (Constructor<?> constructor : constructors) {
            System.out.println("Trying constructor: " + constructor);

            if (constructor.getParameterCount() == args.length) {
                try {
                    // 检查参数类型兼容性
                    if (isConstructorCompatible(constructor, argTypes, args)) {
                        constructor.setAccessible(true); // 允许访问非公共构造函数
                        System.out.println("Using constructor: " + constructor);
                        Object instance = constructor.newInstance(args);
                        System.out.println("Successfully created bean with constructor: " + constructor);
                        return instance;
                    } else {
                        System.out.println("Constructor not compatible: " + constructor);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to use constructor " + constructor + ", error: " + e.getMessage());
                    e.printStackTrace();
                    // 继续尝试下一个构造函数
                }
            } else {
                System.out.println("Parameter count mismatch: expected " + constructor.getParameterCount() +
                        ", got " + args.length);
            }
        }

        throw new RuntimeException("No compatible constructor found for " + beanClass.getName() +
                " with provided arguments. Args types: " + Arrays.toString(argTypes));
    }

    // 改进的构造函数兼容性检查
    private boolean isConstructorCompatible(Constructor<?> constructor, Class<?>[] argTypes, Object[] args) {
        Class<?>[] paramTypes = constructor.getParameterTypes();

        if (paramTypes.length != argTypes.length) {
            return false;
        }

        for (int i = 0; i < paramTypes.length; i++) {
            if (!isAssignable(paramTypes[i], argTypes[i], args[i])) {
                System.out.println("Parameter type mismatch at index " + i + ": expected " + paramTypes[i] +
                        ", got " + argTypes[i] + " (value: " + args[i] + ")");
                return false;
            }
        }

        return true;
    }

    // 改进的类型分配检查
    private boolean isAssignable(Class<?> targetType, Class<?> sourceType, Object value) {
        // 空值可以赋给任何引用类型
        if (value == null && !targetType.isPrimitive()) {
            return true;
        }

        // 直接相等
        if (targetType.equals(sourceType)) {
            return true;
        }

        // 目标类型是源类型的父类或接口
        if (targetType.isAssignableFrom(sourceType)) {
            return true;
        }

        // 处理基本类型和包装类型
        if (targetType.isPrimitive() && sourceType != null) {
            return isPrimitiveWrapperMatch(targetType, sourceType);
        }

        // 处理接口实现
        if (targetType.isInterface() && sourceType != null) {
            return targetType.isAssignableFrom(sourceType);
        }

        return false;
    }
    /**
     * 查找指定类型的实现bean
     *
     * @param type 接口或抽象类类型
     * @return 实现该类型的bean实例，如果找不到则返回null
     */
    private Object findImplementationForType(Class<?> type) {
        // 遍历所有已注册的bean定义
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            Class<?> beanClass = entry.getValue().getBeanClass();
            // 检查该bean是否实现了指定的接口且不是接口或抽象类本身
            if (type.isAssignableFrom(beanClass) &&
                    !beanClass.isInterface() &&
                    !java.lang.reflect.Modifier.isAbstract(beanClass.getModifiers())) {
                // 返回该bean的实例
                return getBean(entry.getKey());
            }
        }
        return null;
    }

    /**
     * 查找匹配的构造函数
     *
     * @param beanClass Bean的类
     * @param argTypes 参数类型数组
     * @param args 参数值数组
     * @return 匹配的构造函数，如果找不到则返回null
     */
    private Constructor<?> findMatchingConstructor(Class<?> beanClass, Class<?>[] argTypes, Object[] args) {
        Constructor<?>[] constructors = beanClass.getConstructors();

        // 首先尝试精确匹配
        for (Constructor<?> constructor : constructors) {
            if (isConstructorMatch(constructor, argTypes)) {
                return constructor;
            }
        }

        // 如果没有精确匹配，尝试兼容性匹配
        for (Constructor<?> constructor : constructors) {
            if (isConstructorCompatible(constructor, argTypes, args)) {
                return constructor;
            }
        }

        return null;
    }

    /**
     * 检查构造函数是否与给定参数类型精确匹配
     *
     * @param constructor 要检查的构造函数
     * @param argTypes 参数类型数组
     * @return 如果匹配返回true，否则返回false
     */
    private boolean isConstructorMatch(Constructor<?> constructor, Class<?>[] argTypes) {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        if (paramTypes.length != argTypes.length) {
            return false;
        }

        for (int i = 0; i < paramTypes.length; i++) {
            if (!paramTypes[i].equals(argTypes[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查基本类型和包装类型是否匹配
     *
     * @param targetType 目标类型
     * @param sourceType 源类型
     * @return 如果匹配返回true，否则返回false
     */
    private boolean isPrimitiveWrapperMatch(Class<?> targetType, Class<?> sourceType) {
        if (targetType.isPrimitive()) {
            return getWrapperClass(targetType).equals(sourceType);
        } else if (sourceType.isPrimitive()) {
            return getWrapperClass(sourceType).equals(targetType);
        }
        return false;
    }

    /**
     * 获取基本类型的包装类
     *
     * @param primitiveType 基本类型
     * @return 对应的包装类
     */
    private Class<?> getWrapperClass(Class<?> primitiveType) {
        if (primitiveType == int.class) return Integer.class;
        if (primitiveType == boolean.class) return Boolean.class;
        if (primitiveType == byte.class) return Byte.class;
        if (primitiveType == char.class) return Character.class;
        if (primitiveType == short.class) return Short.class;
        if (primitiveType == long.class) return Long.class;
        if (primitiveType == float.class) return Float.class;
        if (primitiveType == double.class) return Double.class;
        return primitiveType;
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
        // 检查是否有 AOP 配置
        if (shouldApplyProxy(bean)) {
            return createProxy(bean);
        }
        return bean;
    }

    private boolean shouldApplyProxy(Object bean) {
        // 只对实现了接口的类应用JDK动态代理
        // 但排除数据源相关类
        if (bean instanceof DataSource ||
                bean instanceof PlatformTransactionManager) {
            return false;
        }
        return bean.getClass().getInterfaces().length > 0;
    }

    private Object createProxy(Object target) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.setMethodInterceptor(new LoggingInterceptor());
        return proxyFactory.getProxy();
    }

}
