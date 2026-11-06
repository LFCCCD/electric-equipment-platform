package com.power.service;

import com.power.dto.UserLoginDTO;
import com.power.dto.BuyerLoginDTO;
import com.power.entity.User;

public interface UserService {

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO);

    /**
     * 本地账号登录，供 Web 演示端使用。
     *
     * @param buyerLoginDTO 登录参数
     * @return 采购用户
     */
    User localLogin(BuyerLoginDTO buyerLoginDTO);
}
