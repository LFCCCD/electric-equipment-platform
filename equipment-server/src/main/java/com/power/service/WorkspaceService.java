package com.power.service;

import com.power.vo.BusinessDataVO;
import com.power.vo.EquipmentOverViewVO;
import com.power.vo.OrderOverViewVO;
import com.power.vo.EquipmentPackageOverViewVO;
import java.time.LocalDateTime;

public interface WorkspaceService {

    /**
     * 根据时间段统计营业数据
     *
     * @param begin
     * @param end
     * @return
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询订单管理数据
     *
     * @return
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 查询设备总览
     *
     * @return
     */
    EquipmentOverViewVO getEquipmentOverView();

    /**
     * 查询套餐总览
     *
     * @return
     */
    EquipmentPackageOverViewVO getEquipmentPackageOverView();

}
