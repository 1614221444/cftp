package com.createlt.cis.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.createlt.cis.sys.entity.SysJurisdiction;
import com.createlt.cis.sys.entity.SysMenu;
import com.createlt.cis.sys.entity.SysUser;
import com.createlt.cis.sys.entity.SysUserRole;
import com.createlt.cis.sys.mapper.SysUserMapper;
import com.createlt.cis.sys.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService, UserDetailsService {

    @Resource
    private ISysUserRoleService userRoleService;


    @Resource
    private ISysMenuService menuService;

    @Resource
    private ISysJurisdictionService jurisdictionService;

    /**
     * 登陆验证时，通过username获取用户的所有权限信息
     * 并返回UserDetails放到spring的全局缓存SecurityContextHolder中，以供授权器使用
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //去用户信息
        SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if(user == null){
            throw new  UsernameNotFoundException("用户读取失败");
        }
        // 初始化权限
        List<SysUserRole> roleIdList =  userRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, user.getId()));
        List<String> roleIds = new ArrayList<>();
        roleIdList.forEach((role) -> roleIds.add(role.getRoleId()));
        List<SysJurisdiction> jurisdictionList = jurisdictionService.list(new LambdaQueryWrapper<SysJurisdiction>()
                .in(SysJurisdiction::getRoleId, roleIds));
        user.setAuthorities(jurisdictionList);

        List<String> menuIds = new ArrayList<>();
        jurisdictionList.forEach(data -> menuIds.add(data.getMenuId()));
        List<SysMenu> menuList = menuService.list(new LambdaQueryWrapper<SysMenu>()
                .in(SysMenu::getId, menuIds));
        for(SysJurisdiction jurisdiction : jurisdictionList) {
            for(SysMenu menu : menuList) {
                if(jurisdiction.getMenuId().equals(menu.getId())) {
                    jurisdiction.setMenu(menu);
                }
            }
        }
        return user;
    }

}
