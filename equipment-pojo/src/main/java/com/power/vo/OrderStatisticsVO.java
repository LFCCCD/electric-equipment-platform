package com.power.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrderStatisticsVO implements Serializable {
    //待确认数量
    private Integer toBeConfirmed;

    //待发货数量
    private Integer confirmed;

    //已发货数量
    private Integer deliveryInProgress;
}
