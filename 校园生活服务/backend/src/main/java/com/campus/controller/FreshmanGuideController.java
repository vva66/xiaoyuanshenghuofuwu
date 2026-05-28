package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.FreshmanGuide;
import com.campus.service.FreshmanGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/freshman")
@RequiredArgsConstructor
public class FreshmanGuideController {

    private final FreshmanGuideService freshmanGuideService;

    @GetMapping("/guides")
    public Result<List<FreshmanGuide>> getGuides(@RequestParam(required = false) String category) {
        try {
            List<FreshmanGuide> guides = freshmanGuideService.getGuidesByCategory(category);
            return Result.success(guides);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/guide/{id}")
    public Result<FreshmanGuide> getGuideDetail(@PathVariable Long id) {
        try {
            FreshmanGuide guide = freshmanGuideService.getGuideById(id);
            return Result.success(guide);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
