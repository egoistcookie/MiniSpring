package org.springframework.aop.framework;

import java.lang.reflect.Method;

public class MethodInvocation {
    private final Object target;    // 目标对象实例
    private final Method method;    // 当前执行的方法
    private final Object[] args;    // 方法参数

    public MethodInvocation(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    // 新增 getMethod() 方法
    public Method getMethod() {
        return method;
    }

    // 其他必要方法
    public Object getTarget() {
        return target;
    }

    public Object[] getArgs() {
        return args;
    }

    // 执行目标方法
    public Object proceed() throws Throwable {
        return method.invoke(target, args);
    }
}