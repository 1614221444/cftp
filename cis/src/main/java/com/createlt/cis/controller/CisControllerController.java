package com.createlt.cis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.createlt.agreement.base.BaseClient;
import com.createlt.agreement.base.BaseServer;
import com.createlt.agreement.client.CftpClient;
import com.createlt.agreement.server.CftpServer;
import com.createlt.cis.entity.CisAuthentication;
import com.createlt.cis.entity.CisController;
import com.createlt.cis.service.ICisAuthenticationService;
import com.createlt.cis.service.ICisControllerService;
import com.createlt.common.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private ICisControllerService cisControllerService;
    private ICisAuthenticationService cisAuthenticationService;

    /**
     * 服务器池
     */
    public static Map<String, BaseServer> serverMap = new HashMap<>();

    /**
     * 客户端池
     */
    public static Map<String, BaseClient> clientMap = new HashMap<>();

    @Autowired
    public CisControllerController (ICisControllerService cisControllerService,ICisAuthenticationService cisAuthenticationService) {
        this.cisControllerService = cisControllerService;
        this.cisAuthenticationService = cisAuthenticationService;
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
        page = cisControllerService.page(page, new LambdaQueryWrapper<CisController>()
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
            cisControllerService.save(controller);
        } else {
            cisControllerService.updateById(controller);
        }
        for(CisAuthentication auth : controller.getAuthList()) {
            auth.setControllerId(controller.getId());
        }
        // 替换更新
        cisAuthenticationService.remove(new LambdaQueryWrapper<CisAuthentication>()
                .eq(CisAuthentication::getControllerId, controller.getId()));
        cisAuthenticationService.saveBatch(controller.getAuthList());
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
        cisControllerService.removeById(controller.getId());
        cisAuthenticationService.remove(new LambdaQueryWrapper<CisAuthentication>()
                .eq(CisAuthentication::getControllerId, controller.getId()));
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
        CisController controller = cisControllerService.getById(id);
        controller.setIsStart(true);
        // 服务端启动
        if ("0".equals(controller.getServerType())) {
            CftpServer server = new CftpServer();
            try {
                server.start(Integer.parseInt(controller.getPort()), id);
            } catch (Exception e) {
                return responseFail(e.getMessage());
            }
            serverMap.put(id, server);
        } else {
            // 客户端启动
            CftpClient client = new CftpClient();
            try {
                client.start(controller.getIp(), Integer.parseInt(controller.getPort()), id);
            } catch (Exception e) {
                client.stop();
                return responseFail(e.getMessage());
            }
            clientMap.put(id,client);
        }
        cisControllerService.updateById(controller);
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
        CisController controller = cisControllerService.getById(id);
        controller.setIsStart(false);
        if ("0".equals(controller.getServerType())) {
            if(serverMap.get(id) != null) {
                serverMap.get(id).stop();
                serverMap.remove(id);
            }
        } else {
            if(clientMap.get(id) != null) {
                clientMap.get(id).stop();
                clientMap.remove(id);
            }
        }
        cisControllerService.updateById(controller);
        return responseSuccess();
    }

    /**
     * 查询认证
     *
     * @param controllerId 外键ID
     * @return 查询结果
     */
    @RequestMapping(value = "getAuthList")
    public String getAuthList(String controllerId) {
        List<CisAuthentication> authList = cisAuthenticationService.list(new LambdaQueryWrapper<CisAuthentication>()
                .eq(CisAuthentication::getControllerId, controllerId));
        return getJson(authList);
    }
}
