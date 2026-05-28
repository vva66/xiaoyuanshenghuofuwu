package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.entity.FreshmanGuide;
import com.campus.mapper.FreshmanGuideMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreshmanGuideService {

    private final FreshmanGuideMapper freshmanGuideMapper;

    public List<FreshmanGuide> getGuidesByCategory(String category) {
        LambdaQueryWrapper<FreshmanGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FreshmanGuide::getStatus, 1);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(FreshmanGuide::getCategory, category);
        }
        wrapper.orderByAsc(FreshmanGuide::getSortOrder)
               .orderByDesc(FreshmanGuide::getIsImportant)
               .orderByDesc(FreshmanGuide::getPublishTime);
        return freshmanGuideMapper.selectList(wrapper);
    }

    public List<FreshmanGuide> getAllGuides() {
        LambdaQueryWrapper<FreshmanGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FreshmanGuide::getStatus, 1)
               .orderByAsc(FreshmanGuide::getSortOrder)
               .orderByDesc(FreshmanGuide::getIsImportant)
               .orderByDesc(FreshmanGuide::getPublishTime);
        return freshmanGuideMapper.selectList(wrapper);
    }

    public FreshmanGuide getGuideById(Long id) {
        return freshmanGuideMapper.selectById(id);
    }
}
