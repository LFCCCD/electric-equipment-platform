package com.power.service.impl;

import com.power.context.BaseContext;
import com.power.entity.DeliveryAddress;
import com.power.mapper.DeliveryAddressMapper;
import com.power.service.DeliveryAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DeliveryAddressServiceImpl implements DeliveryAddressService {
    @Autowired
    private DeliveryAddressMapper deliveryAddressMapper;

    /**
     * 条件查询
     *
     * @param deliveryAddress
     * @return
     */
    public List<DeliveryAddress> list(DeliveryAddress deliveryAddress) {
        return deliveryAddressMapper.list(deliveryAddress);
    }

    /**
     * 新增地址
     *
     * @param deliveryAddress
     */
    public void save(DeliveryAddress deliveryAddress) {
        deliveryAddress.setUserId(BaseContext.getCurrentId());
        deliveryAddress.setIsDefault(0);
        deliveryAddressMapper.insert(deliveryAddress);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public DeliveryAddress getById(Long id) {
        DeliveryAddress deliveryAddress = deliveryAddressMapper.getById(id);
        return deliveryAddress;
    }

    /**
     * 根据id修改地址
     *
     * @param deliveryAddress
     */
    public void update(DeliveryAddress deliveryAddress) {
        deliveryAddressMapper.update(deliveryAddress);
    }

    /**
     * 设置默认地址
     *
     * @param deliveryAddress
     */
    @Transactional
    public void setDefault(DeliveryAddress deliveryAddress) {
        // 1、将当前用户的所有地址修改为非默认地址 update delivery_address set is_default = ? where user_id = ?
        deliveryAddress.setIsDefault(0);
        deliveryAddress.setUserId(BaseContext.getCurrentId());
        deliveryAddressMapper.updateIsDefaultByUserId(deliveryAddress);

        // 2、将当前地址改为默认地址 update delivery_address set is_default = ? where id = ?
        deliveryAddress.setIsDefault(1);
        deliveryAddressMapper.update(deliveryAddress);
    }

    /**
     * 根据id删除地址
     *
     * @param id
     */
    public void deleteById(Long id) {
        deliveryAddressMapper.deleteById(id);
    }

}
