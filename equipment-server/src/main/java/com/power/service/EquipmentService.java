package com.power.service;

import java.util.List;

import com.power.dto.EquipmentDTO;
import com.power.dto.EquipmentPageQueryDTO;
import com.power.entity.Equipment;
import com.power.result.PageResult;
import com.power.vo.EquipmentVO;

public interface EquipmentService {
    /**
     * 新增设备以及保存规格
     *
     * @param equipmentDTO
     */
    public void saveWithSpec(EquipmentDTO equipmentDTO);

    /**
     * 分页查询设备
     *
     * @param equipmentPageQueryDTO
     */
    public PageResult<EquipmentVO> queryPage(EquipmentPageQueryDTO equipmentPageQueryDTO);

    /**
     * 删除设备
     *
     * @param ids
     */
    public void deleteBatch(List<Long> ids);

    /**
     * 根据id查询设备
     *
     * @param id
     * @return
     */
    public EquipmentVO getByIdWithSpec(Long id);

    /**
     * 更新设备以及规格
     *
     * @param equipmentDTO
     */
    public void updateWithSpec(EquipmentDTO equipmentDTO);

    /**
     * 启用或禁用设备
     *
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id);

    /**
     * 根据分类id查询设备
     *
     * @param categoryId
     * @return
     */
    public List<Equipment> list(Long categoryId);

    /**
     * 条件查询设备和规格
     *
     * @param equipment
     * @return
     */
    List<EquipmentVO> listWithSpec(Equipment equipment);
}
