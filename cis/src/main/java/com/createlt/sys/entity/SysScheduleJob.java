package com.createlt.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.createlt.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 定时任务
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Getter
@Setter
@TableName("SYS_SCHEDULE_JOB")
public class SysScheduleJob extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * cron表达式
     */
    private String cronExpression;

    /**
     * 服务名称
     */
    private String beanName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 状态 0 启动 1 停止
     */
    private Integer status;


}
