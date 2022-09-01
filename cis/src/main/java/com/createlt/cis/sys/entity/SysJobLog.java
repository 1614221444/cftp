package com.createlt.cis.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.createlt.cis.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class SysJobLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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


}
