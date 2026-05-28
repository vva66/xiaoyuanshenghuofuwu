package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.PageResult;
import com.campus.entity.Canteen;
import com.campus.entity.CanteenDish;
import com.campus.mapper.CanteenMapper;
import com.campus.mapper.CanteenDishMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 食堂服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CanteenService {

    private final CanteenMapper canteenMapper;
    private final CanteenDishMapper canteenDishMapper;

    /**
     * 获取食堂列表
     */
    public PageResult<Canteen> getCanteenList(Integer pageNum, Integer pageSize) {
        Page<Canteen> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Canteen> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Canteen::getStatus, 1)
               .orderByAsc(Canteen::getSortOrder);
        Page<Canteen> result = canteenMapper.selectPage(page, wrapper);
        
        PageResult<Canteen> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    /**
     * 获取食堂详情
     */
    public Canteen getCanteenById(Long id) {
        return canteenMapper.selectById(id);
    }

    /**
     * 获取食堂的菜品列表
     */
    public List<CanteenDish> getDishesByCanteenId(Long canteenId) {
        LambdaQueryWrapper<CanteenDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CanteenDish::getCanteenId, canteenId)
               .eq(CanteenDish::getStatus, 1)
               .orderByDesc(CanteenDish::getIsRecommend)
               .orderByAsc(CanteenDish::getSortOrder);
        return canteenDishMapper.selectList(wrapper);
    }

    /**
     * 获取推荐菜品
     */
    public List<CanteenDish> getRecommendDishes() {
        LambdaQueryWrapper<CanteenDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CanteenDish::getIsRecommend, 1)
               .eq(CanteenDish::getStatus, 1)
               .orderByDesc(CanteenDish::getSalesCount)
               .last("LIMIT 20");
        return canteenDishMapper.selectList(wrapper);
    }

    /**
     * 搜索菜品
     */
    public List<CanteenDish> searchDishes(String keyword) {
        LambdaQueryWrapper<CanteenDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(CanteenDish::getDishName, keyword)
               .eq(CanteenDish::getStatus, 1)
               .orderByDesc(CanteenDish::getRating);
        return canteenDishMapper.selectList(wrapper);
    }

    /**
     * 新增食堂
     */
    public void addCanteen(Canteen canteen) {
        canteen.setStatus(1);
        canteenMapper.insert(canteen);
    }

    /**
     * 更新食堂
     */
    public void updateCanteen(Canteen canteen) {
        canteenMapper.updateById(canteen);
    }

    /**
     * 删除食堂
     */
    public void deleteCanteen(Long id) {
        canteenMapper.deleteById(id);
    }

    /**
     * 获取所有菜品
     */
    public List<CanteenDish> getAllDishes() {
        LambdaQueryWrapper<CanteenDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CanteenDish::getStatus, 1)
               .orderByDesc(CanteenDish::getIsRecommend)
               .orderByAsc(CanteenDish::getSortOrder);
        return canteenDishMapper.selectList(wrapper);
    }

    /**
     * 新增菜品
     */
    public void addDish(CanteenDish dish) {
        dish.setStatus(1);
        canteenDishMapper.insert(dish);
    }

    /**
     * 更新菜品
     */
    public void updateDish(CanteenDish dish) {
        canteenDishMapper.updateById(dish);
    }

    /**
     * 删除菜品
     */
    public void deleteDish(Long id) {
        canteenDishMapper.deleteById(id);
    }

    /**
     * 获取食堂总数
     */
    public long getCanteenCount() {
        return canteenMapper.selectCount(null);
    }

    /**
     * 获取菜品总数
     */
    public long getDishCount() {
        return canteenDishMapper.selectCount(null);
    }
}
