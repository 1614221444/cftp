package com.createlt.cis.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.createlt.cis.common.BaseController;
import com.createlt.cis.common.DESUtil;
import com.createlt.cis.sys.entity.SysUser;
import com.createlt.cis.sys.entity.SysUserRole;
import com.createlt.cis.sys.service.ISysUserRoleService;
import com.createlt.cis.sys.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController  extends BaseController {

    private ISysUserService sysUserService;
    private ISysUserRoleService userRoleService;

    @Autowired
    public SysUserController(ISysUserService sysUserService,ISysUserRoleService userRoleService) {
        this.sysUserService = sysUserService;
        this.userRoleService = userRoleService;
    }

    /*@GetMapping("/save")
    @ResponseBody
    public String save(){
        SysUser user =new SysUser();
        user.setName("张某某");
        sysUserService.save(user);
        return "";
    }

    @GetMapping("/update")
    @ResponseBody
    public String update(String id){
        SysUser user =new SysUser();
        user.setId(id);
        user.setName("修改的名字");

        sysUserService.updateById(user);

        return "";
    }

    @GetMapping("/list")
    @ResponseBody
    public String list(){
        // 返回所有
        List<SysUser> list = sysUserService.list();
        return JSONUtils.toJSONString(list);
    }

    *//**
     * 条件查询， 通过QueryWrapper来实现查询的条件：
     * eq: 代表相等
     * like: 模糊匹配
     * orderBy: 排序
     * in, notin
     * 大于，小于，between等
     *//*
    @GetMapping("/listByContion")
    @ResponseBody
    public String listByContion(){
        List<SysUser> list = sysUserService.list(new LambdaQueryWrapper<SysUser>()
                // 模糊匹配
                .like(SysUser::getUsername, "admin%")
                // 排序，按照创建时间
                .orderByDesc(SysUser::getCreateTime)
        );
        return JSONUtils.toJSONString(list);
    }

    @GetMapping("/getById")
    @ResponseBody
    public String getById(String id){
        SysUser user = sysUserService.getById(id);
        return JSONUtils.toJSONString(user);
    }

    @GetMapping("/delete")
    @ResponseBody
    public String delete(Integer id){
        sysUserService.removeById(id);
        return "";
    }

    @GetMapping("/page")
    @ResponseBody
    public String page(Page<SysUser> pages,String name){

        IPage<SysUser> page1 = sysUserService.page(pages, new LambdaQueryWrapper<SysUser>()
                // 主要演示这里可以加条件。在name不为空的时候执行
                .like(StringUtils.isNotEmpty(name), SysUser::getUsername, name + "%"));

        return JSONUtils.toJSONString(page1);
    }*/



    @RequestMapping(value = "list")
    public String list(SysUser user, Page<SysUser> page) {
        page = sysUserService.page(page, new LambdaQueryWrapper<SysUser>()
                .eq(!StringUtils.isEmpty(user.getUsername()), SysUser::getUsername,user.getUsername())
                .eq(!StringUtils.isEmpty(user.getName()), SysUser::getName,user.getName())
                .orderByDesc(SysUser::getName)
                .orderByDesc(SysUser::getEnterpriseId));

        return getJson(page);
    }

    @RequestMapping(value = "get")
    public String get(String id) {
        return getJson(sysUserService.getById(id));
    }

    /**
     * 保存用户
     * @param sysUser 用户信息
     * @return 保存结果
     */
    @RequestMapping(value = "save")
    public String save(SysUser sysUser) {
        if(null == sysUser.getId() || "".equals(sysUser.getId())){
            SysUser user = null;
            try {
                user = (SysUser) sysUserService.loadUserByUsername(sysUser.getUsername());
            } catch (UsernameNotFoundException e) {
            }
            //判断用户是否存在
            if(user != null){
                return responseFail("用户已存在");
            }
            //密码加密
            sysUser.setPassword(DESUtil.encrypt(sysUser.getPassword(),DESUtil.KEY));
        }
        if(StringUtils.isEmpty(sysUser.getId())) {
            sysUserService.save(sysUser);
        } else {
            sysUserService.updateById(sysUser);
        }
        // 封装角色关系表
        List<SysUserRole> roleList = new ArrayList<>();
        if(sysUser.getRoleList() != null) {
            for (String id : sysUser.getRoleList()) {
                SysUserRole  role = new SysUserRole();
                role.setUserId(sysUser.getId());
                role.setRoleId(id);
                roleList.add(role);
            }
        }
        userRoleService.save(sysUser.getId(), roleList);
        return responseSuccess();
    }

    /**
     * 删除用户
     * @param sysUser 删除条件
     * @return 删除结果
     */
    @RequestMapping(value = "delete")
    public String delete(SysUser sysUser) {
        sysUserService.removeById(sysUser.getId());
        userRoleService.save(sysUser.getId(), new ArrayList<>());
        return responseSuccess();
    }

    /**
     * 修改密码
     * @param originalPassword 旧密码
     * @param newPassword 新密码
     * @return 保存结果
     */
    @RequestMapping(value = "updatePassword")
    public String updatePassword(String originalPassword, String newPassword) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        // 判断当前用户的密码是否等于原密码 如果等于就改密码 否则不作操作
        if(user.getPassword().equals(DESUtil.encrypt(originalPassword,DESUtil.KEY))){
            // 为新密码加密
            user.setPassword(DESUtil.encrypt(newPassword,DESUtil.KEY));
            sysUserService.save(user);
            return responseSuccess();
        } else {
            return responseFail("原密码错误！");
        }
    }


    /**
     * 获取当前登录用户
     * @return 用户
     */
    @ResponseBody
    @RequestMapping(value = "getThisUser")
    public String getThisUser() {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        return getJson(user);
    }

    /**
     * 获取当前登录用户
     * @return 用户
     */
    @ResponseBody
    @RequestMapping(value = "getRoleList")
    public String getRoleList(String userId) {
        List<SysUserRole> userRole = userRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId,userId));
        return getJson(userRole);
    }
}
