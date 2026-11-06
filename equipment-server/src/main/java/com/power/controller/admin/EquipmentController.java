package com.power.controller.admin;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.power.dto.EquipmentDTO;
import com.power.dto.EquipmentPageQueryDTO;
import com.power.entity.Equipment;
import com.power.result.PageResult;
import com.power.result.Result;
import com.power.service.EquipmentService;
import com.power.vo.EquipmentVO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/equipment")
@Slf4j
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新增设备
     *
     * @param equipmentDTO
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody EquipmentDTO equipmentDTO) {
        log.info("新增设备：{}", equipmentDTO);
        equipmentService.saveWithSpec(equipmentDTO);
        String key = "equipment_" + equipmentDTO.getCategoryId();
        clearRedis(key);
        return Result.success();
    }

    /**
     * 分页查询设备
     *
     * @param equipmentPageQueryDTO
     * @return
     */
    @GetMapping("/page") // query
    public Result<PageResult<EquipmentVO>> page(EquipmentPageQueryDTO equipmentPageQueryDTO) {
        log.info("分页查询设备：{}", equipmentPageQueryDTO);
        PageResult<EquipmentVO> querySet = equipmentService.queryPage(equipmentPageQueryDTO);
        return Result.success(querySet);
    }

    /**
     * 删除设备
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("删除设备：{}", ids);
        equipmentService.deleteBatch(ids);
        clearRedis("equipment_*");
        return Result.success();
    }

    /**
     * 根据id查询设备
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<EquipmentVO> getById(@PathVariable Long id) {
        log.info("根据id查询设备：{}", id);
        EquipmentVO equipmentVO = equipmentService.getByIdWithSpec(id);
        return Result.success(equipmentVO);
    }

    /**
     * 更新设备
     *
     * @param equipmentDTO
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody EquipmentDTO equipmentDTO) {
        log.info("更新设备：{}", equipmentDTO);
        equipmentService.updateWithSpec(equipmentDTO);
        clearRedis("equipment_*");
        return Result.success();
    }

    /**
     * 启用或停用设备
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        // 1. 启用 0. 停用
        log.info("启用或停用设备：{}", id);
        equipmentService.startOrStop(status, id);
        clearRedis("equipment_*");
        return Result.success();
    }

    /**
     * 根据分类id查询设备
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<Equipment>> list(Long categoryId) {
        List<Equipment> equipmentList = equipmentService.list(categoryId);
        return Result.success(equipmentList);
    }

    private void clearRedis(String keys) {
        Set<String> cacheKeys = redisTemplate.keys(keys);
        redisTemplate.delete(cacheKeys);
    }
}
