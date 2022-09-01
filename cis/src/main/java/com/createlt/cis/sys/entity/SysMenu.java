package com.createlt.cis.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.createlt.cis.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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
public class SysMenu extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private Boolean showMenu;

}
