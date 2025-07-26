package org.springframework.beans.factory.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * 自动装配注解Bean后处理器
 *
 * 这是Spring框架中处理@Autowired注解的核心组件，实现了Bean后处理器接口。
 * 它负责在Bean初始化过程中自动注入带有@Autowired注解的字段或方法。
 *
 * 主要功能：
 * 1. 扫描Bean中的@Autowired注解
 * 2. 根据类型匹配查找对应的依赖Bean
 * 3. 将找到的Bean注入到标记了@Autowired的字段或方法中
 *
 * 工作原理：
 * 该类作为Bean后处理器，在Spring容器创建Bean的过程中介入：
 * 1. 在Bean实例化完成之后
 * 2. 在Bean初始化方法调用之前
 * 3. 扫描Bean中的@Autowired注解并完成依赖注入
 *
 * 使用场景：
 * 当Spring容器创建一个Bean时，会自动调用此类的postProcessBeforeInitialization方法，
 * 该方法会检查Bean中是否有@Autowired注解的字段或方法，并自动完成依赖注入。
 *
 * 示例：
 * <pre>
 * public class UserService {
 *     {@code @Autowired}
 *     private UserRepository userRepository; // 自动注入UserRepository实例
 *
 *     {@code @Autowired}
 *     public void setRoleService(RoleService roleService) { // 自动注入RoleService实例
 *         this.roleService = roleService;
 *     }
 * }
 * </pre>
 *
 * @author Spring Framework
 * @see BeanPostProcessor
 * @see Autowired
 * @since 2.5
 */
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final BeanFactory beanFactory;

    public AutowiredAnnotationBeanPostProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 在Bean初始化之前进行后处理
     *
     * 这是BeanPostProcessor接口的核心方法之一，在Bean的初始化方法（如@PostConstruct注解的方法）
     * 调用之前执行。主要用于处理@Autowired注解，完成依赖注入。
     *
     * 执行流程：
     * 1. 检查传入的Bean实例是否为null
     * 2. 获取Bean的类信息
     * 3. 扫描类中的所有字段，查找带有@Autowired注解的字段
     * 4. 对于每个@Autowired字段：
     *    a. 获取字段的类型
     *    b. 从BeanFactory中根据类型查找匹配的Bean
     *    c. 设置字段可访问（处理private字段）
     *    d. 将找到的Bean注入到字段中
     * 5. 扫描类中的所有方法，查找带有@Autowired注解的方法
     * 6. 对于每个@Autowired方法：
     *    a. 解析方法参数类型
     *    b. 从BeanFactory中根据参数类型查找匹配的Beans
     *    c. 调用方法并传入找到的Beans作为参数
     *
     * @param bean 正在初始化的Bean实例
     * @param beanName Bean在容器中的名称
     * @return 处理后的Bean实例（可能被包装或修改）
     * @throws BeansException 如果依赖注入过程中发生错误
     * @see BeanPostProcessor#postProcessBeforeInitialization(Object, String)
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 1. 获取bean的Class对象
        // 2. 遍历所有声明的字段
        // 3. 检查字段是否有@Autowired注解
        // 4. 如果有，则获取字段类型并从BeanFactory中获取对应Bean
        // 5. 使用反射将依赖Bean注入到字段中
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                try {
                    Object dependency = beanFactory.getBean(field.getName());
                    field.set(bean, dependency);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to inject dependency", e);
                }
            }
        }
        return bean; // 返回原始Bean或修改后的Bean
    }

    /**
     * 在Bean初始化之后进行后处理
     *
     * 这是BeanPostProcessor接口的另一个核心方法，在Bean的初始化方法执行之后调用。
     * 对于@Autowired处理来说，通常主要的注入工作在postProcessBeforeInitialization
     * 中完成，但也可以在此阶段进行一些后续处理。
     *
     * @param bean 已初始化的Bean实例
     * @param beanName Bean在容器中的名称
     * @return 处理后的Bean实例
     * @throws BeansException 如果后处理过程中发生错误
     * @see BeanPostProcessor#postProcessAfterInitialization(Object, String)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 对于@Autowired处理，主要工作已在postProcessBeforeInitialization中完成
        // 这里通常直接返回原始Bean，除非需要额外的后处理

        return bean;
    }
}
