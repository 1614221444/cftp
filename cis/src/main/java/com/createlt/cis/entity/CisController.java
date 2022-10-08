package com.createlt.cis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.createlt.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 控制层
 * </p>
 *
 * @author wuyh
 * @since 2022-09-01
 */
@Getter
@Setter
@TableName("CIS_CONTROLLER")
public class CisController extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 启动类型：0=自动;1=手动;
     */
    private String startType;

    /**
     * 控制器名称
     */
    private String controllerName;

    /**
     * 服务类型 1客户端;0=服务端;
     */
    private String serverType;

    /**
     * 地址
     */
    private String ip;

    /**
     * 端口
     */
    private String port;

    /**
     * 协议类型:0=CFTP;
     */
    private String agreementType;

    /**
     * 是否启动
     */
    private Boolean isStart;

    /**
     * 启动时间
     */
    private Date startTime;

    /**
     * 启动时间
     */
    @TableField(exist = false)
    private List<CisAuthentication> authList;

}
