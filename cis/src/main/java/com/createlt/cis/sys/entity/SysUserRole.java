package com.createlt.cis.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户角色关系表
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Getter
@Setter
@TableName("SYS_USER_ROLE")
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色主键
     */
    private String roleId;

    /**
     * 用户主键
     */
    private String userId;


}
