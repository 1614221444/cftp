package com.createlt.business.model;

public interface BaseModel {

    /**
     * 出栈规则
     * @return
     */
    Object pop();

    /**
     * 入栈规则
     * @return
     */
    Object push(String id, byte[] data);
}
