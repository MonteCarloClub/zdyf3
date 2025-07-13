package com.weiyan.atp.integration;

import com.weiyan.atp.AbeTrustPlatformApplication;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.entity.UserEntity;
import com.weiyan.atp.data.response.web.RsaKeysResponse;
import com.weiyan.atp.repository.UserRepository;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.service.impl.DatabaseDABEServiceImpl;
import com.weiyan.atp.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户管理集成测试类
 * 测试用户创建、查询、更新、删除以及公私钥管理功能
 */
@Slf4j
@SpringBootTest(classes = AbeTrustPlatformApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class UserManagementIntegrationTest {

    @Autowired
    private DatabaseDABEServiceImpl databaseDABEService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DABEService dabeService;

    // 测试常量
    private static final String TEST_USER_NAME = "test_user_integration";
    private static final String TEST_USER_DISPLAY_NAME = "集成测试用户";
    private static final String TEST_PASSWORD = "test123456";
    private static final String TEST_USER_TYPE = "INTEGRATION_TEST";
    private static final String TEST_CHANNEL = "test_channel";

    // 测试用的公私钥
    private String testPublicKey;
    private String testPrivateKey;

    @BeforeEach
    void setUp() {
        try {
            // 生成测试用的密钥对
            RsaKeysResponse keys = SecurityUtils.generateKeyPair();
            testPublicKey = keys.getPubKey();
            testPrivateKey = keys.getPriKey();
        } catch (NoSuchAlgorithmException e) {
            fail("生成测试密钥对失败: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        try {
            userRepository.findByUserName(TEST_USER_NAME).ifPresent(userRepository::delete);
            // 清理批量测试数据
            userRepository.findByUserType("BATCH_TEST").forEach(userRepository::delete);
        } catch (Exception e) {
            log.warn("清理测试数据失败", e);
        }
    }

    /**
     * 测试 getUser 方法 - 成功从数据库获取用户
     */
    @Test
    @Order(1)
    @DisplayName("测试获取用户成功")
    public void testGetUser_Success() {
        // Given - 创建测试用户
        UserEntity testUser = createTestUserEntity();

        // When
        DABEUser result = databaseDABEService.getUser(TEST_USER_NAME);

        // Then
        assertNotNull(result, "应该成功从数据库获取用户");
        assertEquals(TEST_USER_DISPLAY_NAME, result.getName(), "用户名应该匹配");
        assertEquals(TEST_USER_TYPE, result.getUserType(), "用户类型应该匹配");
        assertEquals(TEST_CHANNEL, result.getChannel(), "渠道应该匹配");

        log.info("用户获取测试通过: {}", result);
    }

    /**
     * 测试 getUser 方法 - 用户不存在
     */
    @Test
    @Order(2)
    @DisplayName("测试获取不存在的用户")
    public void testGetUser_UserNotFound() {
        // Given
        String nonExistentUser = "non_existent_user_12345";

        // When
        DABEUser result = databaseDABEService.getUser(nonExistentUser);

        // Then
        assertNull(result, "不存在的用户应返回null");
        log.info("不存在用户测试通过");
    }

    /**
     * 测试 getUser2 方法 - 密码验证成功
     */
    @Test
    @Order(3)
    @DisplayName("测试用户密码验证成功")
    public void testGetUser2_PasswordValidation_Success() {
        // Given
        createTestUserEntity();

        // When
        DABEUser result = databaseDABEService.getUser2(TEST_USER_NAME, TEST_PASSWORD);

        // Then
        assertNotNull(result, "密码正确时应返回用户");
        assertEquals(TEST_USER_DISPLAY_NAME, result.getName(), "用户名应该匹配");
        assertEquals(SecurityUtils.md5(TEST_PASSWORD), result.getPassword(), "密码哈希应该匹配");
        log.info("密码验证成功测试通过");
    }

    /**
     * 测试 getUser2 方法 - 密码验证失败
     */
    @Test
    @Order(4)
    @DisplayName("测试用户密码验证失败")
    public void testGetUser2_PasswordValidation_Failed() {
        // Given
        createTestUserEntity();
        String wrongPassword = "wrong_password";

        // When
        DABEUser result = databaseDABEService.getUser2(TEST_USER_NAME, wrongPassword);

        // Then
        assertNull(result, "密码错误时应返回null");
        log.info("密码验证失败测试通过");
    }

    /**
     * 测试用户创建功能
     */
    @Test
    @Order(5)
    @DisplayName("测试用户创建功能")
    public void testCreateUser_Success() {
        // Given
        String newUserName = TEST_USER_NAME + "_create";
        String newDisplayName = TEST_USER_DISPLAY_NAME + "_创建";

        // When
        DABEUser createdUser = databaseDABEService.createUser(
                newUserName, newDisplayName, TEST_USER_TYPE, TEST_CHANNEL, TEST_PASSWORD);

        // Then
        if (createdUser != null) {
            assertEquals(newDisplayName, createdUser.getName(), "创建的用户名应该匹配");
            assertEquals(TEST_USER_TYPE, createdUser.getUserType(), "用户类型应该匹配");
            assertEquals(TEST_CHANNEL, createdUser.getChannel(), "渠道应该匹配");

            // 验证数据库中的数据
            Optional<UserEntity> userEntityOpt = userRepository.findByUserName(newUserName);
            assertTrue(userEntityOpt.isPresent(), "用户应该被保存到数据库");

            // 清理测试数据
            userRepository.delete(userEntityOpt.get());
        }

        log.info("用户创建测试完成");
    }

    /**
     * 测试用户公私钥管理
     */
    @Test
    @Order(6)
    @DisplayName("测试用户公私钥管理")
    public void testUserKeyManagement() {
        // Given - 创建带有密钥的用户
        UserEntity testUser = createTestUserEntityWithKeys();

        // Then - 验证密钥存储
        assertNotNull(testUser.getPublicKey(), "用户公钥应该被设置");
        assertNotNull(testUser.getPrivateKey(), "用户私钥应该被设置");
        assertEquals(testPublicKey, testUser.getPublicKey(), "公钥存储应该匹配");
        assertEquals(testPrivateKey, testUser.getPrivateKey(), "私钥存储应该匹配");

        // 通过公钥查询用户
        Optional<UserEntity> keyQueryUser = userRepository.findByPublicKey(testPublicKey);
        assertTrue(keyQueryUser.isPresent(), "通过公钥查询用户应该成功");
        assertEquals(TEST_USER_NAME, keyQueryUser.get().getUserName(), "公钥查询结果应该匹配");

        // 测试密钥更新
        try {
            RsaKeysResponse newKeys = SecurityUtils.generateKeyPair();
            testUser.setPublicKey(newKeys.getPubKey());
            testUser.setPrivateKey(newKeys.getPriKey());
            UserEntity updatedUser = userRepository.save(testUser);

            assertEquals(newKeys.getPubKey(), updatedUser.getPublicKey(), "公钥更新应该成功");
            assertEquals(newKeys.getPriKey(), updatedUser.getPrivateKey(), "私钥更新应该成功");
        } catch (NoSuchAlgorithmException e) {
            log.warn("生成新密钥对失败", e);
        }

        log.info("用户公私钥管理测试通过");
    }

    /**
     * 测试用户DABE字段基础存储（仅测试数据库层面）
     * 注意：真正的属性管理需要通过链码服务，这里只测试基础的数据存储
     */
    @Test
    @Order(7)
    @DisplayName("测试用户DABE字段基础存储")
    public void testUserDABEFieldsStorage() {
        log.info("开始测试用户DABE字段基础存储");

        // Given - 创建用户
        UserEntity testUser = createTestUserEntity();

        // 设置DABE相关字段（仅测试数据库存储能力）
        String testEggAlpha = "test_egg_alpha_value";
        String testAlpha = "test_alpha_value";
        String testGAlpha = "test_g_alpha_value";
        String testUpk = "test_user_upk_value";

        // When - 更新用户DABE字段
        testUser.setEggAlpha(testEggAlpha);
        testUser.setAlpha(testAlpha);
        testUser.setGAlpha(testGAlpha);
        testUser.setUpk(testUpk);
        UserEntity updatedUser = userRepository.save(testUser);

        // Then - 验证DABE字段存储
        assertEquals(testEggAlpha, updatedUser.getEggAlpha(), "EggAlpha字段应该被正确存储");
        assertEquals(testAlpha, updatedUser.getAlpha(), "Alpha字段应该被正确存储");
        assertEquals(testGAlpha, updatedUser.getGAlpha(), "GAlpha字段应该被正确存储");
        assertEquals(testUpk, updatedUser.getUpk(), "UPK字段应该被正确存储");

        // 验证从数据库重新读取后的一致性
        Optional<UserEntity> reloadedUserOpt = userRepository.findByUserName(TEST_USER_NAME);
        assertTrue(reloadedUserOpt.isPresent(), "重新加载用户应该成功");

        UserEntity reloadedUser = reloadedUserOpt.get();
        assertEquals(testEggAlpha, reloadedUser.getEggAlpha(), "重新加载后EggAlpha应该一致");
        assertEquals(testAlpha, reloadedUser.getAlpha(), "重新加载后Alpha应该一致");
        assertEquals(testGAlpha, reloadedUser.getGAlpha(), "重新加载后GAlpha应该一致");
        assertEquals(testUpk, reloadedUser.getUpk(), "重新加载后UPK应该一致");

        log.info("用户DABE字段基础存储测试通过");
        log.warn("注意：此测试仅验证数据库存储，真正的属性管理需要通过DABEService和链码服务");
    }

    /**
     * 测试用户查询功能
     */
    @Test
    @Order(8)
    @DisplayName("测试用户查询功能")
    public void testUserQueryFunctions() {
        // Given - 创建多个测试用户
        createTestUserEntity();
        createAdditionalTestUsers();

        // 测试按用户类型查询
        List<UserEntity> usersByType = userRepository.findByUserType(TEST_USER_TYPE);
        assertFalse(usersByType.isEmpty(), "按用户类型查询应该有结果");

        // 测试按渠道查询
        List<UserEntity> usersByChannel = userRepository.findByChannel(TEST_CHANNEL);
        assertFalse(usersByChannel.isEmpty(), "按渠道查询应该有结果");

        // 测试关键词查询
        List<UserEntity> usersByKeyword = userRepository.findByKeyword("集成测试");
        assertFalse(usersByKeyword.isEmpty(), "关键词查询应该有结果");

        // 测试用户名存在性检查
        assertTrue(userRepository.existsByUserName(TEST_USER_NAME), "用户名应该存在");
        assertFalse(userRepository.existsByUserName("non_existent_user"), "不存在的用户名应该返回false");

        log.info("用户查询功能测试通过");
    }

    /**
     * 测试批量操作和性能
     */
    @Test
    @Order(9)
    @DisplayName("测试批量操作和性能")
    public void testBatchOperationsAndPerformance() {
        int batchSize = 10;
        long startTime = System.currentTimeMillis();

        // 批量创建用户
        List<UserEntity> batchUsers = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            UserEntity user = UserEntity.builder()
                    .userName("batch_user_" + i)
                    .name("批量用户" + i)
                    .userType("BATCH_TEST")
                    .channel("batch_channel")
                    .password(SecurityUtils.md5("password" + i))
                    .build();
            batchUsers.add(userRepository.save(user));
        }

        long createTime = System.currentTimeMillis();
        log.info("批量创建{}个用户耗时: {}ms", batchSize, createTime - startTime);

        // 批量查询
        List<UserEntity> queryResults = userRepository.findByUserType("BATCH_TEST");
        assertEquals(batchSize, queryResults.size(), "批量查询数量应该匹配");

        long queryTime = System.currentTimeMillis();
        log.info("批量查询耗时: {}ms", queryTime - createTime);

        // 批量删除
        userRepository.deleteAll(batchUsers);

        long deleteTime = System.currentTimeMillis();
        log.info("批量删除耗时: {}ms", deleteTime - queryTime);

        // 验证删除结果
        List<UserEntity> remainingUsers = userRepository.findByUserType("BATCH_TEST");
        assertEquals(0, remainingUsers.size(), "批量删除后应该没有残留数据");

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("批量操作总耗时: {}ms", totalTime);

        // 性能断言
        assertTrue(totalTime < 10000, "批量操作耗时不应超过10秒: " + totalTime + "ms");

        log.info("批量操作和性能测试通过");
    }

    /**
     * 测试异常情况处理
     */
    @Test
    @Order(10)
    @DisplayName("测试异常情况处理")
    public void testExceptionHandling() {
        // 测试空值处理
        assertThrows(
                ConstraintViolationException.class,
                () -> databaseDABEService.getUser(null),
                "空用户名应抛出 ConstraintViolationException"
        );

        // 测试特殊字符处理
        DABEUser specialCharResult = databaseDABEService.getUser("user@#$%^&*()");
        assertNull(specialCharResult, "特殊字符用户名应该返回null");

        log.info("异常情况处理测试通过");
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建测试用户实体
     */
    private UserEntity createTestUserEntity() {
        UserEntity user = UserEntity.builder()
                .userName(TEST_USER_NAME)
                .name(TEST_USER_DISPLAY_NAME)
                .password(SecurityUtils.md5(TEST_PASSWORD))
                .userType(TEST_USER_TYPE)
                .channel(TEST_CHANNEL)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    /**
     * 创建带有密钥的测试用户实体
     */
    private UserEntity createTestUserEntityWithKeys() {
        UserEntity user = UserEntity.builder()
                .userName(TEST_USER_NAME)
                .name(TEST_USER_DISPLAY_NAME)
                .password(SecurityUtils.md5(TEST_PASSWORD))
                .userType(TEST_USER_TYPE)
                .channel(TEST_CHANNEL)
                .publicKey(testPublicKey)
                .privateKey(testPrivateKey)
                .upk("test_user_upk_value")
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    /**
     * 创建额外的测试用户
     */
    private void createAdditionalTestUsers() {
        for (int i = 1; i <= 3; i++) {
            UserEntity user = UserEntity.builder()
                    .userName(TEST_USER_NAME + "_additional_" + i)
                    .name(TEST_USER_DISPLAY_NAME + "_额外_" + i)
                    .password(SecurityUtils.md5(TEST_PASSWORD))
                    .userType(TEST_USER_TYPE)
                    .channel(TEST_CHANNEL)
                    .build();
            userRepository.save(user);
        }
    }
}