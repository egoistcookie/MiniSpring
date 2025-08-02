package org.springframework.web.servlet;

// JSP 视图解析器
public class InternalResourceViewResolver implements ViewResolver {
    private final String prefix;
    private final String suffix;

    public InternalResourceViewResolver(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public View resolveView(String viewName) {
        return (req, resp) -> req.getRequestDispatcher(prefix + viewName + suffix).forward(req, resp);
    }
}
