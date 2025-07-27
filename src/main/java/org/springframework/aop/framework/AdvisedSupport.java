package org.springframework.aop.framework;

/**
 * AdvisedSupport 类是 Spring AOP 框架中用于存储和管理 AOP 配置信息的核心支持类。
 *
 * 该类保存了创建 AOP 代理所需的所有配置信息，包括目标对象、目标类以及方法拦截器。
 * 它作为 ProxyFactory 和 AopProxy 实现之间的数据载体，提供统一的配置访问接口。
 *
 * 该类通常不直接由用户使用，而是作为框架内部实现的一部分。
 */
public class AdvisedSupport {

    /**
     * 目标对象实例，即需要被代理的实际对象
     * 这是将被增强(advised)的对象
     */
    private Object target;

    /**
     * 目标对象的类型信息
     * 保存目标对象的 Class 对象，用于方法匹配和代理创建
     */
    private Class<?> targetClass;

    /**
     * 方法拦截器，用于拦截目标对象方法的执行
     * 这是 AOP 通知(Advice)的具体实现，定义了在目标方法执行前后需要执行的逻辑
     */
    private MethodInterceptor methodInterceptor;

    /**
     * 获取目标对象实例
     *
     * @return 目标对象实例，如果未设置则返回 null
     */
    public Object getTarget() {
        return target;
    }

    /**
     * 设置目标对象实例
     *
     * 当设置目标对象时，会自动获取并设置目标对象的 Class 类型，
     * 确保 target 和 targetClass 的一致性
     *
     * @param target 需要被代理的目标对象实例，不能为 null
     */
    public void setTarget(Object target) {
        this.target = target;
        this.targetClass = target.getClass(); // 设置目标对象时自动获取Class
    }

    /**
     * 获取目标对象的类型信息
     *
     * @return 目标对象的 Class 对象，如果未设置目标对象则可能为 null
     */
    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * 直接设置目标对象的类型信息
     *
     * 通常在无法直接访问目标对象实例但知道其类型时使用
     *
     * @param targetClass 目标对象的 Class 对象
     */
    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * 获取方法拦截器
     *
     * @return 方法拦截器实例，用于拦截目标方法的执行
     */
    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    /**
     * 设置方法拦截器
     *
     * 方法拦截器定义了在目标方法执行前后需要执行的增强逻辑
     *
     * @param methodInterceptor 方法拦截器实例
     */
    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }
}

