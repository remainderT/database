package org.buaa.shortlink.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.buaa.shortlink.common.biz.user.UserContext;
import org.buaa.shortlink.common.convention.exception.ClientException;
import org.buaa.shortlink.common.convention.exception.ServiceException;
import org.buaa.shortlink.dao.entity.GroupDO;
import org.buaa.shortlink.dao.mapper.GroupMapper;
import org.buaa.shortlink.service.GroupService;
import org.buaa.shortlink.toolkit.RandomGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.buaa.shortlink.common.consts.UserConstants.GROUP_MAX_COUNT;
import static org.buaa.shortlink.common.enums.ServiceErrorCodeEnum.GROUP_GENERATE_ERROR;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.GROUP_SIZE_LIMIT;

/**
 * 短链接分组接口实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public void saveGroup(String groupName) {
        saveGroup(UserContext.getUsername(), groupName);
    }

    @Override
    public void saveGroup(String username, String groupName) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(groupDOList) && groupDOList.size() == GROUP_MAX_COUNT) {
            throw new ClientException(GROUP_SIZE_LIMIT);
        }
        int retryCount = 0;
        int maxRetries = 10;
        String gid = RandomGenerator.generateSixDigitCode();
        while (retryCount < maxRetries) {
            GroupDO groupDO = baseMapper.selectOne(Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getGid, gid)
                    .eq(GroupDO::getDelFlag, 0));
            if (groupDO == null) {
                break;
            }
            gid = RandomGenerator.generateSixDigitCode();
            retryCount++;
        }
        if (retryCount == maxRetries) {
            throw new ServiceException(GROUP_GENERATE_ERROR);
        }
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .sortOrder(0)
                .username(username)
                .name(groupName)
                .build();
        baseMapper.insert(groupDO);

    }
}
