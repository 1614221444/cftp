package com.createlt.cis.sys.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 角色权限关系表
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Getter
@Setter
@TableName("SYS_JURISDICTION")
public class SysJurisdiction implements Serializable, GrantedAuthority {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 角色主键
     */
    private String roleId;

    /**
     * 菜单主键
     */
    private String menuId;

    /**
     * 删除标识
     */
    private Integer del;

    /**
     * 修改标识
     */
    private Integer edit;

    /**
     * 删除标记
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


    /**
     * 菜单对象
     */
    @TableField(exist = false)
    private SysMenu menu;

    @Override
    public String getAuthority() {
        return null;
    }
}
