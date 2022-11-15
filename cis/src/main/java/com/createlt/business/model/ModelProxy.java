package com.createlt.business.model;

import com.createlt.cis.entity.CisSendLog;
import com.createlt.cis.service.ICisSendLogService;
import com.createlt.common.ToolSpring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ModelProxy implements InvocationHandler {
    private Object targetObject;
    private ICisSendLogService logService;

    public ModelProxy(Object targetObject) {
        this.targetObject = targetObject;
        logService = (ICisSendLogService) ToolSpring.getBean("cisSendLogServiceImpl");

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("业务层开始");
        // 取日志ID更新消息状态
        CisSendLog log = logService.getById(args[0].toString());
        // 数据封装开始
        log.setProgress(1);
        logService.save(log);
        Object invoke = method.invoke(targetObject, args);
        // 数据封装完成
        log.setProgress(2);
        logService.save(log);
        System.out.println("业务层结束");
        return invoke;
    }
}
