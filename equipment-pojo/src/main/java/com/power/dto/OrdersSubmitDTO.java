package com.power.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersSubmitDTO implements Serializable {
    //交付地址id
    private Long deliveryAddressId;
    //付款方式
    private int payMethod;
    //备注
    private String remark;
    //预计交付时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;
    //配送状态  1立即送出  0选择具体时间
    private Integer deliveryStatus;
    //需要安装的设备数量
    private Integer installationQuantity;
    //安装服务状态 0不需要 1需要
    private Integer installationStatus;
    //服务费
    private Integer serviceFee;
    //总金额
    private BigDecimal amount;
}
