package com.createlt.sys.service;

import com.createlt.sys.entity.SysNoticeRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 通知角色关系表 服务类
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
public interface ISysNoticeRoleService extends IService<SysNoticeRole> {

    void save(String noticeId, List<SysNoticeRole> roleList);
}
