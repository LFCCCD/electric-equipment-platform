package com.power.service;

import com.power.entity.DeliveryAddress;
import java.util.List;

public interface DeliveryAddressService {

    List<DeliveryAddress> list(DeliveryAddress deliveryAddress);

    void save(DeliveryAddress deliveryAddress);

    DeliveryAddress getById(Long id);

    void update(DeliveryAddress deliveryAddress);

    void setDefault(DeliveryAddress deliveryAddress);

    void deleteById(Long id);

}
