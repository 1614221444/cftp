package com.cylt.ftp.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cylt.ftp.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;


@RestController
@RequestMapping(value = "api", produces = "text/plain; charset=utf-8")
public class ApiController {


    /**
     * log
     */
    private static Logger logger = LoggerFactory.getLogger(ApiController.class);

    @RequestMapping("/send")
    @ResponseBody
    public String send(@RequestParam String url) {
        logger.info("发送数据路径：{0}",url);
        // /Users/wuyh/Desktop/FTP/A/b.text
        if(App.client == null) {
            return "发送失败 服务端连接异常";
        }
        Send send = new Send(url, App.client, true);
        send.run();
        return "发送提交完成";
    }

}
