package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.Canteen;
import com.campus.entity.CanteenDish;
import com.campus.service.CanteenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 食堂控制器
 */
@RestController
@RequestMapping("/canteen")
@RequiredArgsConstructor
public class CanteenController {

    private final CanteenService canteenService;

    /**
     * 获取食堂列表
     */
    @GetMapping("/list")
    public Result<?> getCanteenList(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            return Result.success(canteenService.getCanteenList(pageNum, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取食堂详情
     */
    @GetMapping("/detail/{id}")
    public Result<Canteen> getCanteenDetail(@PathVariable Long id) {
        Canteen canteen = canteenService.getCanteenById(id);
        return Result.success(canteen);
    }

    /**
     * 获取食堂的菜品列表
     */
    @GetMapping("/dishes/{canteenId}")
    public Result<List<CanteenDish>> getDishesByCanteenId(@PathVariable Long canteenId) {
        List<CanteenDish> dishes = canteenService.getDishesByCanteenId(canteenId);
        return Result.success(dishes);
    }

    /**
     * 获取推荐菜品
     */
    @GetMapping("/recommend")
    public Result<List<CanteenDish>> getRecommendDishes() {
        List<CanteenDish> dishes = canteenService.getRecommendDishes();
        return Result.success(dishes);
    }

    /**
     * 搜索菜品
     */
    @GetMapping("/search")
    public Result<List<CanteenDish>> searchDishes(@RequestParam String keyword) {
        List<CanteenDish> dishes = canteenService.searchDishes(keyword);
        return Result.success(dishes);
    }
}
