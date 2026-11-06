package com.power.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 设备总览
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentOverViewVO implements Serializable {
    // 已启售数量
    private Integer sold;

    // 已下架数量
    private Integer discontinued;
}
