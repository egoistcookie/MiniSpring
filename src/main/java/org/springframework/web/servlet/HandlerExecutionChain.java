package org.springframework.web.servlet;

import java.lang.reflect.Method;

public class HandlerExecutionChain {
    private final Object controller;
    private final Method handlerMethod;

    public HandlerExecutionChain(Object controller, Method handlerMethod) {
        this.controller = controller;
        this.handlerMethod = handlerMethod;
    }

    public Object getController() {
        return controller;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }
}