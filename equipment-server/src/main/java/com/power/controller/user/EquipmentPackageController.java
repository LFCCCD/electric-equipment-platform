package com.power.controller.user;

import com.power.constant.StatusConstant;
import com.power.entity.EquipmentPackage;
import com.power.result.Result;
import com.power.service.EquipmentPackageService;
import com.power.vo.EquipmentItemVO;
import com.power.vo.EquipmentPackageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userEquipmentPackageController")
@RequestMapping("/user/package")
public class EquipmentPackageController {
    @Autowired
    private EquipmentPackageService packageService;

    /**
     * 条件查询
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Cacheable(cacheNames = "packageCache", key = "#categoryId") // key: packageCache::categoryId
    public Result<List<EquipmentPackage>> list(Long categoryId) {
        EquipmentPackage equipmentPackage = new EquipmentPackage();
        equipmentPackage.setCategoryId(categoryId);
        equipmentPackage.setStatus(StatusConstant.ENABLE);

        List<EquipmentPackage> list = packageService.list(equipmentPackage);
        return Result.success(list);
    }

    /**
     * 根据套餐id查询包含的设备列表
     *
     * @param id
     * @return
     */
    @GetMapping("/equipment/{id}")
    public Result<List<EquipmentItemVO>> equipmentList(@PathVariable("id") Long id) {
        List<EquipmentItemVO> list = packageService.getEquipmentItemById(id);
        return Result.success(list);
    }

    /**
     * 查询套餐详情。
     *
     * @param id 套餐ID
     * @return 套餐详情
     */
    @GetMapping("/{id}")
    public Result<EquipmentPackageVO> details(@PathVariable("id") Long id) {
        return Result.success(packageService.getById(id));
    }
}
