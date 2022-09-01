package com.createlt.cis.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.createlt.cis.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

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
public class SysJurisdiction extends BaseEntity implements Serializable, GrantedAuthority {

    private static final long serialVersionUID = 1L;

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
    private Boolean del;

    /**
     * 修改标识
     */
    private Boolean edit;


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
