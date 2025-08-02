package org.springframework.web.servlet;

import com.example.controller.LoginController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotationHandlerMapping implements HandlerMapping {
    private final Map<String, ControllerMethod> urlMappings = new HashMap<>();

    public AnnotationHandlerMapping(String... basePackages) {
        // 模拟扫描包下的 @Controller 类（实际需用反射工具扫描）
        scanControllers(basePackages);
    }

    private void scanControllers(String[] basePackages) {
        // 示例：手动注册一个控制器（实际需动态扫描）
        Class<?> clazz = LoginController.class;

        // 修复：明确传入 Controller.class 的注解类型
        if (clazz.isAnnotationPresent(Controller.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                // 修复：明确传入 RequestMapping.class 的注解类型
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                    String url = mapping.value();
                    urlMappings.put(url, new ControllerMethod(clazz, method));
                }
            }
        }
    }

    @Override
    public Object getHandler(HttpServletRequest request) {
        String url = request.getRequestURI();
        ControllerMethod controllerMethod = urlMappings.get(url);
        if (controllerMethod != null) {
            try {
                // 创建控制器实例并关联方法
                Object controller = controllerMethod.getControllerClass().newInstance();
                return new HandlerExecutionChain(controller, controllerMethod.getMethod());
            } catch (Exception e) {
                throw new RuntimeException("Failed to create controller", e);
            }
        }
        return null;
    }

    // 封装控制器类和方法
    private static class ControllerMethod {
        private final Class<?> controllerClass;
        private final Method method;

        public ControllerMethod(Class<?> controllerClass, Method method) {
            this.controllerClass = controllerClass;
            this.method = method;
        }

        public Class<?> getControllerClass() {
            return controllerClass;
        }

        public Method getMethod() {
            return method;
        }
    }
}