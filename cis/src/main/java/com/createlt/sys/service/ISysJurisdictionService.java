package com.createlt.sys.service;

import com.createlt.sys.entity.SysJurisdiction;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色权限关系表 服务类
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
public interface ISysJurisdictionService extends IService<SysJurisdiction> {
    /**
     * 替换保存角色权限
     * @param roleId 角色ID
     * @param list 要替换的信息
     */
    void save (String roleId, List<SysJurisdiction> list);
}
