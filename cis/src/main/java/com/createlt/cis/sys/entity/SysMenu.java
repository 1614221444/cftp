package com.createlt.cis.sys.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Getter
@Setter
@TableName("SYS_MENU")
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;

    /**
     * 父主键
     */
    private String pid;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单基础路径
     */
    private String baseUrl;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 页面路径
     */
    private String component;

    /**
     * 是否显示
     */
    private String showMenu;

    /**
     * 删除标识
     */
    private String delState;

    /**
     * 新增人
     */
    private String createBy;

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
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
