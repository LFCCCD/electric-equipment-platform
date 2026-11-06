package com.power.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 套餐设备关系
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageEquipment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //套餐id
    private Long packageId;

    //设备id
    private Long equipmentId;

    //设备名称 （冗余字段）
    private String name;

    //设备原价
    private BigDecimal price;

    //数量
    private Integer quantity;
}
