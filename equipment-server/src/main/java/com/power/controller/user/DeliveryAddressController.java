package com.power.controller.user;

import com.power.context.BaseContext;
import com.power.entity.DeliveryAddress;
import com.power.result.Result;
import com.power.service.DeliveryAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user/deliveryAddress")
public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    /**
     * 查询当前登录用户的所有地址信息
     *
     * @return
     */
    @GetMapping("/list")
    public Result<List<DeliveryAddress>> list() {
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setUserId(BaseContext.getCurrentId());
        List<DeliveryAddress> list = deliveryAddressService.list(deliveryAddress);
        return Result.success(list);
    }

    /**
     * 新增地址
     *
     * @param deliveryAddress
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DeliveryAddress deliveryAddress) {
        deliveryAddressService.save(deliveryAddress);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DeliveryAddress> getById(@PathVariable Long id) {
        DeliveryAddress deliveryAddress = deliveryAddressService.getById(id);
        return Result.success(deliveryAddress);
    }

    /**
     * 根据id修改地址
     *
     * @param deliveryAddress
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody DeliveryAddress deliveryAddress) {
        deliveryAddressService.update(deliveryAddress);
        return Result.success();
    }

    /**
     * 设置默认地址
     *
     * @param deliveryAddress
     * @return
     */
    @PutMapping("/default")
    public Result<String> setDefault(@RequestBody DeliveryAddress deliveryAddress) {
        deliveryAddressService.setDefault(deliveryAddress);
        return Result.success();
    }

    /**
     * 根据id删除地址
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public Result<String> deleteById(Long id) {
        deliveryAddressService.deleteById(id);
        return Result.success();
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public Result<DeliveryAddress> getDefault() {
        // SQL:select * from delivery_address where user_id = ? and is_default = 1
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setIsDefault(1);
        deliveryAddress.setUserId(BaseContext.getCurrentId());
        List<DeliveryAddress> list = deliveryAddressService.list(deliveryAddress);

        if (list != null && list.size() == 1) {
            return Result.success(list.get(0));
        }

        return Result.error("没有查询到默认地址");
    }

}
