package com.weiyan.atp.integration;

import com.weiyan.atp.AbeTrustPlatformApplication;
import com.weiyan.atp.data.bean.entity.OrganizationEntity;
import com.weiyan.atp.data.bean.entity.UserEntity;
import com.weiyan.atp.data.response.web.RsaKeysResponse;
import com.weiyan.atp.repository.OrganizationRepository;
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

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组织和用户公私钥管理集成测试类
 * 测试用户和组织的公私钥生成、存储、查询、更新等功能
 */
@Slf4j
@SpringBootTest(classes = AbeTrustPlatformApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class OrganizationKeyManagementIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private DatabaseDABEServiceImpl databaseDABEService;

    @Autowired
    private DABEService dabeService;

    // 测试常量
    private static final String TEST_USER_NAME = "key_test_user";
    private static final String TEST_USER_DISPLAY_NAME = "密钥测试用户";
    private static final String TEST_PASSWORD = "key123456";
    private static final String TEST_USER_TYPE = "KEY_TEST";
    private static final String TEST_CHANNEL = "key_channel";

    private static final String TEST_ORG_NAME = "key_test_org";
    private static final String TEST_ORG_DISPLAY_NAME = "密钥测试组织";
    private static final String TEST_ORG_TYPE = "KEY_TEST_ORG";

    // 测试用的公私钥
    private String testUserPublicKey;
    private String testUserPrivateKey;
    private String testOrgPublicKey;
    private String testOrgPrivateKey;

    @BeforeEach
    void setUp() {
        try {
            // 生成用户测试密钥对
            RsaKeysResponse userKeys = SecurityUtils.generateKeyPair();
            testUserPublicKey = userKeys.getPubKey();
            testUserPrivateKey = userKeys.getPriKey();

            // 生成组织测试密钥对
            RsaKeysResponse orgKeys = SecurityUtils.generateKeyPair();
            testOrgPublicKey = orgKeys.getPubKey();
            testOrgPrivateKey = orgKeys.getPriKey();
        } catch (NoSuchAlgorithmException e) {
            fail("生成测试密钥对失败: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        try {
            userRepository.findByUserName(TEST_USER_NAME).ifPresent(userRepository::delete);
            organizationRepository.findByOrgName(TEST_ORG_NAME).ifPresent(organizationRepository::delete);

            // 清理批量测试数据
            userRepository.findByUserType("BATCH_KEY_TEST").forEach(userRepository::delete);
            organizationRepository.findByOrgType("BATCH_KEY_TEST").forEach(organizationRepository::delete);
        } catch (Exception e) {
            log.warn("清理测试数据失败", e);
        }
    }

    /**
     * 测试1: 用户公私钥生成和存储
     */
    @Test
    @Order(1)
    @DisplayName("测试用户公私钥生成和存储")
    public void testUserKeyGenerationAndStorage() {
        log.info("开始测试用户公私钥生成和存储");

        // Given - 创建带有密钥的用户
        UserEntity testUser = createTestUserEntityWithKeys();

        // Then - 验证密钥存储
        assertNotNull(testUser.getPublicKey(), "用户公钥应该被设置");
        assertNotNull(testUser.getPrivateKey(), "用户私钥应该被设置");
        assertEquals(testUserPublicKey, testUser.getPublicKey(), "公钥存储应该匹配");
        assertEquals(testUserPrivateKey, testUser.getPrivateKey(), "私钥存储应该匹配");

        // 验证密钥格式
//        assertTrue(testUser.getPublicKey().contains("BEGIN PUBLIC KEY"), "公钥应该包含正确的格式标识");
//        assertTrue(testUser.getPrivateKey().contains("BEGIN PRIVATE KEY"), "私钥应该包含正确的格式标识");

        // 验证密钥长度
        assertTrue(testUser.getPublicKey().length() > 200, "公钥长度应该合理");
        assertTrue(testUser.getPrivateKey().length() > 500, "私钥长度应该合理");

        log.info("用户公私钥生成和存储测试通过");
    }

    /**
     * 测试2: 组织公私钥生成和存储
     */
    @Test
    @Order(2)
    @DisplayName("测试组织公私钥生成和存储")
    public void testOrganizationKeyGenerationAndStorage() {
        log.info("开始测试组织公私钥生成和存储");

        // Given - 创建带有密钥的组织
        OrganizationEntity testOrg = createTestOrganizationEntityWithKeys();

        // Then - 验证密钥存储
        assertNotNull(testOrg.getPublicKey(), "组织公钥应该被设置");
        assertNotNull(testOrg.getPrivateKey(), "组织私钥应该被设置");
        assertEquals(testOrgPublicKey, testOrg.getPublicKey(), "组织公钥存储应该匹配");
        assertEquals(testOrgPrivateKey, testOrg.getPrivateKey(), "组织私钥存储应该匹配");

        // 验证密钥格式
//        assertTrue(testOrg.getPublicKey().contains("BEGIN PUBLIC KEY"), "组织公钥应该包含正确的格式标识");
//        assertTrue(testOrg.getPrivateKey().contains("BEGIN PRIVATE KEY"), "组织私钥应该包含正确的格式标识");

        // 验证密钥长度
        assertTrue(testOrg.getPublicKey().length() > 200, "组织公钥长度应该合理");
        assertTrue(testOrg.getPrivateKey().length() > 500, "组织私钥长度应该合理");

        log.info("组织公私钥生成和存储测试通过");
    }

    /**
     * 测试3: 通过公钥查询用户和组织
     */
    @Test
    @Order(3)
    @DisplayName("测试通过公钥查询用户和组织")
    public void testQueryByPublicKey() {
        log.info("开始测试通过公钥查询用户和组织");

        // Given - 创建带有密钥的用户和组织
        UserEntity testUser = createTestUserEntityWithKeys();
        OrganizationEntity testOrg = createTestOrganizationEntityWithKeys();

        // When & Then - 通过公钥查询用户
        Optional<UserEntity> userByPublicKey = userRepository.findByPublicKey(testUserPublicKey);
        assertTrue(userByPublicKey.isPresent(), "通过公钥查询用户应该成功");
        assertEquals(TEST_USER_NAME, userByPublicKey.get().getUserName(), "查询到的用户名应该匹配");

        // When & Then - 通过公钥查询组织
        Optional<OrganizationEntity> orgByPublicKey = organizationRepository.findByPublicKey(testOrgPublicKey);
        assertTrue(orgByPublicKey.isPresent(), "通过公钥查询组织应该成功");
        assertEquals(TEST_ORG_NAME, orgByPublicKey.get().getOrgName(), "查询到的组织名应该匹配");

        // 测试不存在的公钥查询
        Optional<UserEntity> nonExistentUser = userRepository.findByPublicKey("non_existent_public_key");
        assertFalse(nonExistentUser.isPresent(), "不存在的公钥查询应该返回空");

        Optional<OrganizationEntity> nonExistentOrg = organizationRepository.findByPublicKey("non_existent_public_key");
        assertFalse(nonExistentOrg.isPresent(), "不存在的公钥查询应该返回空");

        log.info("通过公钥查询用户和组织测试通过");
    }

    /**
     * 测试4: 密钥更新功能
     */
    @Test
    @Order(4)
    @DisplayName("测试密钥更新功能")
    public void testKeyUpdate() {
        log.info("开始测试密钥更新功能");

        // Given - 创建带有密钥的用户和组织
        UserEntity testUser = createTestUserEntityWithKeys();
        OrganizationEntity testOrg = createTestOrganizationEntityWithKeys();

        try {
            // When - 更新用户密钥
            RsaKeysResponse newUserKeys = SecurityUtils.generateKeyPair();
            String oldUserPublicKey = testUser.getPublicKey();
            String oldUserPrivateKey = testUser.getPrivateKey();

            testUser.setPublicKey(newUserKeys.getPubKey());
            testUser.setPrivateKey(newUserKeys.getPriKey());
            UserEntity updatedUser = userRepository.save(testUser);

            // Then - 验证用户密钥更新
            assertEquals(newUserKeys.getPubKey(), updatedUser.getPublicKey(), "用户公钥更新应该成功");
            assertEquals(newUserKeys.getPriKey(), updatedUser.getPrivateKey(), "用户私钥更新应该成功");
            assertNotEquals(oldUserPublicKey, updatedUser.getPublicKey(), "新公钥应该与旧公钥不同");
            assertNotEquals(oldUserPrivateKey, updatedUser.getPrivateKey(), "新私钥应该与旧私钥不同");

            // When - 更新组织密钥
            RsaKeysResponse newOrgKeys = SecurityUtils.generateKeyPair();
            String oldOrgPublicKey = testOrg.getPublicKey();
            String oldOrgPrivateKey = testOrg.getPrivateKey();

            testOrg.setPublicKey(newOrgKeys.getPubKey());
            testOrg.setPrivateKey(newOrgKeys.getPriKey());
            OrganizationEntity updatedOrg = organizationRepository.save(testOrg);

            // Then - 验证组织密钥更新
            assertEquals(newOrgKeys.getPubKey(), updatedOrg.getPublicKey(), "组织公钥更新应该成功");
            assertEquals(newOrgKeys.getPriKey(), updatedOrg.getPrivateKey(), "组织私钥更新应该成功");
            assertNotEquals(oldOrgPublicKey, updatedOrg.getPublicKey(), "新组织公钥应该与旧公钥不同");
            assertNotEquals(oldOrgPrivateKey, updatedOrg.getPrivateKey(), "新组织私钥应该与旧私钥不同");

        } catch (NoSuchAlgorithmException e) {
            fail("生成新密钥对失败: " + e.getMessage());
        }

        log.info("密钥更新功能测试通过");
    }

    /**
     * 测试5: DABE基础字段存储（仅测试数据库层面）
     * 注意：真正的DABE属性管理需要通过链码服务，这里只测试基础的数据存储能力
     */
    @Test
    @Order(5)
    @DisplayName("测试DABE基础字段存储")
    public void testDABEBasicFieldsStorage() {
        log.info("开始测试DABE基础字段存储");

        // Given - 创建用户和组织
        UserEntity testUser = createTestUserEntityWithKeys();
        OrganizationEntity testOrg = createTestOrganizationEntityWithKeys();

        // 设置DABE基础字段（仅测试数据库存储能力）
        String testEggAlpha = "test_egg_alpha_value";
        String testAlpha = "test_alpha_value";
        String testGAlpha = "test_g_alpha_value";
        String testUpk = "test_upk_value";

        // When - 更新用户DABE基础字段
        testUser.setEggAlpha(testEggAlpha);
        testUser.setAlpha(testAlpha);
        testUser.setGAlpha(testGAlpha);
        testUser.setUpk(testUpk);
        UserEntity updatedUser = userRepository.save(testUser);

        // Then - 验证用户DABE字段存储
        assertEquals(testEggAlpha, updatedUser.getEggAlpha(), "用户EggAlpha应该匹配");
        assertEquals(testAlpha, updatedUser.getAlpha(), "用户Alpha应该匹配");
        assertEquals(testGAlpha, updatedUser.getGAlpha(), "用户GAlpha应该匹配");
        assertEquals(testUpk, updatedUser.getUpk(), "用户UPK应该匹配");

        // When - 更新组织DABE基础字段
        testOrg.setEggAlpha(testEggAlpha);
        testOrg.setAlpha(testAlpha);
        testOrg.setGAlpha(testGAlpha);
        testOrg.setUpk(testUpk);
        OrganizationEntity updatedOrg = organizationRepository.save(testOrg);

        // Then - 验证组织DABE字段存储
        assertEquals(testEggAlpha, updatedOrg.getEggAlpha(), "组织EggAlpha应该匹配");
        assertEquals(testAlpha, updatedOrg.getAlpha(), "组织Alpha应该匹配");
        assertEquals(testGAlpha, updatedOrg.getGAlpha(), "组织GAlpha应该匹配");
        assertEquals(testUpk, updatedOrg.getUpk(), "组织UPK应该匹配");

        // 验证数据持久化一致性
        Optional<UserEntity> reloadedUser = userRepository.findByUserName(testUser.getUserName());
        Optional<OrganizationEntity> reloadedOrg = organizationRepository.findByOrgName(testOrg.getOrgName());

        assertTrue(reloadedUser.isPresent(), "重新加载的用户应该存在");
        assertTrue(reloadedOrg.isPresent(), "重新加载的组织应该存在");

        assertEquals(testEggAlpha, reloadedUser.get().getEggAlpha(), "用户EggAlpha持久化应该一致");
        assertEquals(testEggAlpha, reloadedOrg.get().getEggAlpha(), "组织EggAlpha持久化应该一致");

        log.info("DABE基础字段存储测试通过");
        log.warn("注意：此测试仅验证数据库存储，真正的DABE属性管理需要通过链码服务");
    }

    /**
     * 测试6: 密钥安全性验证
     */
    @Test
    @Order(6)
    @DisplayName("测试密钥安全性验证")
    public void testKeySecurityValidation() {
        log.info("开始测试密钥安全性验证");

        // 测试密钥唯一性
        testKeyUniqueness();

        // 测试密钥完整性
        UserEntity testUser = createTestUserEntityWithKeys();
        OrganizationEntity testOrg = createTestOrganizationEntityWithKeys();
        testKeyIntegrity(testUser, testOrg);

        // 测试密钥格式验证
//        testKeyFormatValidation(testUser, testOrg);

        log.info("密钥安全性验证测试通过");
    }

    /**
     * 测试7: 批量密钥操作
     */
    @Test
    @Order(7)
    @DisplayName("测试批量密钥操作")
    public void testBatchKeyOperations() {
        log.info("开始测试批量密钥操作");

        int batchSize = 5;
        List<UserEntity> users = new ArrayList<>();
        List<OrganizationEntity> orgs = new ArrayList<>();

        try {
            long startTime = System.currentTimeMillis();

            // 批量创建用户和组织的密钥
            for (int i = 0; i < batchSize; i++) {
                RsaKeysResponse userKeys = SecurityUtils.generateKeyPair();
                RsaKeysResponse orgKeys = SecurityUtils.generateKeyPair();

                UserEntity user = UserEntity.builder()
                        .userName("batch_key_user_" + i)
                        .name("批量密钥用户" + i)
                        .password(SecurityUtils.md5(TEST_PASSWORD))
                        .userType("BATCH_KEY_TEST")
                        .channel(TEST_CHANNEL)
                        .publicKey(userKeys.getPubKey())
                        .privateKey(userKeys.getPriKey())
                        .upk("batch_upk_" + i)
                        .build();
                users.add(userRepository.save(user));

                OrganizationEntity org = OrganizationEntity.builder()
                        .orgName("batch_key_org_" + i)
                        .displayName("批量密钥组织" + i)
                        .orgType("BATCH_KEY_TEST")
                        .status("ACTIVE")
                        .publicKey(orgKeys.getPubKey())
                        .privateKey(orgKeys.getPriKey())
                        .upk("batch_org_upk_" + i)
                        .build();
                orgs.add(organizationRepository.save(org));
            }

            long createTime = System.currentTimeMillis();
            log.info("批量创建{}个用户和组织密钥耗时: {}ms", batchSize, createTime - startTime);

            // 验证批量创建的结果
            assertEquals(batchSize, users.size(), "应该创建指定数量的用户");
            assertEquals(batchSize, orgs.size(), "应该创建指定数量的组织");

            // 验证所有密钥都不为空且唯一
            Set<String> userPublicKeys = new HashSet<>();
            Set<String> orgPublicKeys = new HashSet<>();

            for (UserEntity user : users) {
                assertNotNull(user.getPublicKey(), "用户公钥不应为空");
                assertNotNull(user.getPrivateKey(), "用户私钥不应为空");
                assertTrue(userPublicKeys.add(user.getPublicKey()), "用户公钥应该唯一");
            }

            for (OrganizationEntity org : orgs) {
                assertNotNull(org.getPublicKey(), "组织公钥不应为空");
                assertNotNull(org.getPrivateKey(), "组织私钥不应为空");
                assertTrue(orgPublicKeys.add(org.getPublicKey()), "组织公钥应该唯一");
            }

            long verifyTime = System.currentTimeMillis();
            log.info("批量验证耗时: {}ms", verifyTime - createTime);

            // 清理批量测试数据
            users.forEach(userRepository::delete);
            orgs.forEach(organizationRepository::delete);

            long totalTime = System.currentTimeMillis() - startTime;
            log.info("批量密钥操作总耗时: {}ms", totalTime);

        } catch (NoSuchAlgorithmException e) {
            fail("批量密钥操作测试失败: " + e.getMessage());
        }

        log.info("批量密钥操作测试通过");
    }

    /**
     * 测试8: 异常情况处理
     */
    @Test
    @Order(8)
    @DisplayName("测试异常情况处理")
    public void testExceptionHandling() {
        log.info("开始测试异常情况处理");

        // 测试空密钥处理
        UserEntity userWithNullKeys = UserEntity.builder()
                .userName("null_key_user")
                .name("空密钥用户")
                .userType(TEST_USER_TYPE)
                .channel(TEST_CHANNEL)
                .publicKey(null)
                .privateKey(null)
                .build();
        UserEntity savedUser = userRepository.save(userWithNullKeys);
        assertNull(savedUser.getPublicKey(), "空公钥应该被正确保存");
        assertNull(savedUser.getPrivateKey(), "空私钥应该被正确保存");

        // 测试无效密钥格式处理
        UserEntity userWithInvalidKeys = UserEntity.builder()
                .userName("invalid_key_user")
                .name("无效密钥用户")
                .userType(TEST_USER_TYPE)
                .channel(TEST_CHANNEL)
                .publicKey("invalid_public_key")
                .privateKey("invalid_private_key")
                .build();
        UserEntity savedInvalidUser = userRepository.save(userWithInvalidKeys);
        assertEquals("invalid_public_key", savedInvalidUser.getPublicKey(), "无效公钥应该被保存");
        assertEquals("invalid_private_key", savedInvalidUser.getPrivateKey(), "无效私钥应该被保存");

        // 清理测试数据
        userRepository.delete(savedUser);
        userRepository.delete(savedInvalidUser);

        log.info("异常情况处理测试通过");
    }

    // ==================== 辅助方法 ====================

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
                .publicKey(testUserPublicKey)
                .privateKey(testUserPrivateKey)
                .upk("test_user_upk_value")
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    /**
     * 创建带有密钥的测试组织实体
     */
    private OrganizationEntity createTestOrganizationEntityWithKeys() {
        OrganizationEntity org = OrganizationEntity.builder()
                .orgName(TEST_ORG_NAME)
                .displayName(TEST_ORG_DISPLAY_NAME)
                .description("测试组织描述")
                .orgType(TEST_ORG_TYPE)
                .status("ACTIVE")
                .publicKey(testOrgPublicKey)
                .privateKey(testOrgPrivateKey)
                .upk("test_org_upk_value")
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
        return organizationRepository.save(org);
    }

    /**
     * 测试密钥唯一性
     */
    private void testKeyUniqueness() {
        try {
            Set<String> publicKeys = new HashSet<>();
            Set<String> privateKeys = new HashSet<>();

            for (int i = 0; i < 5; i++) {
                RsaKeysResponse keys = SecurityUtils.generateKeyPair();
                assertTrue(publicKeys.add(keys.getPubKey()), "公钥应该是唯一的");
                assertTrue(privateKeys.add(keys.getPriKey()), "私钥应该是唯一的");
            }
        } catch (NoSuchAlgorithmException e) {
            fail("密钥唯一性测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试密钥完整性
     */
    private void testKeyIntegrity(UserEntity user, OrganizationEntity org) {
        // 验证保存和读取后密钥的完整性
        String originalUserPublicKey = user.getPublicKey();
        String originalUserPrivateKey = user.getPrivateKey();
        String originalOrgPublicKey = org.getPublicKey();
        String originalOrgPrivateKey = org.getPrivateKey();

        // 重新从数据库读取
        Optional<UserEntity> reloadedUser = userRepository.findByUserName(user.getUserName());
        Optional<OrganizationEntity> reloadedOrg = organizationRepository.findByOrgName(org.getOrgName());

        assertTrue(reloadedUser.isPresent(), "重新加载的用户应该存在");
        assertTrue(reloadedOrg.isPresent(), "重新加载的组织应该存在");

        assertEquals(originalUserPublicKey, reloadedUser.get().getPublicKey(), "用户公钥完整性验证失败");
        assertEquals(originalUserPrivateKey, reloadedUser.get().getPrivateKey(), "用户私钥完整性验证失败");
        assertEquals(originalOrgPublicKey, reloadedOrg.get().getPublicKey(), "组织公钥完整性验证失败");
        assertEquals(originalOrgPrivateKey, reloadedOrg.get().getPrivateKey(), "组织私钥完整性验证失败");
    }

    /**
     * 测试密钥格式验证
     */
    private void testKeyFormatValidation(UserEntity user, OrganizationEntity org) {
        // 验证用户密钥格式
        String userPublicKey = user.getPublicKey();
        String userPrivateKey = user.getPrivateKey();

        assertTrue(userPublicKey.startsWith("-----BEGIN PUBLIC KEY-----"), "用户公钥应该有正确的开始标识");
        assertTrue(userPublicKey.endsWith("-----END PUBLIC KEY-----"), "用户公钥应该有正确的结束标识");
        assertTrue(userPrivateKey.startsWith("-----BEGIN PRIVATE KEY-----"), "用户私钥应该有正确的开始标识");
        assertTrue(userPrivateKey.endsWith("-----END PRIVATE KEY-----"), "用户私钥应该有正确的结束标识");

        // 验证组织密钥格式
        String orgPublicKey = org.getPublicKey();
        String orgPrivateKey = org.getPrivateKey();

        assertTrue(orgPublicKey.startsWith("-----BEGIN PUBLIC KEY-----"), "组织公钥应该有正确的开始标识");
        assertTrue(orgPublicKey.endsWith("-----END PUBLIC KEY-----"), "组织公钥应该有正确的结束标识");
        assertTrue(orgPrivateKey.startsWith("-----BEGIN PRIVATE KEY-----"), "组织私钥应该有正确的开始标识");
        assertTrue(orgPrivateKey.endsWith("-----END PRIVATE KEY-----"), "组织私钥应该有正确的结束标识");
    }
}

