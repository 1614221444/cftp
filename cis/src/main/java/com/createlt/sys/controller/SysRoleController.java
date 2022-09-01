package com.createlt.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.createlt.common.BaseController;
import com.createlt.sys.entity.SysJurisdiction;
import com.createlt.sys.entity.SysRole;
import com.createlt.sys.service.ISysJurisdictionService;
import com.createlt.sys.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {


    private ISysRoleService roleService;
    private ISysJurisdictionService jurisdictionService;

    @Autowired
    public SysRoleController(ISysRoleService roleService,ISysJurisdictionService jurisdictionService) {
        this.roleService = roleService;
        this.jurisdictionService = jurisdictionService;
    }


    @RequestMapping(value = "noPageList")
    public String noPageList(SysRole sysRole) {
        List<SysRole> list = roleService.list();
        return getJson(list);
    }
    @RequestMapping(value = "list")
    public String list(SysRole sysRole, Page<SysRole> page) {
        page = roleService.page(page, new LambdaQueryWrapper<SysRole>().
                like(!StringUtils.isEmpty(sysRole.getRoleName()), SysRole::getRoleName, "%" + sysRole.getRoleName() + "%"));
        return getJson(page);
    }

    /**
     * 保存角色
     * @param role 角色
     * @return 保存结果
     */
    @RequestMapping(value = "save")
    public String save(SysRole role) {
        if(StringUtils.isEmpty(role.getId())) {
            roleService.save(role);
        } else {
            roleService.updateById(role);
        }
        jurisdictionService.save(role.getId(),role.getJurisdictionList());
        return responseSuccess();
    }


    /**
     * 删除角色
     * @param role 角色
     * @return 删除结果
     */
    @RequestMapping(value = "delete")
    public String delete(SysRole role) {
        roleService.removeById(role.getId());
        return responseSuccess();
    }
    /**
     * 查询角色权限
     * @param roleId 角色
     * @return 权限列表
     */
    @RequestMapping(value = "getJurisdictionList")
    public String getJurisdictionList(String roleId) {
        List<SysJurisdiction> jurisdictionList = jurisdictionService.list(new LambdaQueryWrapper<SysJurisdiction>()
                .eq(SysJurisdiction::getRoleId, roleId));
        return getJson(jurisdictionList);
    }
}
