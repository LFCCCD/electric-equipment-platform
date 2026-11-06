package com.power.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.power.constant.StatusConstant;
import com.power.dto.BuyerLoginDTO;
import com.power.entity.User;
import com.power.exception.LoginFailedException;
import com.power.mapper.UserMapper;
import com.power.properties.WeChatProperties;
import com.power.utils.PasswordUtil;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private WeChatProperties weChatProperties;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void localLoginReturnsEnabledUserWithMatchingPassword() {
        String rawPassword = "123456";
        User user = User.builder()
                .id(1L)
                .username("buyer")
                .password(PasswordUtil.encode(rawPassword))
                .status(StatusConstant.ENABLE)
                .build();
        when(userMapper.getByUsername("buyer")).thenReturn(user);

        BuyerLoginDTO loginDTO = new BuyerLoginDTO();
        loginDTO.setUsername("buyer");
        loginDTO.setPassword(rawPassword);

        assertEquals(user, userService.localLogin(loginDTO));
    }

    @Test
    void localLoginRejectsWrongPassword() {
        User user = User.builder()
                .username("buyer")
                .password(PasswordUtil.encode("123456"))
                .status(StatusConstant.ENABLE)
                .build();
        when(userMapper.getByUsername("buyer")).thenReturn(user);

        BuyerLoginDTO loginDTO = new BuyerLoginDTO();
        loginDTO.setUsername("buyer");
        loginDTO.setPassword("wrong");

        assertThrows(LoginFailedException.class, () -> userService.localLogin(loginDTO));
    }

    @Test
    void localLoginRejectsDisabledUser() {
        User user = User.builder()
                .username("buyer")
                .password(PasswordUtil.encode("123456"))
                .status(StatusConstant.DISABLE)
                .build();
        when(userMapper.getByUsername("buyer")).thenReturn(user);

        BuyerLoginDTO loginDTO = new BuyerLoginDTO();
        loginDTO.setUsername("buyer");
        loginDTO.setPassword("123456");

        assertThrows(LoginFailedException.class, () -> userService.localLogin(loginDTO));
    }
}
