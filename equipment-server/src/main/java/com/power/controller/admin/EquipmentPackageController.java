package com.power.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.power.dto.EquipmentPackageDTO;
import com.power.dto.EquipmentPackagePageQueryDTO;
import com.power.result.PageResult;
import com.power.result.Result;
import com.power.service.EquipmentPackageService;
import com.power.vo.EquipmentPackageVO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/package")
@Slf4j
public class EquipmentPackageController {

    @Autowired
    private EquipmentPackageService packageService;

    /**
     * 新增套餐
     *
     * @param packageDTO
     * @return
     */
    @PostMapping
    @CacheEvict(cacheNames = "packageCache", key = "#packageDTO.categoryId")
    public Result<String> save(@RequestBody EquipmentPackageDTO packageDTO) {
        log.info("新增套餐：{}", packageDTO);
        packageService.saveWithEquipment(packageDTO);
        return Result.success();
    }

    /**
     * 分页查询
     *
     * @param packagePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult<EquipmentPackageVO>> page(EquipmentPackagePageQueryDTO packagePageQueryDTO) {
        log.info("分页查询套餐列表，请求参数：{}", packagePageQueryDTO);
        PageResult<EquipmentPackageVO> pageResult = packageService.pageQuery(packagePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(cacheNames = "packageCache", allEntries = true)
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("删除套餐，ids：{}", ids);
        packageService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<EquipmentPackageVO> getById(@PathVariable Long id) {
        log.info("根据id查询套餐，id：{}", id);
        EquipmentPackageVO packageVO = packageService.getById(id);
        return Result.success(packageVO);
    }

    /**
     * 修改套餐
     *
     * @param packageDTO
     * @return
     */
    @PutMapping
    @CacheEvict(cacheNames = "packageCache", allEntries = true)
    public Result<String> update(@RequestBody EquipmentPackageDTO packageDTO) {
        log.info("修改套餐，请求参数：{}", packageDTO);
        packageService.update(packageDTO);
        return Result.success();
    }

    /**
     * 启用或停用套餐
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "packageCache", allEntries = true)
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用或停用套餐，status：{}，id：{}", status, id);
        packageService.startOrStop(status, id);
        return Result.success();
    }
}
