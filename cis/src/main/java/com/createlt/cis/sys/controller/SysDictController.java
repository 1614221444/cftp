package com.createlt.cis.sys.controller;

import com.createlt.cis.common.BaseController;
import com.createlt.cis.sys.entity.SysDict;
import com.createlt.cis.sys.service.ISysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@RestController
@RequestMapping("/sys/dict")
public class SysDictController extends BaseController {

    private ISysDictService dictService;

    @Autowired
    public SysDictController(ISysDictService dictService) {
        this.dictService = dictService;
    }
    @RequestMapping(value = "noPageList")
    public String noPageList() {
        List<SysDict> list = dictService.list();
        return getJson(list);
    }
}
