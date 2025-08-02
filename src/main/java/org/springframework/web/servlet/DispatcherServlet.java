package org.springframework.web.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 前端控制器 中央调度器，负责整个请求处理流程的协调
 */
public class DispatcherServlet extends HttpServlet {
    private HandlerMapping handlerMapping;
    private ViewResolver viewResolver;

    @Override
    public void init() {
        this.handlerMapping = new AnnotationHandlerMapping("com.example.controller");
        this.viewResolver = new InternalResourceViewResolver("/WEB-INF/views/", ".jsp");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // 1. 获取处理器链
            HandlerExecutionChain chain = (HandlerExecutionChain) handlerMapping.getHandler(req);
            if (chain == null) {
                resp.sendError(404);
                return;
            }

            // 2. 获取控制器方法及其参数类型
            Method method = chain.getHandlerMethod();
            Class<?>[] paramTypes = method.getParameterTypes();
            Object[] args = new Object[paramTypes.length];

            // 3. 根据参数类型填充参数值
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] == HttpServletRequest.class) {
                    args[i] = req;
                } else if (paramTypes[i] == HttpServletResponse.class) {
                    args[i] = resp;
                }
                // 可扩展支持其他类型（如 @RequestParam）
            }

            // 4. 反射调用方法
            Object controller = chain.getController();
            String viewName = (String) method.invoke(controller, args); // 传入正确参数

            // 5. 渲染视图
            viewResolver.resolveView(viewName).render(req, resp);
        } catch (Exception e) {
            throw new RuntimeException("MVC processing failed", e);
        }
    }
}