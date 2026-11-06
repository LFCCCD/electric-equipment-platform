package com.power.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 套餐总览
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentPackageOverViewVO implements Serializable {
    // 已启售数量
    private Integer sold;

    // 已下架数量
    private Integer discontinued;
}
