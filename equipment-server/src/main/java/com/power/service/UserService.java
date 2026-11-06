package com.power.service;

import com.power.dto.UserLoginDTO;
import com.power.entity.User;

public interface UserService {

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO);
}
