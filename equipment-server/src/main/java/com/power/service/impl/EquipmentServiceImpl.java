package com.power.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.power.constant.MessageConstant;
import com.power.constant.StatusConstant;
import com.power.dto.EquipmentDTO;
import com.power.dto.EquipmentPageQueryDTO;
import com.power.entity.Equipment;
import com.power.entity.EquipmentSpec;
import com.power.entity.EquipmentPackage;
import com.power.exception.DeletionNotAllowedException;
import com.power.mapper.EquipmentSpecMapper;
import com.power.mapper.EquipmentMapper;
import com.power.mapper.PackageEquipmentMapper;
import com.power.mapper.EquipmentPackageMapper;
import com.power.result.PageResult;
import com.power.service.EquipmentService;
import com.power.vo.EquipmentVO;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentSpecMapper equipmentSpecMapper;

    @Autowired
    private PackageEquipmentMapper packageEquipmentMapper;

    @Autowired
    private EquipmentPackageMapper packageMapper;

    /**
     * 新增设备以及保存规格
     *
     * @param equipmentDTO
     */
    @Override
    @Transactional // 要么都成功，要么都失败
    public void saveWithSpec(EquipmentDTO equipmentDTO) {
        // 保存设备
        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(equipmentDTO, equipment);
        equipmentMapper.insert(equipment);
        // 获取设备ID
        Long equipmentId = equipment.getId();
        // 保存规格
        List<EquipmentSpec> specs = equipmentDTO.getSpecs();
        if (specs != null && specs.size() > 0) {
            specs.forEach(spec -> {
                spec.setEquipmentId(equipmentId);
            });
            equipmentSpecMapper.insertBatch(specs);
        }
    }

    /**
     * 分页查询设备
     *
     * @param equipmentPageQueryDTO
     */
    @Override
    public PageResult<EquipmentVO> queryPage(EquipmentPageQueryDTO equipmentPageQueryDTO) {
        PageHelper.startPage(equipmentPageQueryDTO.getPage(), equipmentPageQueryDTO.getPageSize());
        Page<EquipmentVO> page = equipmentMapper.pageQuery(equipmentPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    /**
     * 删除设备
     *
     * @param ids
     */
    @Override
    @Transactional // 一致性
    public void deleteBatch(List<Long> ids) {
        // 判断设备是否能够删除 是否启用
        for (Long id : ids) {
            Equipment equipment = equipmentMapper.getById(id);
            if (equipment.getStatus() == StatusConstant.ENABLE) {
                // 抛出异常
                throw new DeletionNotAllowedException(MessageConstant.EQUIPMENT_ON_SALE);
            }
        }
        // 是否被套餐引用
        List<Long> packageIds = packageEquipmentMapper.getPackageIdByEquipmentIds(ids);
        if (packageIds != null && packageIds.size() > 0) {
            // 抛出异常
            throw new DeletionNotAllowedException(MessageConstant.EQUIPMENT_BE_RELATED_BY_PACKAGE);
        }

        // // 删除设备
        // for (Long id : ids) {
        //     equipmentMapper.deleteById(id);
        //     // 删除设备规格
        //     equipmentSpecMapper.deleteByEquipmentId(id);
        // }

        //批量删除设备数据
        equipmentMapper.deleteByIds(ids);

        //批量删除设备关联的规格数据
        equipmentSpecMapper.deleteByEquipmentIds(ids);

    }

    /**
     * 根据id查询设备
     *
     * @param id
     * @return
     */
    @Override
    public EquipmentVO getByIdWithSpec(Long id) {
        // 根据id查询设备
        Equipment equipment = equipmentMapper.getById(id);
        // 根据id查询设备规格
        List<EquipmentSpec> specs = equipmentSpecMapper.getByEquipmentId(id);
        // 封装数据
        EquipmentVO equipmentVO = new EquipmentVO();
        BeanUtils.copyProperties(equipment, equipmentVO);
        equipmentVO.setSpecs(specs);

        return equipmentVO;
    }

    /**
     * 更新设备以及保存规格
     *
     * @param equipmentDTO
     */
    @Override
    public void updateWithSpec(EquipmentDTO equipmentDTO) {
        // 修改设备的基本信息
        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(equipmentDTO, equipment);
        equipmentMapper.update(equipment);

        // 删除设备的规格
        equipmentSpecMapper.deleteByEquipmentId(equipmentDTO.getId());

        // 保存设备的规格
        List<EquipmentSpec> specs = equipmentDTO.getSpecs();
        if (specs != null && specs.size() > 0) {
            specs.forEach(spec -> {
                spec.setEquipmentId(equipmentDTO.getId());
            });
            equipmentSpecMapper.insertBatch(specs);
        }
    }

    /**
     * 启用或禁用设备
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setStatus(status);
        equipmentMapper.update(equipment);

        // 如果是禁用，需要将套餐中的设备也禁用
        if (status == StatusConstant.DISABLE) {
            List<Long> equipmentIds = new ArrayList<>();
            equipmentIds.add(id);
            List<Long> packageIds = packageEquipmentMapper.getPackageIdByEquipmentIds(equipmentIds);
            if (packageIds != null && packageIds.size() > 0) {
                for (Long packageId : packageIds) {
                    EquipmentPackage equipmentPackage = EquipmentPackage.builder()
                            .id(packageId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    packageMapper.update(equipmentPackage);
                }
            }
        }
    }

    /**
     * 根据设备分类id查询设备
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Equipment> list(Long categoryId) {
        Equipment equipment = Equipment.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return equipmentMapper.list(equipment);
    }

    /**
     * 条件查询设备和规格
     *
     * @param equipment
     * @return
     */
    public List<EquipmentVO> listWithSpec(Equipment equipment) {
        List<Equipment> equipmentList = equipmentMapper.list(equipment);

        List<EquipmentVO> equipmentVOList = new ArrayList<>();

        for (Equipment d : equipmentList) {
            EquipmentVO equipmentVO = new EquipmentVO();
            BeanUtils.copyProperties(d, equipmentVO);

            // 根据设备id查询对应的规格
            List<EquipmentSpec> specs = equipmentSpecMapper.getByEquipmentId(d.getId());

            equipmentVO.setSpecs(specs);
            equipmentVOList.add(equipmentVO);
        }

        return equipmentVOList;
    }
}
