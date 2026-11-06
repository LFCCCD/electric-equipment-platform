package com.power.controller.admin;

import com.power.constant.JwtClaimsConstant;
import com.power.dto.OperatorDTO;
import com.power.dto.OperatorLoginDTO;
import com.power.dto.OperatorPageQueryDTO;
import com.power.entity.Operator;
import com.power.properties.JwtProperties;
import com.power.result.PageResult;
import com.power.result.Result;
import com.power.service.OperatorService;
import com.power.utils.JwtUtil;
import com.power.vo.OperatorLoginVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 运营人员管理
 */
@RestController
@RequestMapping("/admin/operator")
@Slf4j
@Tag(name = "OperatorController", description = "运营人员管理")
public class OperatorController {

    @Autowired
    private OperatorService operatorService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param operatorLoginDTO
     * @return
     */
    @Operation(summary = "登录")
    @Parameters({
            @Parameter(name = "operatorLoginDTO", description = "登录信息", required = true)
    })
    @PostMapping("/login")
    public Result<OperatorLoginVO> login(@RequestBody OperatorLoginDTO operatorLoginDTO) {
        log.info("运营人员登录：{}", operatorLoginDTO);

        Operator operator = operatorService.login(operatorLoginDTO);

        // 登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, operator.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        OperatorLoginVO operatorLoginVO = OperatorLoginVO.builder()
                .id(operator.getId())
                .userName(operator.getUsername())
                .name(operator.getName())
                .token(token)
                .build();

        return Result.success(operatorLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增
     *
     * @param operatorDTO
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody OperatorDTO operatorDTO) {
        log.info("新增运营人员：{}", operatorDTO);
        operatorService.save(operatorDTO);
        return Result.success();
    }

    /**
     * 运营人员分页查询
     *
     * @param operatorPageQueryDTO
     *
     * @return Result<PageResult<Operator>>
     */
    @GetMapping("/page")
    public Result<PageResult<Operator>> page(OperatorPageQueryDTO operatorPageQueryDTO) {
        log.info("分页查询运营人员：{}", operatorPageQueryDTO);
        PageResult<Operator> pageQueryData = operatorService.pageQuery(operatorPageQueryDTO);
        return Result.success(pageQueryData);
    }

    /**
     * 启用/禁用
     *
     * @param status
     * @param id
     * @return Result<String>
     */
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable Integer status, long id) {
        log.info("运营人员启用/禁用：{}, {}", status, id);
        operatorService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id查询运营人员
     *
     * @param id
     * @return Result<Operator>
     */
    @GetMapping("/{id}")
    public Result<Operator> getById(@PathVariable long id) {
        log.info("根据id查询运营人员：{}", id);
        Operator operator = operatorService.getById(id);
        return Result.success(operator);
    }

    /**
     * 修改
     *
     * @param operatorDTO
     * @return Result<String>
     */
    @PutMapping
    public Result<String> update(@RequestBody OperatorDTO operatorDTO) {
        log.info("修改运营人员：{}", operatorDTO);
        operatorService.update(operatorDTO);
        return Result.success();
    }

}
