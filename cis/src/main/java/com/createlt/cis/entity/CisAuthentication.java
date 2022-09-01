package com.createlt.cis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 服务认证
 * </p>
 *
 * @author wuyh
 * @since 2022-09-01
 */
@Getter
@Setter
@TableName("CIS_AUTHENTICATION")
public class CisAuthentication implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 控制层ID
     */
    private String controllerId;

    /**
     * 认证类型：0=用户名/密码;1=证书/密码
     */
    private String joinType;

    /**
     * 用户名/证书URL
     */
    private String loginName;

    /**
     * 密码
     */
    private String password;


}
