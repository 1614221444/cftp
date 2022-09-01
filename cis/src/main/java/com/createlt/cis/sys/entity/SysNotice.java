package com.createlt.cis.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.createlt.cis.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

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
public class SysNotice extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @TableField(exist = false)
    private List<SysNoticeRole> roleList;

}
