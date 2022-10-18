package com.createlt.cis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.createlt.cis.entity.CisSendLog;
import com.createlt.cis.service.ICisSendLogService;
import com.createlt.common.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 数据日志 前端控制器
 * </p>
 *
 * @author wuyh
 * @since 2022-10-11
 */
@RestController
@RequestMapping("/cis/sendLog")
public class CisSendLogController extends BaseController {

    private ICisSendLogService cisSendLogService;


    @Autowired
    public  CisSendLogController (ICisSendLogService cisSendLogService) {
        this.cisSendLogService = cisSendLogService;
    }
    /**
     * 分页查询列表
     *
     * @param log 查询条件
     * @param page 分页
     * @return
     */
    @RequestMapping(value = "list")
    public String list(CisSendLog log, Page<CisSendLog> page) {
        page = cisSendLogService.page(page, new LambdaQueryWrapper<CisSendLog>()
                .like(!StringUtils.isEmpty(log.getFileId()), CisSendLog::getFileId, "%" + log.getFileId() + "%")
                .orderBy(true,false, CisSendLog::getCreateTime));
        return getJson(page);
    }
}
