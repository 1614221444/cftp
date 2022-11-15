package com.createlt.business.model.impl;

import com.createlt.business.model.BaseModel;

public class BaseModelImpl implements BaseModel {
    @Override
    public Object pop() {
        return null;
    }

    @Override
    public Object push(String id, byte[] data) {

        System.out.println("---");
        return new String(data);
    }
}
