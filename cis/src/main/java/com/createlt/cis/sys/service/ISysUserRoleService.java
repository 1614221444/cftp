package com.createlt.cis.sys.service;

import com.createlt.cis.sys.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 用户角色关系表 服务类
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
public interface ISysUserRoleService extends IService<SysUserRole> {


    @Transactional
    void save(String userId, List<SysUserRole> roleList);
}
