package com.power.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrdersConfirmDTO implements Serializable {

    private Long id;
    //订单状态 1待付款 2待确认 3 已确认 4 已发货 5 已完成 6 已取消 7 退款
    private Integer status;

}
