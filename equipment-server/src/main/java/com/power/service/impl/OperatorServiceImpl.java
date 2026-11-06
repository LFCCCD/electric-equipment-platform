package com.power.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.power.constant.MessageConstant;
import com.power.constant.PasswordConstant;
import com.power.constant.StatusConstant;
// import com.power.context.BaseContext;
import com.power.dto.OperatorDTO;
import com.power.dto.OperatorLoginDTO;
import com.power.dto.OperatorPageQueryDTO;
import com.power.entity.Operator;
import com.power.exception.AccountLockedException;
import com.power.exception.AccountNotFoundException;
import com.power.exception.PasswordErrorException;
import com.power.mapper.OperatorMapper;
import com.power.result.PageResult;
import com.power.service.OperatorService;
import com.power.utils.PasswordUtil;

// import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperatorServiceImpl implements OperatorService {

    @Autowired
    private OperatorMapper operatorMapper;

    /**
     * 运营人员登录
     *
     * @param operatorLoginDTO
     * @return
     */
    public Operator login(OperatorLoginDTO operatorLoginDTO) {
        String username = operatorLoginDTO.getUsername();
        // 1、根据用户名查询数据库中的数据
        Operator operator = operatorMapper.getByUsername(username);

        // 2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (operator == null) {
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码比对
        if (!PasswordUtil.matches(operatorLoginDTO.getPassword(), operator.getPassword())) {
            // 密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (operator.getStatus() == StatusConstant.DISABLE) {
            // 账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 3、返回实体对象
        return operator;
    }

    /**
     * 保存运营人员信息
     *
     * @param operatorDTO
     */
    @Override
    public void save(OperatorDTO operatorDTO) {
        Operator operator = new Operator();
        // 对象属性拷贝
        BeanUtils.copyProperties(operatorDTO, operator);
        // 设置账号状态
        operator.setStatus(StatusConstant.ENABLE);
        // 设置密码 默认密码为123456
        operator.setPassword(PasswordUtil.encode(PasswordConstant.DEFAULT_PASSWORD));
        // 设置更新时间和创建时间
        // operator.setCreateTime(LocalDateTime.now());
        // operator.setUpdateTime(LocalDateTime.now());
        // 记录当家创建人和更新人 id
        // operator.setCreateUser(BaseContext.getCurrentId());
        // operator.setUpdateUser(BaseContext.getCurrentId());

        // 保存到数据库
        operatorMapper.insert(operator);
    }

    @Override
    public PageResult<Operator> pageQuery(OperatorPageQueryDTO operatorPageQueryDTO) {
        // 1、设置分页参数
        PageHelper.startPage(operatorPageQueryDTO.getPage(), operatorPageQueryDTO.getPageSize());
        // 2、查询数据
        Page<Operator> page = operatorMapper.pageQuery(operatorPageQueryDTO);
        // 3、封装结果
        long total = page.getTotal();
        List<Operator> records = page.getResult();
        return new PageResult<>(total, records);
    }

    /**
     * 启用/禁用
     *
     * @param status
     * @param id
     *
     * @return
     */
    @Override
    public void startOrStop(Integer status, long id) {
        Operator operator = Operator.builder()
                .status(status)
                .id(id)
                .build();
        operatorMapper.update(operator);
    }

    /**
     * 根据id查询运营人员
     *
     * @param id
     * @return
     */
    @Override
    public Operator getById(long id) {
        return operatorMapper.getById(id);
    }

    @Override
    public void update(OperatorDTO operatorDTO) {
        Operator operator = new Operator();
        // 对象属性拷贝
        BeanUtils.copyProperties(operatorDTO, operator);
        // 设置更新时间
        // operator.setUpdateTime(LocalDateTime.now());
        // 记录当家更新人 id
        // operator.setUpdateUser(BaseContext.getCurrentId());

        // 保存到数据库
        operatorMapper.update(operator);
    }

}
