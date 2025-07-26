package org.springframework.beans.factory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动装配注解
 *
 * 这是Spring框架中最常用的依赖注入注解之一，用于标记需要自动注入的字段、构造函数或方法。
 * 当Spring容器创建Bean时，会自动查找符合要求的Bean并注入到标记了此注解的元素中。
 *
 * 主要特点：
 * 1. 支持字段注入：可以直接标注在类的字段上
 * 2. 支持构造函数注入：可以标注在构造函数上
 * 3. 支持方法注入：可以标注在setter方法或其他方法上
 * 4. 类型匹配：基于类型进行依赖查找和注入
 * 5. 运行时有效：注解在运行时可通过反射获取
 *
 * 使用场景示例：
 * <pre>
 * // 1. 字段注入示例
 * public class UserService {
 *     {@code @Autowired}
 *     private UserRepository userRepository; // 自动注入UserRepository类型的Bean
 *
 *     {@code @Autowired}
 *     private RoleService roleService; // 自动注入RoleService类型的Bean
 * }
 *
 * // 2. 构造函数注入示例
 * public class OrderService {
 *     private final PaymentService paymentService;
 *
 *     {@code @Autowired}
 *     public OrderService(PaymentService paymentService) {
 *         this.paymentService = paymentService;
 *     }
 * }
 *
 * // 3. 方法注入示例
 * public class NotificationService {
 *     private EmailService emailService;
 *
 *     {@code @Autowired}
 *     public void setEmailService(EmailService emailService) {
 *         this.emailService = emailService;
 *     }
 * }
 * </pre>
 *
 * 注意事项：
 * 1. 默认情况下，标记@Autowired的依赖是必需的，如果找不到匹配的Bean会抛出异常
 * 2. 可以通过required属性设置为false来标记为可选依赖
 * 3. 当有多个相同类型的Bean时，需要配合@Qualifier注解指定具体Bean
 * 4. 推荐优先使用构造函数注入，因为它能确保依赖不可变且非空
 *
 * 工作原理：
 * 1. Spring容器在创建Bean过程中会扫描类中的@Autowired注解
 * 2. 根据注解标记的元素类型（字段、构造函数、方法）采取不同的注入策略
 * 3. 通过类型匹配在BeanFactory中查找合适的Bean
 * 4. 使用反射机制将找到的Bean注入到相应位置
 *
 * @author Spring Framework
 * @see AutowiredAnnotationBeanPostProcessor 处理此注解的实际处理器
 * @see org.springframework.beans.factory.annotation.Qualifier 用于指定具体Bean的注解
 * @since 2.5
 */
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

    /**
     * 声明此依赖是否为必需项
     *
     * 默认值为true，表示此依赖是必需的。如果设置为false，则当容器中没有匹配的Bean时，
     * 不会抛出异常，而是保持该字段为null或不调用该方法。
     *
     * 使用示例：
     * <pre>
     * public class MyService {
     *     {@code @Autowired(required = false)}
     *     private OptionalService optionalService; // 可选依赖
     * }
     * </pre>
     *
     * @return true表示依赖是必需的，false表示是可选的
     */
    boolean required() default true;
}
