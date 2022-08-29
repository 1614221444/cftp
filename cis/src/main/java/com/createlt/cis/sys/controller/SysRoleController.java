package com.createlt.cis.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.createlt.cis.common.BaseController;
import com.createlt.cis.sys.entity.SysRole;
import com.createlt.cis.sys.service.ISysRoleService;
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

    @Autowired
    public SysRoleController(ISysRoleService roleService) {
        this.roleService = roleService;
    }


    @RequestMapping(value = "noPageList")
    public String noPageList(SysRole sysRole) {
        List<SysRole> list = roleService.list(new LambdaQueryWrapper<SysRole>());
        return getJson(list);
    }
    @RequestMapping(value = "list")
    public String list(SysRole sysRole, Page<SysRole> page) {
        page = roleService.page(page, new LambdaQueryWrapper<SysRole>());
        return getJson(page);
    }
}
