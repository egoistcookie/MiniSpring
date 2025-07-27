package org.springframework.aop.framework;

public class AdvisedSupport {
    private Object target;                // 目标对象实例
    private Class<?> targetClass;         // 目标对象类型
    private MethodInterceptor methodInterceptor; // 方法拦截器

    // Getter 和 Setter
    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
        this.targetClass = target.getClass(); // 设置目标对象时自动获取Class
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }
}
