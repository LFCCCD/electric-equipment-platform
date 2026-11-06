package com.power.service;

import java.util.List;

import com.power.dto.EquipmentPackageDTO;
import com.power.dto.EquipmentPackagePageQueryDTO;
import com.power.entity.EquipmentPackage;
import com.power.result.PageResult;
import com.power.vo.EquipmentItemVO;
import com.power.vo.EquipmentPackageVO;

public interface EquipmentPackageService {

    /**
     * 新增套餐
     *
     * @param packageDTO
     */
    void saveWithEquipment(EquipmentPackageDTO packageDTO);

    /**
     * 分页查询
     *
     * @param packagePageQueryDTO
     * @return
     */
    PageResult<EquipmentPackageVO> pageQuery(EquipmentPackagePageQueryDTO packagePageQueryDTO);

    /**
     * 删除套餐
     *
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    EquipmentPackageVO getById(Long id);

    /**
     * 更新套餐
     *
     * @param packageDTO
     */
    void update(EquipmentPackageDTO packageDTO);

    /**
     * 启用或禁用套餐
     *
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 条件查询
     *
     * @param equipmentPackage
     * @return
     */
    List<EquipmentPackage> list(EquipmentPackage equipmentPackage);

    /**
     * 根据id查询设备选项
     *
     * @param id
     * @return
     */
    List<EquipmentItemVO> getEquipmentItemById(Long id);

}
