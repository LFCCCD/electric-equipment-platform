package com.power.service;

import com.power.dto.OperatorDTO;
import com.power.dto.OperatorLoginDTO;
import com.power.dto.OperatorPageQueryDTO;
import com.power.entity.Operator;
import com.power.result.PageResult;

public interface OperatorService {

    /**
     * 运营人员登录
     *
     * @param operatorLoginDTO
     * @return
     */
    Operator login(OperatorLoginDTO operatorLoginDTO);

    /**
     * 保存运营人员信息
     *
     * @param operatorDTO
     */
    void save(OperatorDTO operatorDTO);

    /**
     * 分页查询运营人员信息
     *
     * @param operatorPageQueryDTO
     */
    PageResult<Operator> pageQuery(OperatorPageQueryDTO operatorPageQueryDTO);

    /**
     * 启用/禁用
     *
     * @param status
     * @param id
     *
     * @return
     */
    void startOrStop(Integer status, long id);

    /**
     * 根据id查询运营人员
     *
     * @param id
     * @return
     */
    Operator getById(long id);

    /**
     * 修改运营人员信息
     *
     * @param operatorDTO
     */
    void update(OperatorDTO operatorDTO);

}
