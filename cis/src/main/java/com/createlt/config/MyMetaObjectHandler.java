package com.createlt.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.createlt.sys.entity.SysUser;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 添加钩子
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime time = LocalDateTime.now();
        String userId = getThisUser();
        this.setFieldValByName("createBy", userId,metaObject);
        this.setFieldValByName("updateBy",userId,metaObject);
        this.setFieldValByName("createTime",time,metaObject);
        this.setFieldValByName("updateTime",time,metaObject);
    }

    /**
     * 修改钩子
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        String userId = getThisUser();
        this.setFieldValByName("updateTime",LocalDateTime.now(),metaObject);
        this.setFieldValByName("updateBy",userId,metaObject);
    }

    /**
     * 获取当前用户
     * @return 当前用户ID
     */
    private String getThisUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null) {
            return "null";
        }
        SysUser user = (SysUser) auth.getPrincipal();
        return user.getUserName();
    }
}
