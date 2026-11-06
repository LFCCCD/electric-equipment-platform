package com.power.dto;

import com.power.entity.PackageEquipment;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class EquipmentPackageDTO implements Serializable {

    private Long id;

    //分类id
    private Long categoryId;

    //套餐名称
    private String name;

    //套餐价格
    private BigDecimal price;

    //状态 0:停用 1:启用
    private Integer status;

    //描述信息
    private String description;

    //图片
    private String image;

    //套餐设备关系
    private List<PackageEquipment> packageEquipments = new ArrayList<>();

}
