package com.power.mapper;

import com.power.entity.DeliveryAddress;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DeliveryAddressMapper {

    /**
     * 条件查询
     * @param deliveryAddress
     * @return
     */
    List<DeliveryAddress> list(DeliveryAddress deliveryAddress);

    /**
     * 新增
     * @param deliveryAddress
     */
    @Insert("insert into delivery_address" +
            "        (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code," +
            "         district_name, detail, label, is_default)" +
            "        values (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}," +
            "                #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(DeliveryAddress deliveryAddress);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Select("select * from delivery_address where id = #{id}")
    DeliveryAddress getById(Long id);

    /**
     * 根据id修改
     * @param deliveryAddress
     */
    void update(DeliveryAddress deliveryAddress);

    /**
     * 根据 用户id修改 是否默认地址
     * @param deliveryAddress
     */
    @Update("update delivery_address set is_default = #{isDefault} where user_id = #{userId}")
    void updateIsDefaultByUserId(DeliveryAddress deliveryAddress);

    /**
     * 根据id删除地址
     * @param id
     */
    @Delete("delete from delivery_address where id = #{id}")
    void deleteById(Long id);

}
