package com.power.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Equipment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //设备编码
    private String code;

    //设备名称
    private String name;

    //设备分类id
    private Long categoryId;

    //品牌
    private String brand;

    //型号
    private String model;

    //计价单位
    private String unit;

    //设备价格
    private BigDecimal price;

    //可售数量
    private Integer stockQuantity;

    //图片
    private String image;

    //描述信息
    private String description;

    //0 下架 1 上架
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;

}
