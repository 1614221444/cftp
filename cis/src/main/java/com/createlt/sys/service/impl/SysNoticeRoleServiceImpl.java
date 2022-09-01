package com.createlt.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.createlt.sys.entity.SysNoticeRole;
import com.createlt.sys.mapper.SysNoticeRoleMapper;
import com.createlt.sys.service.ISysNoticeRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 通知角色关系表 服务实现类
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Service
@Transactional
public class SysNoticeRoleServiceImpl extends ServiceImpl<SysNoticeRoleMapper, SysNoticeRole> implements ISysNoticeRoleService {
    @Override
    public void save(String noticeId, List<SysNoticeRole> roleList) {
        this.remove(new LambdaQueryWrapper<SysNoticeRole>()
                .eq(SysNoticeRole::getNoticeId,noticeId));
        this.saveBatch(roleList);
    }

}
