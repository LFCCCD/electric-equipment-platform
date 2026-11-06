package com.power.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.github.pagehelper.Page;
import com.power.annotation.AutoFill;
import com.power.dto.EquipmentPackagePageQueryDTO;
import com.power.entity.EquipmentPackage;
import com.power.enumeration.OperationType;
import com.power.vo.EquipmentItemVO;
import com.power.vo.EquipmentPackageVO;

@Mapper
public interface EquipmentPackageMapper {

    /**
     * 根据分类id查询套餐的数量
     *
     * @param id
     * @return
     */
    @Select("select count(id) from equipment_package where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 根据id查询套餐
     *
     * @param equipmentPackage
     * @return
     */
    @AutoFill(OperationType.UPDATE)
    void update(EquipmentPackage equipmentPackage);

    /**
     * 分页查询
     *
     * @param packagePageQueryDTO
     * @return
     */
    Page<EquipmentPackageVO> pageQuery(EquipmentPackagePageQueryDTO packagePageQueryDTO);

    /**
     * 新增套餐
     *
     * @param equipmentPackage
     */
    @AutoFill(OperationType.INSERT)
    void insert(EquipmentPackage equipmentPackage);

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @Select("select * from equipment_package where id = #{id}")
    EquipmentPackage getById(Long id);

    /**
     * 根据id删除套餐
     *
     * @param id
     */
    @Delete("delete from equipment_package where id = #{id}")
    void deleteById(Long id);

    /**
     * 动态条件查询套餐
     *
     * @param equipmentPackage
     * @return
     */
    List<EquipmentPackage> list(EquipmentPackage equipmentPackage);

    /**
     * 根据套餐id查询设备选项
     *
     * @param packageId
     * @return
     */
    @Select("select pe.name, pe.quantity, e.image, e.description " +
            "from package_equipment pe left join equipment e on pe.equipment_id = e.id " +
            "where pe.package_id = #{packageId}")
    List<EquipmentItemVO> getEquipmentItemByPackageId(Long packageId);

    /**
     * 根据条件统计套餐数量
     *
     * @param map
     * @return
     */
    Integer countByMap(Map<String, Integer> map);
}
