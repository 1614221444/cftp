package com.createlt.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.createlt.common.BaseController;
import com.createlt.sys.entity.SysNotice;
import com.createlt.sys.entity.SysNoticeRole;
import com.createlt.sys.service.ISysNoticeRoleService;
import com.createlt.sys.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 通知模板表 前端控制器
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@RestController
@RequestMapping("/sys/notice")
public class SysNoticeController extends BaseController {

    private ISysNoticeService sysNoticeService;
    private ISysNoticeRoleService sysNoticeRoleService;
    @Autowired
    public SysNoticeController(ISysNoticeService sysNoticeService, ISysNoticeRoleService sysNoticeRoleService) {
        this.sysNoticeRoleService = sysNoticeRoleService;
        this.sysNoticeService = sysNoticeService;
    }

    /**
     * 分页查询列表
     * @param notice 通知对象
     * @param page 分页参数
     * @return 通知列表
     */
    @RequestMapping(value = "list")
    public String list(SysNotice notice, Page<SysNotice> page) {
        page = sysNoticeService.page(page,new LambdaQueryWrapper<SysNotice>()
                .like(!StringUtils.isEmpty(notice.getCode()), SysNotice::getCode,"%" + notice.getCode() +"%"));
        return getJson(page);
    }

    /**
     * 保存通知模板
     * @param notice 通知对象
     * @return 保存结果
     */
    @RequestMapping(value = "save")
    public String save(SysNotice notice) {
        if(StringUtils.isEmpty(notice.getId())) {
            sysNoticeService.save(notice);
        } else {
            sysNoticeService.updateById(notice);
        }
        if(notice.getRoleList() != null && notice.getRoleList().size() != 0) {
            notice.getRoleList().forEach((role) -> role.setNoticeId(notice.getId()));
            sysNoticeRoleService.save(notice.getId(),notice.getRoleList());
        }
        return responseSuccess();
    }

    /**
     * 删除通知模板
     * @param notice 通知对象
     * @return 删除结果
     */
    @RequestMapping(value = "delete")
    public String delete(SysNotice notice) {
        sysNoticeService.removeById(notice.getId());
        return responseSuccess();
    }


    @RequestMapping(value = "getRoleList")
    public String getRoleList(String noticeId) {
        List<SysNoticeRole> roleList = sysNoticeRoleService.list(new LambdaQueryWrapper<SysNoticeRole>()
                .eq(SysNoticeRole::getNoticeId,noticeId));
        return getJson(roleList);
    }


    /**
     * 推送通知
     * @param notice 通知对象
     * @return 推送结果
     */
    @RequestMapping(value = "push")
    public String push(SysNotice notice) {
        //WebSocketServer.sendInfo("message","1");
        //sysNoticeService.push(notice);
        return responseSuccess();
    }

    /*    *//**
     * 推送通知
     * @return 推送列表
     *//*
    @RequestMapping(value = "news")
    public String news() {
        // 获取当前登录用户
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        return getJson(sysNoticeService.pop(user));
    }

    *//**
     * 推送已读
     * @param push 推送对象
     *//*
    @RequestMapping(value = "read")
    public String read(SysPush push) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        sysNoticeService.read(user, push);
        return responseSuccess();
    }

    *//**
     * 推送已读
     *//*
    @RequestMapping(value = "readAll")
    public String readAll() {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        sysNoticeService.readAll(user);
        return responseSuccess();
    }

    *//**
     * 推送删除
     *//*
    @RequestMapping(value = "delPush")
    public String delPush(SysPush push) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        sysNoticeService.delPush(user, push);
        return responseSuccess();
    }*/
}
