package com.createlt.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.createlt.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author wuyh
 * @since 2022-08-25
 */
@Getter
@Setter
@TableName("SYS_DICT")
public class SysDict extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 键
     */
    private String dictKey;

    /**
     * 值
     */
    private String dictValue;

    /**
     * 内容
     */
    private String title;

    /**
     * 排序
     */
    private Integer dictOrder;

    private String remakes;


}
