package com.power.dto;

import com.power.entity.EquipmentSpec;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class EquipmentDTO implements Serializable {

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
    //规格
    private List<EquipmentSpec> specs = new ArrayList<>();

}
