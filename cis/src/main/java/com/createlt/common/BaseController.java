package com.createlt.common;


import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


/**
 * 控制层基类
 */
@ResponseBody
public class BaseController {
    /**
     * 将对象转换成json
     * @param obj data
     * @return json
     */
    public String getJson(Object obj) {
        return JSON.toJSONStringWithDateFormat(obj,"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 返回成功
     * @param message 自定义提示
     * @return
     */
    public String responseSuccess(String message) {
        Map<String,Object> map = new HashMap<>();
        map.put("code",200);
        map.put("message",message);
        return getJson(map);
    }


    /**
     * 返回成功
     * @return
     */
    public String responseSuccess() {
        return responseSuccess("操作成功");
    }

    /**
     * 返回失败
     * @param message 自定义提示
     * @return
     */
    public String responseFail(String message) {
        Map<String,Object> map = new HashMap<>();
        map.put("code",501);
        map.put("message",message);
        return getJson(map);
    }


    /**
     * 返回失败
     * @return
     */
    public String responseFail() {
        return responseFail("操作失败");
    }

}
