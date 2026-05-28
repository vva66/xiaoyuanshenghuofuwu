package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.PageResult;
import com.campus.entity.Club;
import com.campus.entity.ClubMember;
import com.campus.mapper.ClubMapper;
import com.campus.mapper.ClubMemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社团服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubMapper clubMapper;
    private final ClubMemberMapper clubMemberMapper;

    /**
     * 创建社团
     */
    @Transactional
    public void createClub(Club club) {
        club.setStatus(0);
        club.setMemberCount(0);
        clubMapper.insert(club);

        ClubMember member = new ClubMember();
        member.setClubId(club.getId());
        member.setUserId(club.getPresidentId());
        member.setUserName(club.getPresidentName());
        member.setRole("社长");
        member.setJoinDate(java.time.LocalDate.now());
        member.setStatus(1);
        clubMemberMapper.insert(member);

        club.setMemberCount(1);
        clubMapper.updateById(club);
    }

    /**
     * 更新社团
     */
    public void updateClub(Club club) {
        clubMapper.updateById(club);
    }

    /**
     * 删除社团
     */
    public void deleteClub(Long id) {
        clubMapper.deleteById(id);
    }

    /**
     * 获取社团详情
     */
    public Club getClubById(Long id) {
        return clubMapper.selectById(id);
    }

    /**
     * 分页查询社团列表
     */
    public PageResult<Club> getClubList(Integer pageNum, Integer pageSize, String category) {
        Page<Club> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Club> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(category)) {
            wrapper.eq(Club::getCategory, category);
        }
        
        wrapper.eq(Club::getStatus, 1) // 正常状态
               .orderByDesc(Club::getMemberCount);
        Page<Club> result = clubMapper.selectPage(page, wrapper);
        
        PageResult<Club> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    /**
     * 加入社团
     */
    @Transactional
    public void joinClub(Long clubId, Long userId, String userName) {
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId)
               .eq(ClubMember::getUserId, userId);
        ClubMember existing = clubMemberMapper.selectOne(wrapper);
        if (existing != null) {
            if (existing.getStatus() == 1) {
                throw new RuntimeException("您已加入该社团");
            }
            existing.setStatus(1);
            existing.setRole("成员");
            existing.setJoinDate(java.time.LocalDate.now());
            clubMemberMapper.updateById(existing);
            Club club = clubMapper.selectById(clubId);
            if (club != null) {
                club.setMemberCount(club.getMemberCount() + 1);
                clubMapper.updateById(club);
            }
            return;
        }

        ClubMember member = new ClubMember();
        member.setClubId(clubId);
        member.setUserId(userId);
        member.setUserName(userName);
        member.setRole("成员");
        member.setJoinDate(java.time.LocalDate.now());
        member.setStatus(1);
        clubMemberMapper.insert(member);

        Club club = clubMapper.selectById(clubId);
        if (club != null) {
            club.setMemberCount(club.getMemberCount() + 1);
            clubMapper.updateById(club);
        }
    }

    /**
     * 退出社团
     */
    @Transactional
    public void leaveClub(Long clubId, Long userId) {
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId)
               .eq(ClubMember::getUserId, userId)
               .eq(ClubMember::getStatus, 1);

        ClubMember member = clubMemberMapper.selectOne(wrapper);
        if (member == null) {
            throw new RuntimeException("您不是该社团成员");
        }

        if ("社长".equals(member.getRole())) {
            throw new RuntimeException("社长不能退出社团，请先转让社长身份");
        }

        member.setStatus(0);
        clubMemberMapper.updateById(member);

        Club club = clubMapper.selectById(clubId);
        if (club != null && club.getMemberCount() > 0) {
            club.setMemberCount(club.getMemberCount() - 1);
            clubMapper.updateById(club);
        }
    }

    /**
     * 获取用户加入的社团列表
     */
    public List<Club> getMyClubs(Long userId) {
        return clubMemberMapper.selectClubsByUserId(userId);
    }

    /**
     * 检查用户是否为社团社长
     */
    public boolean isPresident(Long clubId, Long userId) {
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId)
               .eq(ClubMember::getUserId, userId)
               .eq(ClubMember::getRole, "社长")
               .eq(ClubMember::getStatus, 1);
        return clubMemberMapper.selectCount(wrapper) > 0;
    }

    /**
     * 获取社团成员列表
     */
    public List<ClubMember> getMembers(Long clubId) {
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId)
               .eq(ClubMember::getStatus, 1)
               .orderByDesc(ClubMember::getRole)
               .orderByAsc(ClubMember::getJoinDate);
        return clubMemberMapper.selectList(wrapper);
    }

    /**
     * 分页查询待审核社团列表
     */
    public PageResult<Club> getPendingClubs(Integer pageNum, Integer pageSize) {
        Page<Club> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Club> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Club::getStatus, 0)
               .orderByDesc(Club::getCreateTime);
        
        Page<Club> result = clubMapper.selectPage(page, wrapper);
        
        PageResult<Club> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    /**
     * 审核通过社团
     */
    public void approveClub(Long id) {
        Club club = clubMapper.selectById(id);
        if (club != null) {
            club.setStatus(1);
            clubMapper.updateById(club);
        }
    }

    /**
     * 拒绝社团申请
     */
    public void rejectClub(Long id) {
        Club club = clubMapper.selectById(id);
        if (club != null) {
            club.setStatus(2);
            clubMapper.updateById(club);
        }
    }

    /**
     * 获取用户管理的社团（社长身份）
     */
    public List<Club> getMyManagedClubs(Long userId) {
        return clubMemberMapper.selectManagedClubsByUserId(userId);
    }

    /**
     * 移除社团成员
     */
    @Transactional
    public void removeMember(Long clubId, Long userId) {
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId)
               .eq(ClubMember::getUserId, userId)
               .eq(ClubMember::getStatus, 1);
        ClubMember member = clubMemberMapper.selectOne(wrapper);
        if (member == null) {
            throw new RuntimeException("该用户不是社团成员");
        }
        if ("社长".equals(member.getRole())) {
            throw new RuntimeException("不能移除社长");
        }
        member.setStatus(0);
        clubMemberMapper.updateById(member);

        Club club = clubMapper.selectById(clubId);
        if (club != null && club.getMemberCount() > 0) {
            club.setMemberCount(club.getMemberCount() - 1);
            clubMapper.updateById(club);
        }
    }

    /**
     * 获取入社申请列表
     */
    public List<ClubMember> getJoinRequests(Long clubId) {
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId)
               .eq(ClubMember::getStatus, 0)
               .orderByDesc(ClubMember::getCreateTime);
        return clubMemberMapper.selectList(wrapper);
    }

    /**
     * 审核入社申请
     */
    @Transactional
    public void approveJoinRequest(Long requestId, Integer status) {
        ClubMember member = clubMemberMapper.selectById(requestId);
        if (member == null) {
            throw new RuntimeException("申请不存在");
        }
        if (status == 1) {
            member.setStatus(1);
            member.setRole("成员");
            member.setJoinDate(java.time.LocalDate.now());
            clubMemberMapper.updateById(member);

            Club club = clubMapper.selectById(member.getClubId());
            if (club != null) {
                club.setMemberCount(club.getMemberCount() + 1);
                clubMapper.updateById(club);
            }
        } else {
            clubMemberMapper.deleteById(requestId);
        }
    }

    /**
     * 获取所有社团（管理端使用）
     */
    public PageResult<Club> getAllClubs(Integer pageNum, Integer pageSize, String category, Integer status) {
        Page<Club> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Club> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(category)) {
            wrapper.eq(Club::getCategory, category);
        }
        
        if (status != null) {
            wrapper.eq(Club::getStatus, status);
        }
        
        wrapper.orderByDesc(Club::getCreateTime);
        
        Page<Club> result = clubMapper.selectPage(page, wrapper);
        
        PageResult<Club> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    /**
     * 获取社团总数
     */
    public long getClubCount() {
        return clubMapper.selectCount(null);
    }
}
