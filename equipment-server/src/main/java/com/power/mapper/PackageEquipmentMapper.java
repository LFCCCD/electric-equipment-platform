package com.power.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.power.entity.PackageEquipment;

@Mapper
public interface PackageEquipmentMapper {
    /**
     * 根据设备id查询套餐id
     *
     * @param equipmentId
     * @return
     */
    List<Long> getPackageIdByEquipmentIds(List<Long> equipmentIds);

    /**
     * 批量插入
     *
     * @param packageEquipments
     */
    void insertBatch(List<PackageEquipment> packageEquipments);

    /**
     * 根据套餐id删除
     *
     * @param packageId
     */
    @Delete("delete from package_equipment where package_id = #{packageId}")
    void deleteByPackageId(Long packageId);

    /**
     * 根据套餐id查询设备
     *
     * @param packageId
     */
    @Select("select * from package_equipment where package_id = #{packageId}")
    List<PackageEquipment> getEquipmentByPackageId(Long packageId);
}
