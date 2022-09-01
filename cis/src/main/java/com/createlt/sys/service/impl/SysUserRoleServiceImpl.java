package com.createlt.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.createlt.sys.entity.SysUserRole;
import com.createlt.sys.mapper.SysUserRoleMapper;
import com.createlt.sys.service.ISysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 替换保存
     * @param roleList 要替换的角色
     * @return
     */
    @Override
    @Transactional
    public void save(String userId, List<SysUserRole> roleList) {
        this.remove(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));
        this.saveBatch(roleList, 1000);
    }

}
