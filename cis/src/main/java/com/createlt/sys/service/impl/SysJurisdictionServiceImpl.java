package com.createlt.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.createlt.sys.entity.SysJurisdiction;
import com.createlt.sys.mapper.SysJurisdictionMapper;
import com.createlt.sys.service.ISysJurisdictionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色权限关系表 服务实现类
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Service
public class SysJurisdictionServiceImpl extends ServiceImpl<SysJurisdictionMapper, SysJurisdiction> implements ISysJurisdictionService {


    /**
     * 替换保存角色权限
     * @param roleId 角色ID
     * @param list 要替换的信息
     */
    public void save (String roleId, List<SysJurisdiction> list) {
        this.remove(new LambdaQueryWrapper<SysJurisdiction>()
                .eq(SysJurisdiction::getRoleId, roleId));
        this.saveBatch(list);
    }

}
