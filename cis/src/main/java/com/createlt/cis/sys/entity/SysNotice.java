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
 * 通知模板表
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Getter
@Setter
@TableName("SYS_NOTICE")
public class SysNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 推送编号(用于搜索)
     */
    private String code;

    /**
     * 标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 回调地址
     */
    private String callbackUrl;

    /**
     * 图标URL
     */
    private String icon;

    /**
     * 跳转参数
     */
    private String jsonData;

    /**
     * 备注
     */
    private String remakes;

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
