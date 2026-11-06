package com.power.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.power.constant.MessageConstant;
import com.power.constant.StatusConstant;
// import com.power.context.BaseContext;
import com.power.dto.CategoryDTO;
import com.power.dto.CategoryPageQueryDTO;
import com.power.entity.Category;
import com.power.exception.DeletionNotAllowedException;
import com.power.mapper.CategoryMapper;
import com.power.mapper.EquipmentMapper;
import com.power.mapper.EquipmentPackageMapper;
import com.power.result.PageResult;
import com.power.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类业务层
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private EquipmentPackageMapper packageMapper;

    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        // 属性拷贝
        BeanUtils.copyProperties(categoryDTO, category);

        // 分类状态默认为禁用状态0
        category.setStatus(StatusConstant.DISABLE);

        // 设置创建时间、修改时间、创建人、修改人
        // category.setCreateTime(LocalDateTime.now());
        // category.setUpdateTime(LocalDateTime.now());
        // category.setCreateUser(BaseContext.getCurrentId());
        // category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.insert(category);
    }

    /**
     * 分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    public PageResult<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        // 下一条sql进行分页，自动加入limit关键字分页
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    /**
     * 根据id删除分类
     *
     * @param id
     */
    public void deleteById(Long id) {
        // 查询当前分类是否关联了设备，如果关联了就抛出业务异常
        Integer count = equipmentMapper.countByCategoryId(id);
        if (count > 0) {
            // 当前分类下有设备，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_EQUIPMENT);
        }

        // 查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = packageMapper.countByCategoryId(id);
        if (count > 0) {
            // 当前分类下有设备，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_PACKAGE);
        }

        // 删除分类数据
        categoryMapper.deleteById(id);
    }

    /**
     * 修改分类
     *
     * @param categoryDTO
     */
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        // 设置修改时间、修改人
        // category.setUpdateTime(LocalDateTime.now());
        // category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.update(category);
    }

    /**
     * 启用、禁用分类
     *
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                // .updateTime(LocalDateTime.now())
                // .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.update(category);
    }

    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }
}
