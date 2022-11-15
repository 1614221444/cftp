package com.createlt.business.view;

public interface BaseView {
    /**
     * 获取数据源
     * @return
     */
    byte[] getData();

    /**
     * 放入数据源
     * @return
     */
    void setData(String id, Object data);
}
