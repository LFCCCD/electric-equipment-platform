package com.power.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.power.constant.JwtClaimsConstant;
import com.power.dto.BuyerLoginDTO;
import com.power.dto.UserLoginDTO;
import com.power.entity.User;
import com.power.properties.JwtProperties;
import com.power.result.Result;
import com.power.service.UserService;
import com.power.utils.JwtUtil;
import com.power.vo.UserLoginVO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录： userLoginDTO:{}", userLoginDTO);
        User user = userService.wxLogin(userLoginDTO);

        // 生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    /**
     * 本地账号登录，供 H5 演示端使用。
     */
    @PostMapping("/demo-login")
    public Result<UserLoginVO> demoLogin(@RequestBody BuyerLoginDTO buyerLoginDTO) {
        User user = userService.localLogin(buyerLoginDTO);
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        UserLoginVO loginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .username(user.getUsername())
                .name(user.getName())
                .token(token)
                .build();
        return Result.success(loginVO);
    }
}
