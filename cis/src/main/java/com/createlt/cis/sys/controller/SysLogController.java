package com.createlt.cis.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.createlt.cis.common.BaseController;
import com.createlt.cis.sys.entity.SysLog;
import com.createlt.cis.sys.service.ISysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 日志表 前端控制器
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@RestController
@RequestMapping("/sys/log")
public class SysLogController extends BaseController {

    private ISysLogService sysLogService;
    @Autowired
    public SysLogController(ISysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }
    /**
     * 查询log
     *
     * @param log  检索条件
     * @param page 分页条件
     * @return 分页列表
     */
    @RequestMapping(value = "list")
    public String list(SysLog log, Page<SysLog> page) {
        page = sysLogService.page(page, new LambdaQueryWrapper<SysLog>().
                like(!StringUtils.isEmpty(log.getTitle()), SysLog::getTitle, "%" + log.getTitle() + "%")
                .like(!StringUtils.isEmpty(log.getErrorText()), SysLog::getErrorText, "%" + log.getErrorText() + "%")
                .eq(log.getState() != null, SysLog::getState, "%" + log.getState() + "%"));
        return getJson(page);
    }

    /**
     * 重试请求
     *
     * @param log 日志信息
     * @return 重试请求结果
     */
    @RequestMapping(value = "retry")
    public String retry(SysLog log) {
        // TODO 这里可能重做
        //sysLogService.retry(log);
        return responseSuccess();
    }

}
