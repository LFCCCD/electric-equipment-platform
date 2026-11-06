package com.power.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.power.entity.EquipmentSpec;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentVO implements Serializable {

    private Long id;
    // 设备编码
    private String code;
    // 设备名称
    private String name;
    // 设备分类id
    private Long categoryId;
    // 品牌
    private String brand;
    // 型号
    private String model;
    // 计价单位
    private String unit;
    // 设备价格
    private BigDecimal price;
    // 可售数量
    private Integer stockQuantity;
    // 图片
    private String image;
    // 描述信息
    private String description;
    // 0 下架 1 上架
    private Integer status;
    // 更新时间
    private LocalDateTime updateTime;
    // 分类名称
    private String categoryName;
    // 设备关联的规格
    @Builder.Default // 默认值 为了防止空指针
    private List<EquipmentSpec> specs = new ArrayList<>();
}
