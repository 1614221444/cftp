package com.createlt.cis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.createlt.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 数据日志
 * </p>
 *
 * @author wuyh
 * @since 2022-10-11
 */
@Getter
@Setter
@TableName("CIS_SEND_LOG")
public class CisSendLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件主键
     */
    private String fileId;

    /**
     * 文件大小
     */
    private Double fileSize;

    /**
     * 发送方
     */
    private String sendId;

    /**
     * 接收方
     */
    private String receiverId;

    /**
     * 进度
     */
    private Integer progress;


}
