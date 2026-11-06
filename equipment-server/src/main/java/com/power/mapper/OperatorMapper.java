package com.power.mapper;

import com.github.pagehelper.Page;
import com.power.annotation.AutoFill;
import com.power.dto.OperatorPageQueryDTO;
import com.power.entity.Operator;
import com.power.enumeration.OperationType;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OperatorMapper {

    /**
     * 根据用户名查询运营人员
     *
     * @param username
     * @return
     */
    @Select("select * from admin_operator where username = #{username}")
    Operator getByUsername(String username);

    /**
     * 保存运营人员信息
     *
     * @param operator
     */
    @Insert("INSERT INTO admin_operator " +
            "(name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) "
            +
            "VALUES(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Operator operator);

    /**
     * 分页查询运营人员信息
     *
     * @param operatorPageQueryDTO
     * @return
     */
    Page<Operator> pageQuery(OperatorPageQueryDTO operatorPageQueryDTO);

    /**
     * 根据主键修改运营人员信息
     *
     * @param operator
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Operator operator);

    /**
     * 根据主键查询运营人员信息
     *
     * @param id
     * @return
     */
    @Select("select * from admin_operator where id = #{id}")
    Operator getById(long id);

}
