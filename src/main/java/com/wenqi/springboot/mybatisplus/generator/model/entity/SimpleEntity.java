package com.wenqi.springboot.mybatisplus.generator.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 测试表
 * </p>
 *
 * @author abc
 * @since 2024-04-08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_simple")
@ApiModel(value = "SimpleEntity对象", description = "测试表")
public class SimpleEntity extends Model<SimpleEntity> {

    @ApiModelProperty("姓名")
    @TableField("name")
    private String name;

    @ApiModelProperty("删除标识1")
    @TableField("delete_flag")
    private Boolean deleteFlag;

    @ApiModelProperty("删除标识2")
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;

    @ApiModelProperty("版本")
    @TableField("version")
    @Version
    private Long version;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @Override
    public Serializable pkVal() {
        return null;
    }

}
