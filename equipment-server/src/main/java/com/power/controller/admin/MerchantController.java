package com.power.controller.admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.power.result.Result;

import lombok.extern.slf4j.Slf4j;

@RestController("adminMerchantController")
@RequestMapping("/admin/merchant")
@Slf4j
public class MerchantController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置商户状态 1: 营业 0: 休息
     *
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    public Result<String> setMerchantStatus(@PathVariable Integer status) {
        log.info("设置商户状态: {}", status == 1 ? "营业" : "休息");
        redisTemplate.opsForValue().set("MERCHANT_STATUS", status);
        return Result.success();
    }

    /**
     * 获取商户状态 1: 营业 0: 休息
     *
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getMerchantStatus() {
        Integer status = (Integer) Optional.ofNullable(redisTemplate.opsForValue().get("MERCHANT_STATUS")).orElse(0);
        log.info("获取商户状态: {}", status == 1 ? "营业" : "休息");
        return Result.success(status);
    }

}
