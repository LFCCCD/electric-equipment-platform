package com.power.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import com.power.entity.OrderDetail;
import com.power.entity.Orders;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false) // 不使用父类属性
public class OrderVO extends Orders {

    // 订单设备信息
    private String orderEquipments;

    // 订单详情
    private List<OrderDetail> orderDetailList;

}
