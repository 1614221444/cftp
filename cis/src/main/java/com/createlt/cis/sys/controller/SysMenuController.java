package com.createlt.cis.sys.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.createlt.cis.common.BaseController;
import com.createlt.cis.sys.entity.SysMenu;
import com.createlt.cis.sys.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController {

    private ISysMenuService menuService;

    @Autowired
    public SysMenuController(ISysMenuService menuService) {
        this.menuService = menuService;
    }
    @RequestMapping(value = "list")
    public String list() {
        List<SysMenu> list = menuService.list();
        return getJson(list);
    }


    @RequestMapping(value = "save")
    public String save(SysMenu menu) {
        if(StringUtils.isEmpty(menu.getId())) {
            menuService.save(menu);
        } else {
            menuService.updateById(menu);
        }
        return responseSuccess();
    }


    /**
     * 删除菜单
     * @param sysMenu 菜单
     * @return 删除结果
     */
    @ResponseBody
    @RequestMapping(value = "delete")
    public String delete(SysMenu sysMenu) {
        menuService.removeById(sysMenu.getId());
        return responseSuccess();
    }
}
