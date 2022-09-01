package com.createlt.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.createlt.common.BaseController;
import com.createlt.sys.entity.SysJobLog;
import com.createlt.sys.entity.SysScheduleJob;
import com.createlt.sys.service.ISysJobLogService;
import com.createlt.sys.service.ISysScheduleJobService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 定时任务 前端控制器
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@RestController
@RequestMapping("/sys/job")
public class SysScheduleJobController extends BaseController {

    private ISysScheduleJobService sysScheduleJobService;

    private ISysJobLogService sysJobLogService;

    public SysScheduleJobController (ISysScheduleJobService scheduleJobService,ISysJobLogService sysJobLogService) {
        this.sysScheduleJobService = scheduleJobService;
        this.sysJobLogService = sysJobLogService;
    }
    /**
     * 分页查询列表
     * @param job 任务
     * @param page 分页
     */
    @RequestMapping(value = "list")
    public String list(SysScheduleJob job, Page<SysScheduleJob> page) {
        page = sysScheduleJobService.page(page, new LambdaQueryWrapper<SysScheduleJob>()
                .like(!StringUtils.isEmpty(job.getJobName()), SysScheduleJob::getJobName,"%" + job.getJobName() + "%"));
        return getJson(page);
    }


    /**
     * 不分页查询列表
     * @param job 任务检索条件
     * @return 任务列表
     */
    @RequestMapping(value = "noPageList")
    public String list(SysScheduleJob job) {
        List<SysScheduleJob> list = sysScheduleJobService.list();
        return getJson(list);
    }

    /**
     * 保存任务
     * @param job 任务
     * @return 保存结果
     */
    @RequestMapping(value = "save")
    public String save(SysScheduleJob job) {
        if(StringUtils.isEmpty(job.getId())) {
            sysScheduleJobService.save(job);
        } else {
            sysScheduleJobService.updateById(job);
        }
        return responseSuccess();
    }

    /**
     * 删除任务
     * @param job 任务
     * @return 删除结果
     */
    @RequestMapping(value = "delete")
    public String delete(SysScheduleJob job) {
        sysScheduleJobService.removeById(job.getId());
        return responseSuccess();
    }


    /**
     * 查询log列表
     * @param jobId 任务
     * @param page 分页条件
     * @return 查询结果
     */
    @RequestMapping(value = "logList")
    public String logList(String jobId, Page<SysJobLog> page) {
        return getJson(sysJobLogService.page(page, new LambdaQueryWrapper<SysJobLog>()
                .eq(SysJobLog::getJobId, jobId)));
    }
}
