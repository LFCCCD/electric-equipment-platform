package com.power.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.context.BaseContext;
import com.power.dto.ShoppingCartDTO;
import com.power.entity.Equipment;
import com.power.entity.EquipmentPackage;
import com.power.entity.ShoppingCart;
import com.power.mapper.EquipmentMapper;
import com.power.mapper.EquipmentPackageMapper;
import com.power.mapper.ShoppingCartMapper;
import com.power.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentPackageMapper packageMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 查询购物车是否存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        // 存在则更新数量
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        } else {
            // 不存在则添加
            // 判断是单品还是套餐
            Long equipmentId = shoppingCartDTO.getEquipmentId();
            if (equipmentId != null) {
                // 单品
                Equipment equipment = equipmentMapper.getById(equipmentId);
                shoppingCart.setName(equipment.getName());
                shoppingCart.setImage(equipment.getImage());
                shoppingCart.setAmount(equipment.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
            } else {
                // 套餐
                Long packageId = shoppingCartDTO.getPackageId();
                EquipmentPackage equipmentPackage = packageMapper.getById(packageId);
                shoppingCart.setName(equipmentPackage.getName());
                shoppingCart.setImage(equipmentPackage.getImage());
                shoppingCart.setAmount(equipmentPackage.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
            }
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart cart = ShoppingCart.builder().id(userId).build();
        List<ShoppingCart> listCarts = shoppingCartMapper.list(cart);
        return listCarts;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 删除购物车中一个商品
     *
     * @param shoppingCartDTO
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && list.size() > 0) {
            shoppingCart = list.get(0);
            Integer number = shoppingCart.getNumber();
            if (number > 1) {
                shoppingCart.setNumber(number - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            } else {
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }
        }
    }

}
