package com.createlt.cis.controller;

import com.createlt.cis.service.ICisAuthenticationService;
import com.createlt.common.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 服务认证 前端控制器
 * </p>
 *
 * @author wuyh
 * @since 2022-09-01
 */
@RestController
@RequestMapping("/cis/authentication")
public class CisAuthenticationController extends BaseController {

    @Resource
    private ICisAuthenticationService cisAuthenticationService;
}
