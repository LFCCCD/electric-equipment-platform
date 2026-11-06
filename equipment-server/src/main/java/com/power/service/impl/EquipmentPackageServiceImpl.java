package com.power.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.power.constant.MessageConstant;
import com.power.constant.StatusConstant;
import com.power.dto.EquipmentPackageDTO;
import com.power.dto.EquipmentPackagePageQueryDTO;
import com.power.entity.Equipment;
import com.power.entity.EquipmentPackage;
import com.power.entity.PackageEquipment;
import com.power.exception.DeletionNotAllowedException;
import com.power.exception.EquipmentPackageEnableFailedException;
import com.power.mapper.EquipmentMapper;
import com.power.mapper.PackageEquipmentMapper;
import com.power.mapper.EquipmentPackageMapper;
import com.power.result.PageResult;
import com.power.service.EquipmentPackageService;
import com.power.vo.EquipmentItemVO;
import com.power.vo.EquipmentPackageVO;

@Service
public class EquipmentPackageServiceImpl implements EquipmentPackageService {

    @Autowired
    private EquipmentPackageMapper packageMapper;

    @Autowired
    private PackageEquipmentMapper packageEquipmentMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    /**
     * 新增套餐
     *
     * @param packageDTO
     */
    @Override
    @Transactional
    public void saveWithEquipment(EquipmentPackageDTO packageDTO) {
        EquipmentPackage equipmentPackage = new EquipmentPackage();
        BeanUtils.copyProperties(packageDTO, equipmentPackage);

        // 向套餐表插入数据
        packageMapper.insert(equipmentPackage);

        // 获取生成的套餐id
        Long packageId = equipmentPackage.getId();

        List<PackageEquipment> packageEquipments = packageDTO.getPackageEquipments();
        packageEquipments.forEach(packageEquipment -> {
            packageEquipment.setPackageId(packageId);
        });

        // 保存套餐和设备的关联关系
        packageEquipmentMapper.insertBatch(packageEquipments);
    }

    /**
     * 分页查询
     *
     * @param packagePageQueryDTO
     * @return
     */
    @Override
    public PageResult<EquipmentPackageVO> pageQuery(EquipmentPackagePageQueryDTO packagePageQueryDTO) {
        PageHelper.startPage(packagePageQueryDTO.getPage(), packagePageQueryDTO.getPageSize());
        Page<EquipmentPackageVO> page = packageMapper.pageQuery(packagePageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    /**
     * 删除套餐
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            EquipmentPackage equipmentPackage = packageMapper.getById(id);
            if (equipmentPackage.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.EQUIPMENT_PACKAGE_ON_SALE);
            }
        });

        // 删除套餐和设备的关联关系
        ids.forEach(packageID -> {
            // 删除套餐关联的设备
            packageEquipmentMapper.deleteByPackageId(packageID);
            // 删除套餐
            packageMapper.deleteById(packageID);
        });
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @Override
    public EquipmentPackageVO getById(Long id) {
        List<PackageEquipment> equipmentByPackageId = packageEquipmentMapper.getEquipmentByPackageId(id);
        EquipmentPackageVO packageVO = new EquipmentPackageVO();
        EquipmentPackage equipmentPackage = packageMapper.getById(id);
        BeanUtils.copyProperties(equipmentPackage, packageVO);
        packageVO.setPackageEquipments(equipmentByPackageId);

        return packageVO;
    }

    /**
     * 更新套餐
     *
     * @param packageDTO
     */
    @Override
    @Transactional
    public void update(EquipmentPackageDTO packageDTO) {
        EquipmentPackage equipmentPackage = new EquipmentPackage();
        BeanUtils.copyProperties(packageDTO, equipmentPackage);
        // 更新套餐
        packageMapper.update(equipmentPackage);
        // 更新套餐和设备的关联关系
        Long packageId = equipmentPackage.getId();
        packageEquipmentMapper.deleteByPackageId(packageId);
        List<PackageEquipment> packageEquipments = packageDTO.getPackageEquipments();
        packageEquipments.forEach(sd -> {
            sd.setPackageId(packageId);
        });
        packageEquipmentMapper.insertBatch(packageEquipments);
    }

    /**
     * 上架或下架
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 有下架设备提示"套餐内包含未上架设备，无法启售"
        if (status == StatusConstant.ENABLE) {
            List<Equipment> equipmentList = equipmentMapper.getByPackageId(id);
            if (equipmentList != null && equipmentList.size() > 0) {
                equipmentList.forEach(equipment -> {
                    if (equipment.getStatus() == StatusConstant.DISABLE) {
                        throw new EquipmentPackageEnableFailedException(MessageConstant.EQUIPMENT_PACKAGE_ENABLE_FAILED);
                    }
                });
            }
        }

        EquipmentPackage equipmentPackage = EquipmentPackage.builder().id(id).status(status).build();
        packageMapper.update(equipmentPackage);
    }

    /**
     * 条件查询
     *
     * @param equipmentPackage
     * @return
     */
    public List<EquipmentPackage> list(EquipmentPackage equipmentPackage) {
        List<EquipmentPackage> list = packageMapper.list(equipmentPackage);
        return list;
    }

    /**
     * 根据id查询设备选项
     *
     * @param id
     * @return
     */
    public List<EquipmentItemVO> getEquipmentItemById(Long id) {
        return packageMapper.getEquipmentItemByPackageId(id);
    }

}
