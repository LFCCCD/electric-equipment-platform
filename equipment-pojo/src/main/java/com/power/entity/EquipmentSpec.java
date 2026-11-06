package com.power.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 设备规格
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    //设备id
    private Long equipmentId;

    //规格名称
    private String name;

    //规格数据list
    private String value;

}
