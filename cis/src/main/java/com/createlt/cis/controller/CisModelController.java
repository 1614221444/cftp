package com.createlt.cis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.createlt.cis.entity.CisModel;
import com.createlt.cis.service.ICisModelService;
import com.createlt.common.BaseController;
import com.createlt.mapping.utils.RunJavaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 解析器脚本 前端控制器
 * </p>
 *
 * @author wuyh
 * @since 2022-10-20
 */
@RestController
@RequestMapping("/cis/model")
public class CisModelController extends BaseController {

    private  ICisModelService cisModelService;
    @Autowired
    public CisModelController(ICisModelService cisModelService) {
        this.cisModelService = cisModelService;
    }

    /**
     * 分页查询解析器
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(value = "list")
    public String list(CisModel model, Page<CisModel> page) {
        page = cisModelService.page(page, new LambdaQueryWrapper<CisModel>()
                .like(!StringUtils.isEmpty(model.getName()), CisModel::getName, "%" + model.getName() + "%"));
        return getJson(page);
    }

    /**
     * 不分页查询解析器
     */
    @RequestMapping(value = "listAll")
    public String listAll() {
        List<CisModel> modelList = cisModelService.list();
        return getJson(modelList);
    }

    /**
     * 保存解析器
     *
     * @param model 解析器
     * @return 保存结果
     */
    @RequestMapping(value = "save")
    public String save(CisModel model) {
        RunJavaUtils javaUtils = new RunJavaUtils();
        try {
            String ret = javaUtils.setScript(model.getId(), model.getScript());
            if (!"".equals(ret)) {
                return responseFail(ret);
            }
        } catch (IOException e) {
            return responseFail("类加载失败： " + e.getMessage());
        }
        if(StringUtils.isEmpty(model.getId())) {
            cisModelService.save(model);
        } else {
            cisModelService.updateById(model);
        }
        return responseSuccess();
    }

    /**
     * 删除解析器
     *
     * @param model 解析器
     * @return 删除结果
     */
    @RequestMapping(value = "delete")
    public String delete(CisModel model) {
        cisModelService.removeById(model.getId());
        return responseSuccess();
    }
}
