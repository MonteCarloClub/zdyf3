package com.weiyan.atp.service.impl;

import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.entity.UserEntity;
import com.weiyan.atp.repository.UserRepository;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.utils.EntityConverter;
import com.weiyan.atp.utils.JsonProviderHolder;
import com.weiyan.atp.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * 基于数据库的DABE服务实现
 * 替代原有的文件存储方式
 */
@Service
@Primary  // 设置为主要实现，替代原有的文件存储实现
@Slf4j
@Validated
public class DatabaseDABEServiceImpl implements DABEService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChaincodeService chaincodeService;

    @Override
    public DABEUser getUser(@NotEmpty String fileName) {
        try {
            Optional<UserEntity> userEntityOpt = userRepository.findByUserName(fileName);
            if (userEntityOpt.isPresent()) {
                return EntityConverter.toDABEUser(userEntityOpt.get());
            }
            log.warn("用户不存在: {}", fileName);
            return null;
        } catch (Exception e) {
            log.error("获取用户失败: {}", fileName, e);
            return null;
        }
    }

    @Override
    public DABEUser getUser2(@NotEmpty String fileName, @NotEmpty String password) {
        try {
            String hashedPassword = SecurityUtils.md5(password);
            Optional<UserEntity> userEntityOpt = userRepository.findByUserNameAndPassword(fileName, hashedPassword);

            if (userEntityOpt.isPresent()) {
                return EntityConverter.toDABEUser(userEntityOpt.get());
            }

            log.warn("用户名或密码错误: {}", fileName);
            return null;
        } catch (Exception e) {
            log.error("验证用户失败: {}", fileName, e);
            return null;
        }
    }

    @Override
    public DABEUser getUser2DryRun(@NotEmpty String fileName, @NotEmpty String password) {
        // 干运行模式，不实际验证密码
        return getUser(fileName);
    }

    @Override
    public DABEUser getUser3(@NotEmpty String fileName, @NotEmpty String cert) {
        try {
            Optional<UserEntity> userEntityOpt = userRepository.findByUserName(fileName);
            if (!userEntityOpt.isPresent()) {
                log.warn("用户不存在: {}", fileName);
                return null;
            }

            // TODO: 实现证书验证逻辑
            // 这里可以添加证书验证的具体实现
            log.info("证书验证功能待实现，当前直接返回用户信息");

            return EntityConverter.toDABEUser(userEntityOpt.get());
        } catch (Exception e) {
            log.error("通过证书获取用户失败: {}", fileName, e);
            return null;
        }
    }

    @Override
    public DABEUser createUser(@NotEmpty String fileName, @NotEmpty String userName) {
        return createUser(fileName, userName, "DEFAULT", "default", "123456");
    }

    @Override
    public DABEUser createUser(@NotEmpty String fileName, @NotEmpty String userName,
                               @NotEmpty String userType, @NotEmpty String channel,
                               @NotEmpty String password) {
        try {
            // 检查用户是否已存在
            if (userRepository.existsByUserName(fileName)) {
                log.warn("用户已存在: {}", fileName);
                return getUser(fileName);
            }

            // 调用链码创建用户
            ChaincodeResponse response = chaincodeService.query(
                    ChaincodeTypeEnum.DABE, "/user/create",
                    new ArrayList<>(Collections.singletonList(userName)));

            if (response.getStatus() == ChaincodeResponse.Status.FAIL) {
                log.error("链码创建用户失败: {}", response.getMessage());
                return null;
            }

            // 解析链码返回的用户信息
            DABEUser dabeUser = JsonProviderHolder.JACKSON.parse(response.getMessage(), DABEUser.class);
            dabeUser.setUserType(userType);
            dabeUser.setChannel(channel);
            dabeUser.setPassword(SecurityUtils.md5(password));

            // 转换并保存到数据库
            UserEntity userEntity = EntityConverter.toUserEntity(dabeUser);
            userEntity.setUserName(fileName);
            userEntity = userRepository.save(userEntity);

            log.info("用户创建成功: {}", fileName);
            return EntityConverter.toDABEUser(userEntity);

        } catch (Exception e) {
            log.error("创建用户失败: {}", fileName, e);
            return null;
        }
    }

    @Override
    public DABEUser declareAttr(@NotEmpty String fileName, @NotEmpty String attrName) {
        try {
            DABEUser user = getUser(fileName);
            if (user == null) {
                log.warn("用户不存在: {}", fileName);
                return null;
            }

            String userJson = JsonProviderHolder.JACKSON.toJsonString(user);

            // 调用链码声明属性
            ChaincodeResponse response = chaincodeService.query(
                    ChaincodeTypeEnum.DABE, "/user/declareAttr",
                    new ArrayList<>(Arrays.asList(userJson, attrName)));

            if (response.getStatus() == ChaincodeResponse.Status.FAIL) {
                log.error("链码声明属性失败: {}", response.getMessage());
                return null;
            }

            // 更新用户信息
            DABEUser updatedUser = JsonProviderHolder.JACKSON.parse(response.getMessage(), DABEUser.class);
            updatedUser.setUserType(user.getUserType());
            updatedUser.setChannel(user.getChannel());
            updatedUser.setPassword(user.getPassword());

            // 保存到数据库
            Optional<UserEntity> userEntityOpt = userRepository.findByUserName(fileName);
            if (userEntityOpt.isPresent()) {
                UserEntity userEntity = userEntityOpt.get();
                EntityConverter.updateUserEntity(userEntity, updatedUser);
                userRepository.save(userEntity);

                log.info("属性声明成功: {} - {}", fileName, attrName);
                return updatedUser;
            }

            return null;
        } catch (Exception e) {
            log.error("声明属性失败: {} - {}", fileName, attrName, e);
            return null;
        }
    }

    @Override
    public ChaincodeResponse approveAttrApply(@NotEmpty String fileName, @NotEmpty String attrName,
                                              @NotEmpty String toUserName) {
        try {
            DABEUser user = getUser(fileName);
            if (user == null) {
                log.warn("用户不存在: {}", fileName);
                return ChaincodeResponse.builder()
                        .status(ChaincodeResponse.Status.FAIL)
                        .message("用户不存在")
                        .build();
            }

            String userJson = JsonProviderHolder.JACKSON.toJsonString(user);
            ChaincodeResponse response = chaincodeService.query(ChaincodeTypeEnum.DABE, "/user/approveAttr",
                    new ArrayList<>(Arrays.asList(userJson, toUserName, attrName)));

            log.info("属性申请审批完成: {} -> {} ({})", fileName, toUserName, attrName);
            return response;

        } catch (Exception e) {
            log.error("审批属性申请失败: {} -> {} ({})", fileName, toUserName, attrName, e);
            return ChaincodeResponse.builder()
                    .status(ChaincodeResponse.Status.FAIL)
                    .message("审批失败: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 保存或更新用户信息
     */
    public DABEUser saveUser(DABEUser dabeUser, String fileName) {
        try {
            Optional<UserEntity> existingUserOpt = userRepository.findByUserName(fileName);

            UserEntity userEntity;
            if (existingUserOpt.isPresent()) {
                // 更新现有用户
                userEntity = existingUserOpt.get();
                EntityConverter.updateUserEntity(userEntity, dabeUser);
            } else {
                // 创建新用户
                userEntity = EntityConverter.toUserEntity(dabeUser);
                userEntity.setUserName(fileName);
            }

            userEntity = userRepository.save(userEntity);
            return EntityConverter.toDABEUser(userEntity);

        } catch (Exception e) {
            log.error("保存用户失败: {}", fileName, e);
            return null;
        }
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(String fileName) {
        try {
            Optional<UserEntity> userEntityOpt = userRepository.findByUserName(fileName);
            if (userEntityOpt.isPresent()) {
                userRepository.delete(userEntityOpt.get());
                log.info("用户删除成功: {}", fileName);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除用户失败: {}", fileName, e);
            return false;
        }
    }
}
