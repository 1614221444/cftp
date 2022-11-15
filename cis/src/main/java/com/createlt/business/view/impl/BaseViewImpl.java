package com.createlt.business.view.impl;

import com.createlt.business.view.BaseView;

public class BaseViewImpl implements BaseView {

    @Override
    public byte[] getData() {
        return null;
    }

    @Override
    public void setData(String id, Object data) {
        System.out.println(data);
    }
}
