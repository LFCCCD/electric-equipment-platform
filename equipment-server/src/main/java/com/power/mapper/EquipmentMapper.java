package com.power.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.github.pagehelper.Page;
import com.power.annotation.AutoFill;
import com.power.dto.EquipmentPageQueryDTO;
import com.power.entity.Equipment;
import com.power.enumeration.OperationType;
import com.power.vo.EquipmentVO;

@Mapper
public interface EquipmentMapper {

    /**
     * 根据分类id查询设备数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from equipment where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增设备
     *
     * @param equipment
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Equipment equipment);

    /**
     * 分页查询设备
     *
     * @param equipmentPageQueryDTO
     * @return
     */
    Page<EquipmentVO> pageQuery(EquipmentPageQueryDTO equipmentPageQueryDTO);

    /**
     * 根据id查询设备
     *
     * @param id
     * @return
     */
    @Select("select * from equipment where id = #{id}")
    Equipment getById(Long id);

    /**
     * 删除设备
     *
     * @param equipment
     */
    @Delete("delete from equipment where id = #{id}")
    void deleteById(Long id);

        /**
     * 批量删除设备数据
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 更新设备
     *
     * @param equipment
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Equipment equipment);

    /**
     * 根据分类id查询设备
     *
     * @param categoryId
     * @return
     */
    List<Equipment> list(Equipment equipment);

    /**
     * 根据套餐id查询设备
     *
     * @param packageId
     * @return
     */
    @Select("select a.* from equipment a left join package_equipment b on a.id = b.equipment_id where b.package_id = #{packageId}")
    List<Equipment> getByPackageId(Long packageId);

    /**
     * 根据条件统计设备数量
     *
     * @param map
     * @return
     */
    Integer countByMap(Map<String, Integer> map);

}
