package com.createlt.cis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.createlt.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 解析器脚本
 * </p>
 *
 * @author wuyh
 * @since 2022-10-20
 */
@Getter
@Setter
@TableName("CIS_MODEL")
public class CisModel extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型:0=其他;1=JSON;2=CSV
     */
    private String type;

    /**
     * 脚本
     */
    private String script;

    /**
     * 备注
     */
    private String remakes;


}
