package com.createlt.cis.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

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
public class CisModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

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

    /**
     * 删除标记
     */
    private String delState;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 新增时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 新增人
     */
    private String createBy;


}
