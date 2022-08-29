package com.createlt.cis.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 通知角色关系表
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Getter
@Setter
@TableName("SYS_NOTICE_ROLE")
public class SysNoticeRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知主键
     */
    private String noticeId;

    /**
     * 用户主键
     */
    private String roleId;


}
