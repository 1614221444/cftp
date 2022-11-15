package com.createlt.business.view;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ViewProxy implements InvocationHandler {
    private Object targetObject;

    public ViewProxy(Object targetObject) {
        this.targetObject = targetObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("持久层开始");
        Object invoke = method.invoke(targetObject, args);
        System.out.println("持久层结束");
        return invoke;
    }
}
