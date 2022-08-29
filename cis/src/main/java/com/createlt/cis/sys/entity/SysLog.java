package com.createlt.cis.sys.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 日志表
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Getter
@Setter
@TableName("SYS_LOG")
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 模块名
     */
    private String module;

    /**
     * 标题
     */
    private String title;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 操作用户
     */
    private String userId;

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 方法名
     */
    private String declaredMethodName;

    /**
     * 参数json
     */
    private String pojo;

    /**
     * 开始时间
     */
    private LocalDateTime startDate;

    /**
     * 处理时间
     */
    private LocalDateTime handleDate;

    /**
     * 结束时间
     */
    private LocalDateTime endDate;

    /**
     * 共用时
     */
    private Float timeUse;

    /**
     * 异常文本
     */
    private String errorText;

    /**
     * 删除标记
     */
    private String delState;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 新增时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 新增人
     */
    private String createBy;


}
