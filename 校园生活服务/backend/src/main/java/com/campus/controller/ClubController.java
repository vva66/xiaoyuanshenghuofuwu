package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.Club;
import com.campus.entity.ClubMember;
import com.campus.entity.ClubTask;
import com.campus.service.ClubService;
import com.campus.service.ClubTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 社团控制器
 */
@RestController
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final ClubTaskService clubTaskService;

    /**
     * 创建社团
     */
    @PostMapping("/create")
    public Result<Void> createClub(@RequestBody Club club) {
        try {
            clubService.createClub(club);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新社团
     */
    @PutMapping("/update")
    public Result<Void> updateClub(@RequestBody Club club) {
        try {
            clubService.updateClub(club);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除社团
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteClub(@PathVariable Long id) {
        try {
            clubService.deleteClub(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取社团详情
     */
    @GetMapping("/detail/{id}")
    public Result<Club> getClubDetail(@PathVariable Long id) {
        Club club = clubService.getClubById(id);
        return Result.success(club);
    }

    /**
     * 分页查询社团列表
     */
    @GetMapping("/list")
    public Result<?> getClubList(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(required = false) String category) {
        try {
            return Result.success(clubService.getClubList(pageNum, pageSize, category));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 加入社团
     */
    @PostMapping("/join")
    public Result<Void> joinClub(@RequestBody Map<String, Object> params) {
        try {
            Long clubId = Long.parseLong(params.get("clubId").toString());
            Long userId = Long.parseLong(params.get("userId").toString());
            String userName = (String) params.get("userName");
            clubService.joinClub(clubId, userId, userName);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 退出社团
     */
    @PostMapping("/leave/{clubId}")
    public Result<Void> leaveClub(@PathVariable Long clubId, @RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.parseLong(params.get("userId").toString());
            clubService.leaveClub(clubId, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我加入的社团
     */
    @GetMapping("/my-clubs")
    public Result<List<Club>> getMyClubs(@RequestParam Long userId) {
        try {
            List<Club> clubs = clubService.getMyClubs(userId);
            return Result.success(clubs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取社团成员列表
     */
    @GetMapping("/members/{clubId}")
    public Result<?> getMembers(@PathVariable Long clubId) {
        try {
            return Result.success(clubService.getMembers(clubId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查用户是否为社长
     */
    @GetMapping("/isPresident")
    public Result<Boolean> isPresident(@RequestParam Long clubId, @RequestParam Long userId) {
        try {
            boolean result = clubService.isPresident(clubId, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取待审核社团列表（管理端）
     */
    @GetMapping("/pending")
    public Result<?> getPendingClubs(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            return Result.success(clubService.getPendingClubs(pageNum, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有社团（管理端）
     */
    @GetMapping("/admin/list")
    public Result<?> getAllClubs(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) Integer status) {
        try {
            return Result.success(clubService.getAllClubs(pageNum, pageSize, category, status));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审核通过社团
     */
    @PostMapping("/approve/{id}")
    public Result<Void> approveClub(@PathVariable Long id) {
        try {
            clubService.approveClub(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 拒绝社团申请
     */
    @PostMapping("/reject/{id}")
    public Result<Void> rejectClub(@PathVariable Long id) {
        try {
            clubService.rejectClub(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我管理的社团（社长身份）
     */
    @GetMapping("/my-managed")
    public Result<List<Club>> getMyManagedClubs(@RequestParam Long userId) {
        try {
            List<Club> clubs = clubService.getMyManagedClubs(userId);
            return Result.success(clubs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 移除社团成员
     */
    @PostMapping("/member/remove")
    public Result<Void> removeMember(@RequestBody Map<String, Object> params) {
        try {
            Long clubId = Long.parseLong(params.get("clubId").toString());
            Long userId = Long.parseLong(params.get("userId").toString());
            clubService.removeMember(clubId, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取入社申请列表
     */
    @GetMapping("/join-requests/{clubId}")
    public Result<List<ClubMember>> getJoinRequests(@PathVariable Long clubId) {
        try {
            List<ClubMember> requests = clubService.getJoinRequests(clubId);
            return Result.success(requests);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审核入社申请
     */
    @PostMapping("/join-request/handle")
    public Result<Void> approveJoinRequest(@RequestBody Map<String, Object> params) {
        try {
            Long requestId = Long.parseLong(params.get("requestId").toString());
            Integer status = Integer.parseInt(params.get("status").toString());
            clubService.approveJoinRequest(requestId, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建社团任务
     */
    @PostMapping("/task/create")
    public Result<Void> createTask(@RequestBody ClubTask task) {
        try {
            clubTaskService.createTask(task);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取社团任务列表
     */
    @GetMapping("/tasks/{clubId}")
    public Result<List<ClubTask>> getClubTasks(@PathVariable Long clubId) {
        try {
            List<ClubTask> tasks = clubTaskService.getClubTasks(clubId);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 完成任务
     */
    @PostMapping("/task/complete")
    public Result<Void> completeTask(@RequestBody Map<String, Object> params) {
        try {
            Long taskId = Long.parseLong(params.get("taskId").toString());
            Long userId = Long.parseLong(params.get("userId").toString());
            clubTaskService.completeTask(taskId, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/task/delete/{taskId}")
    public Result<Void> deleteTask(@PathVariable Long taskId) {
        try {
            clubTaskService.deleteTask(taskId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
