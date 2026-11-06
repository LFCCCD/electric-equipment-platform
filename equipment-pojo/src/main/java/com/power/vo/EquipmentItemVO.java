package com.power.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentItemVO implements Serializable {

    //设备名称
    private String name;

    //份数
    private Integer quantity;

    //设备图片
    private String image;

    //设备描述
    private String description;
}
