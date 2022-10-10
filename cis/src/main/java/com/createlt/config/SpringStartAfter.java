package com.createlt.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.createlt.agreement.base.BaseClient;
import com.createlt.agreement.base.BaseServer;
import com.createlt.agreement.client.CftpClient;
import com.createlt.agreement.server.CftpServer;
import com.createlt.cis.controller.CisControllerController;
import com.createlt.cis.entity.CisController;
import com.createlt.cis.service.ICisAuthenticationService;
import com.createlt.cis.service.ICisControllerService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
public class SpringStartAfter implements InitializingBean {

    @Resource
    private ICisControllerService cisControllerService;
    @Resource
    private ICisAuthenticationService cisAuthenticationService;

    /**
     * 初始化所有控制器
     */
    @Override
    public void afterPropertiesSet() {
        // 所有控制器初始化
        CisController init = new CisController();
        init.setIsStart(false);
        cisControllerService.update(init, new UpdateWrapper<>());
        // 查询所有自启动的控制器 排序按服务在先 客户端在后的顺序
        List<CisController> controllerList = cisControllerService.list
                (new LambdaQueryWrapper<CisController>().eq(CisController::getStartType, "0").orderByAsc(CisController::getServerType));

        controllerList.forEach((controller) -> {
            // 自动启动的服务
            if ("0".equals(controller.getServerType())) {
                BaseServer server = new CftpServer();
                server.start(Integer.parseInt(controller.getPort()), controller.getId());
                CisControllerController.serverMap.put(controller.getId(), server);
                // 更新实例启动状态
                controller.setIsStart(true);
                controller.setStartTime(new Date());
                cisControllerService.updateById(controller);
            } else {
                // 客户端启动
                BaseClient client = new CftpClient();
                try {
                    client.start(controller.getIp(), Integer.parseInt(controller.getPort()), controller.getId());
                    CisControllerController.clientMap.put(controller.getId(),client);
                } catch (Exception e) {
                    client.stop();
                }
            }
        });
    }

}
