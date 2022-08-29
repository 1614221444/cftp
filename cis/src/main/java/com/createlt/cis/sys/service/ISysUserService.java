package com.createlt.cis.sys.service;

import com.createlt.cis.sys.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
public interface ISysUserService extends IService<SysUser> {
    UserDetails loadUserByUsername(String username);
}
