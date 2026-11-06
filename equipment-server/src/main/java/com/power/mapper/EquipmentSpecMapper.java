package com.power.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.power.entity.EquipmentSpec;

@Mapper
public interface EquipmentSpecMapper {

    /**
     * 批量新增设备规格
     *
     * @param specs
     */
    void insertBatch(List<EquipmentSpec> specs);

    /**
     * 根据设备id删除规格
     *
     * @param equipmentId
     * @return
     */
    @Delete("delete from equipment_spec where equipment_id = #{equipmentId}")
    void deleteByEquipmentId(Long equipmentId);

    /**
     * 批量删除设备关联的规格数据
     * @param equipmentIds
     */
    void deleteByEquipmentIds(List<Long> equipmentIds);

    /**
     * 根据设备id查询规格
     *
     * @param equipmentId
     * @return
     */
    @Select("select * from equipment_spec where equipment_id = #{equipmentId}")
    List<EquipmentSpec> getByEquipmentId(Long equipmentId);

}
