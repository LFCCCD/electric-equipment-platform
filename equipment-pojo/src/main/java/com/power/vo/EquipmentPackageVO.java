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

import com.power.entity.PackageEquipment;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentPackageVO implements Serializable {

    private Long id;

    // 分类id
    private Long categoryId;

    // 套餐名称
    private String name;

    // 套餐价格
    private BigDecimal price;

    // 状态 0:停用 1:启用
    private Integer status;

    // 描述信息
    private String description;

    // 图片
    private String image;

    // 更新时间
    private LocalDateTime updateTime;

    // 分类名称
    private String categoryName;

    // 套餐和设备的关联关系
    @Builder.Default // 默认值 为了防止空指针
    private List<PackageEquipment> packageEquipments = new ArrayList<>();
}
