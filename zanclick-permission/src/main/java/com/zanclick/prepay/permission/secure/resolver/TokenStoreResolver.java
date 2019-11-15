package com.zanclick.prepay.permission.secure.resolver;

import com.zanclick.prepay.permission.util.JwtUtil;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.RedisUtil;

import java.util.Date;

/**
 * @author lvlu
 * @date 2019-03-06 11:57
 **/
public interface TokenStoreResolver {
    String KEYPREFIX = "JwtToken:";

    Long delaysAllowed = 100L;

    Long times = 1000 * 60 *60 *4L;

    /**
     * token存储
     *
     * @param token
     * @param userId
     * @param expireTime
     */
    default void addOrUpdateToken(String token, String userId, Date expireTime) {
        if (expireTime == null) {
            RedisUtil.set(KEYPREFIX + token, userId, times);
        } else {
            Long ttl = expireTime.getTime() - System.currentTimeMillis();
            if (ttl > delaysAllowed) {
                RedisUtil.set(KEYPREFIX + token, userId, ttl);
            } else {
                throw new JwtUtil.TokenValidationException("token已过期");
            }
        }
    }

    /**
     * 校验token是否还能使用，包括校验token的有效时间
     *
     * @param token
     * @return
     */
    default Boolean validateToken(String token) {
        Object object = RedisUtil.get(KEYPREFIX + token);
        String userId = object == null ? null : (String) object;
        if (DataUtil.isNotEmpty(userId)) {
            return true;
        } else {
            throw new JwtUtil.TokenValidationException("token已过期");
        }
    }

    /**
     * 删除token,使token不能再使用
     *
     * @param token
     */
    default void deleteTokenByToken(String token) {
        RedisUtil.del(KEYPREFIX + token);
    }
}
