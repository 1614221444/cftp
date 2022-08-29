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
 * 任务日志表
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Getter
@Setter
@TableName("SYS_JOB_LOG")
public class SysJobLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 模块名
     */
    private String jobId;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 任务返回文本
     */
    private String text;

    /**
     * 开始时间
     */
    private LocalDateTime startDate;

    /**
     * 共用时
     */
    private Float timeUse;

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
