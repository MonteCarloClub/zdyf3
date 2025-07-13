package com.weiyan.atp.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体转换工具类
 * 用于DABEUser和数据库实体之间的转换
 */
@Slf4j
public class EntityConverter {

    /**
     * 将DABEUser转换为UserEntity
     */
    public static UserEntity toUserEntity(DABEUser dabeUser) {
        if (dabeUser == null) {
            return null;
        }

        try {
            return UserEntity.builder()
                    .userName(dabeUser.getName())
                    .name(dabeUser.getName())
                    .password(dabeUser.getPassword())
                    .userType(dabeUser.getUserType())
                    .channel(dabeUser.getChannel())
                    .apkMap(JsonProviderHolder.JACKSON.toJsonString(dabeUser.getApkMap()))
                    .askMap(JsonProviderHolder.JACKSON.toJsonString(dabeUser.getAskMap()))
                    .opkMap(JsonProviderHolder.JACKSON.toJsonString(dabeUser.getOpkMap()))
                    .oskMap(JsonProviderHolder.JACKSON.toJsonString(dabeUser.getOskMap()))
                    .eggAlpha(dabeUser.getEggAlpha())
                    .alpha(dabeUser.getAlpha())
                    .gAlpha(dabeUser.getGAlpha())
                    .appliedAttrMap(JsonProviderHolder.JACKSON.toJsonString(dabeUser.getAppliedAttrMap()))
                    .privacyAttrMap(JsonProviderHolder.JACKSON.toJsonString(dabeUser.getPrivacyAttrMap()))
                    .build();
        } catch (Exception e) {
            log.error("转换DABEUser到UserEntity失败", e);
            return null;
        }
    }

    /**
     * 将UserEntity转换为DABEUser
     */
    public static DABEUser toDABEUser(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        try {
            DABEUser dabeUser = new DABEUser();
            dabeUser.setName(userEntity.getName());
            dabeUser.setPassword(userEntity.getPassword());
            dabeUser.setUserType(userEntity.getUserType());
            dabeUser.setChannel(userEntity.getChannel());
            dabeUser.setEggAlpha(userEntity.getEggAlpha());
            dabeUser.setAlpha(userEntity.getAlpha());
            dabeUser.setGAlpha(userEntity.getGAlpha());

            // 转换JSON字符串为Map对象
            if (userEntity.getApkMap() != null) {
                dabeUser.setApkMap(JsonProviderHolder.JACKSON.parse(
                        userEntity.getApkMap(),
                        new TypeReference<Map<String, DABEUser.APK>>() {}));
            }

            if (userEntity.getAskMap() != null) {
                dabeUser.setAskMap(JsonProviderHolder.JACKSON.parse(
                        userEntity.getAskMap(),
                        new TypeReference<Map<String, DABEUser.ASK>>() {}));
            }

            if (userEntity.getOpkMap() != null) {
                dabeUser.setOpkMap(JsonProviderHolder.JACKSON.parse(
                        userEntity.getOpkMap(),
                        new TypeReference<Map<String, DABEUser.OPKPart>>() {}));
            }

            if (userEntity.getOskMap() != null) {
                dabeUser.setOskMap(JsonProviderHolder.JACKSON.parse(
                        userEntity.getOskMap(),
                        new TypeReference<Map<String, DABEUser.OSKPart>>() {}));
            }

            if (userEntity.getAppliedAttrMap() != null) {
                dabeUser.setAppliedAttrMap(JsonProviderHolder.JACKSON.parse(
                        userEntity.getAppliedAttrMap(),
                        new TypeReference<Map<String, String>>() {}));
            } else {
                dabeUser.setAppliedAttrMap(new HashMap<>());
            }

            if (userEntity.getPrivacyAttrMap() != null) {
                dabeUser.setPrivacyAttrMap(JsonProviderHolder.JACKSON.parse(
                        userEntity.getPrivacyAttrMap(),
                        new TypeReference<Map<String, String>>() {}));
            } else {
                dabeUser.setPrivacyAttrMap(new HashMap<>());
            }

            return dabeUser;
        } catch (Exception e) {
            log.error("转换UserEntity到DABEUser失败", e);
            return null;
        }
    }

    /**
     * 更新UserEntity的字段（从DABEUser）
     */
    public static void updateUserEntity(UserEntity userEntity, DABEUser dabeUser) {
        if (userEntity == null || dabeUser == null) {
            return;
        }

        try {
            userEntity.setName(dabeUser.getName());
            userEntity.setPassword(dabeUser.getPassword());
            userEntity.setUserType(dabeUser.getUserType());
            userEntity.setChannel(dabeUser.getChannel());
            userEntity.setEggAlpha(dabeUser.getEggAlpha());
            userEntity.setAlpha(dabeUser.getAlpha());
            userEntity.setGAlpha(dabeUser.getGAlpha());
            userEntity.setApkMap(JsonProviderHolder.JACKSON.toJsonString(dabeUser.getApkMap()));
            userEntity.setAskMap(JsonProviderHolder.JACKSON.toJsonString(dabeUser.getAskMap()));
            userEntity.setOpkMap(JsonProviderHolder.JACKSON.toJsonString(dabeUser.getOpkMap()));
            userEntity.setOskMap(JsonProviderHolder.JACKSON.toJsonString(dabeUser.getOskMap()));
            userEntity.setAppliedAttrMap(JsonProviderHolder.JACKSON.toJsonString(dabeUser.getAppliedAttrMap()));
            userEntity.setPrivacyAttrMap(JsonProviderHolder.JACKSON.toJsonString(dabeUser.getPrivacyAttrMap()));
        } catch (Exception e) {
            log.error("更新UserEntity失败", e);
        }
    }
}
