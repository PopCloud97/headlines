package com.popcloud.service;

import com.popcloud.util.RedisKeyUtil;
import com.popcloud.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LikeService {
    @Resource
    private RedisUtil redisUtil;
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        if(redisUtil.sHasKey(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        return redisUtil.sHasKey(disLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    public long like(int userId, int entityType, int entityId) {
        // 在喜欢集合里增加
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        redisUtil.sSet(likeKey, String.valueOf(userId));
        // 从反对里删除
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        redisUtil.setRemove(disLikeKey, String.valueOf(userId));
        return redisUtil.sGetSetSize(likeKey);
    }

    public long disLike(int userId, int entityType, int entityId) {
        // 在反对集合里增加
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        redisUtil.sSet(disLikeKey, String.valueOf(userId));
        // 从喜欢里删除
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        redisUtil.setRemove(likeKey, String.valueOf(userId));
        return redisUtil.sGetSetSize(likeKey);
    }

}
