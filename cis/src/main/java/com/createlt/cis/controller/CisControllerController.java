package com.createlt.cis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.createlt.cis.entity.CisController;
import com.createlt.cis.service.ICisAuthenticationService;
import com.createlt.cis.service.ICisControllerService;
import com.createlt.common.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 控制层 前端控制器
 * </p>
 *
 * @author wuyh
 * @since 2022-09-01
 */
@RestController
@RequestMapping("/cis/controller")
public class CisControllerController extends BaseController {

    private ICisControllerService sisControllerService;
    private ICisAuthenticationService sisAuthenticationService;

    @Autowired
    public CisControllerController (ICisControllerService sisControllerService,ICisAuthenticationService sisAuthenticationService) {
        this.sisControllerService = sisControllerService;
        this.sisAuthenticationService = sisAuthenticationService;
    }

    /**
     * 分页查询列表
     *
     * @param controller 控制器实体
     * @param page 分页
     * @return
     */
    @RequestMapping(value = "list")
    public String list(CisController controller, Page<CisController> page) {
        page = sisControllerService.page(page, new LambdaQueryWrapper<CisController>()
                .eq(!StringUtils.isEmpty(controller.getControllerName()), CisController::getControllerName, controller.getControllerName()));
        return getJson(page);
    }


    /**
     * 保存控制器
     *
     * @param controller 控制器
     * @return 保存结果
     */
    @RequestMapping(value = "save")
    public String save(CisController controller) {
        if(StringUtils.isEmpty(controller.getId())) {
            controller.setIsStart(false);
            sisControllerService.save(controller);
        } else {
            sisControllerService.updateById(controller);
        }
        return responseSuccess();
    }

    /**
     * 删除控制器
     *
     * @param controller 控制器
     * @return 删除结果
     */
    @RequestMapping(value = "delete")
    public String delete(CisController controller) {
        sisControllerService.removeById(controller.getId());
        return responseSuccess();
    }


    /**
     * 启动服务
     *
     * @param id 控制器ID
     * @return 保存结果
     */
    @RequestMapping(value = "start")
    public String start(String id) {
        CisController controller = sisControllerService.getById(id);
        controller.setIsStart(true);
        sisControllerService.updateById(controller);
        return responseSuccess();
    }

    /**
     * 停止服务
     *
     * @param id 控制器ID
     * @return 保存结果
     */
    @RequestMapping(value = "stop")
    public String stop(String id) {
        CisController controller = sisControllerService.getById(id);
        controller.setIsStart(false);
        sisControllerService.updateById(controller);
        return responseSuccess();
    }
}
