package com.power.controller.user;

import com.power.constant.StatusConstant;
import com.power.entity.Equipment;
import com.power.result.Result;
import com.power.service.EquipmentService;
import com.power.vo.EquipmentVO;
// import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userEquipmentController")
@RequestMapping("/user/equipment")
// @Slf4j
public class EquipmentController {
    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据分类id查询设备
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @SuppressWarnings("unchecked")
    public Result<List<EquipmentVO>> list(Long categoryId) {
        // 在redis中查询
        String cacheKey = "equipment_" + categoryId;
        List<EquipmentVO> list = (List<EquipmentVO>) redisTemplate.opsForValue().get(cacheKey);
        if (list != null && list.size() > 0) {
            return Result.success(list);
        }

        Equipment equipment = new Equipment();
        equipment.setCategoryId(categoryId);
        equipment.setStatus(StatusConstant.ENABLE);// 查询上架中的设备
        list = equipmentService.listWithSpec(equipment);
        // 放入redis
        redisTemplate.opsForValue().set(cacheKey, list);

        return Result.success(list);
    }

    /**
     * 查询设备详情和规格。
     *
     * @param id 设备ID
     * @return 设备详情
     */
    @GetMapping("/{id}")
    public Result<EquipmentVO> getById(@PathVariable Long id) {
        return Result.success(equipmentService.getByIdWithSpec(id));
    }

}
