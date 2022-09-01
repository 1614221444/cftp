package com.createlt.cis.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 不分页查询列表
     *
     * @return 字典列表
     */
    @RequestMapping(value = "noPageList")
    public String noPageList() {
        List<SysDict> list = dictService.list();
        return getJson(list);
    }

    /**
     * 分页查询列表
     *
     * @param dict 字典
     * @param page 分页
     * @return 分页字典结果
     */
    @RequestMapping(value = "list")
    public String list(SysDict dict, Page<SysDict> page) {
        page = dictService.page(page, new LambdaQueryWrapper<SysDict>()
                        .eq(!StringUtils.isEmpty(dict.getRemakes()), SysDict::getRemakes, dict.getRemakes())
                        .eq(!StringUtils.isEmpty(dict.getDictKey()), SysDict::getDictKey, dict.getDictKey()));
        return getJson(page);
    }

    /**
     * 保存字典
     *
     * @param dict 字典
     * @return 保存结果
     */
    @RequestMapping(value = "save")
    public String save(SysDict dict) {
        if(StringUtils.isEmpty(dict.getId())) {
            dictService.save(dict);
        } else {
            dictService.updateById(dict);
        }
        return responseSuccess();
    }

    /**
     * 删除字典
     *
     * @param dict 字典
     * @return 删除结果
     */
    @RequestMapping(value = "delete")
    public String delete(SysDict dict) {
        dictService.removeById(dict.getId());
        return responseSuccess();
    }


}
