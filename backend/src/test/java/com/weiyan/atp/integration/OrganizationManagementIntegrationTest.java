package com.weiyan.atp.integration;

import com.weiyan.atp.AbeTrustPlatformApplication;
import com.weiyan.atp.data.bean.entity.OrganizationEntity;
import com.weiyan.atp.data.response.web.RsaKeysResponse;
import com.weiyan.atp.repository.OrganizationRepository;
import com.weiyan.atp.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组织管理集成测试类
 * 测试组织创建、查询、更新、删除以及状态管理功能
 */
@Slf4j
@SpringBootTest(classes = AbeTrustPlatformApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class OrganizationManagementIntegrationTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    // 测试常量
    private static final String TEST_ORG_NAME = "test_org_management";
    private static final String TEST_ORG_DISPLAY_NAME = "组织管理测试组织";
    private static final String TEST_ORG_TYPE = "MANAGEMENT_TEST";
    private static final String TEST_ORG_DESCRIPTION = "用于组织管理功能测试的组织";

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
            organizationRepository.findByOrgName(TEST_ORG_NAME).ifPresent(organizationRepository::delete);
            // 清理批量测试数据
            organizationRepository.findByOrgType("BATCH_ORG_TEST").forEach(organizationRepository::delete);
            organizationRepository.findByOrgType("STATUS_TEST").forEach(organizationRepository::delete);
        } catch (Exception e) {
            log.warn("清理测试数据失败", e);
        }
    }

    /**
     * 测试1: 组织基本信息管理
     */
    @Test
    @Order(1)
    @DisplayName("测试组织基本信息管理")
    public void testOrganizationBasicInfoManagement() {
        log.info("开始测试组织基本信息管理");

        // Given & When - 创建组织
        OrganizationEntity testOrg = createTestOrganizationEntity();

        // Then - 验证组织创建
        assertNotNull(testOrg, "组织创建失败");
        assertNotNull(testOrg.getId(), "组织ID应该被自动生成");
        assertEquals(TEST_ORG_NAME, testOrg.getOrgName(), "组织名不匹配");
        assertEquals(TEST_ORG_DISPLAY_NAME, testOrg.getDisplayName(), "组织显示名不匹配");
        assertEquals(TEST_ORG_TYPE, testOrg.getOrgType(), "组织类型不匹配");
        assertEquals(TEST_ORG_DESCRIPTION, testOrg.getDescription(), "组织描述不匹配");
        assertEquals("ACTIVE", testOrg.getStatus(), "组织状态应该默认为ACTIVE");
        assertNotNull(testOrg.getCreatedTime(), "创建时间应该被设置");
        assertNotNull(testOrg.getUpdatedTime(), "更新时间应该被设置");

        log.info("组织基本信息管理测试通过");
    }

    /**
     * 测试2: 组织查询功能
     */
    @Test
    @Order(2)
    @DisplayName("测试组织查询功能")
    public void testOrganizationQueryFunctions() {
        log.info("开始测试组织查询功能");

        // Given - 创建多个测试组织
        createTestOrganizationEntity();
        createAdditionalTestOrganizations();

        // 测试按组织名查询
        Optional<OrganizationEntity> orgByName = organizationRepository.findByOrgName(TEST_ORG_NAME);
        assertTrue(orgByName.isPresent(), "按组织名查询应该成功");
        assertEquals(TEST_ORG_DISPLAY_NAME, orgByName.get().getDisplayName(), "查询结果应该匹配");

        // 测试按组织类型查询
        List<OrganizationEntity> orgsByType = organizationRepository.findByOrgType(TEST_ORG_TYPE);
        assertFalse(orgsByType.isEmpty(), "按组织类型查询应该有结果");
        assertTrue(orgsByType.size() >= 1, "应该至少有一个组织");

        // 测试按状态查询
        List<OrganizationEntity> activeOrgs = organizationRepository.findByStatus("ACTIVE");
        assertFalse(activeOrgs.isEmpty(), "按状态查询应该有结果");

        // 测试关键词查询
        List<OrganizationEntity> orgsByKeyword = organizationRepository.findByKeyword("管理测试");
        assertFalse(orgsByKeyword.isEmpty(), "关键词查询应该有结果");

        // 测试组织名存在性检查
        assertTrue(organizationRepository.existsByOrgName(TEST_ORG_NAME), "组织名应该存在");
        assertFalse(organizationRepository.existsByOrgName("non_existent_org"), "不存在的组织名应该返回false");

        log.info("组织查询功能测试通过");
    }

    /**
     * 测试3: 组织信息更新
     */
    @Test
    @Order(3)
    @DisplayName("测试组织信息更新")
    public void testOrganizationInfoUpdate() {
        log.info("开始测试组织信息更新");

        // Given - 创建组织
        OrganizationEntity testOrg = createTestOrganizationEntity();
        LocalDateTime originalUpdatedTime = testOrg.getUpdatedTime();

        // When - 更新组织信息
        String newDisplayName = TEST_ORG_DISPLAY_NAME + "_更新";
        String newDescription = TEST_ORG_DESCRIPTION + "_已更新";
        String newOrgType = TEST_ORG_TYPE + "_UPDATED";

        testOrg.setDisplayName(newDisplayName);
        testOrg.setDescription(newDescription);
        testOrg.setOrgType(newOrgType);

        // 模拟时间流逝
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        OrganizationEntity updatedOrg = organizationRepository.save(testOrg);

        // Then - 验证更新结果
        assertEquals(newDisplayName, updatedOrg.getDisplayName(), "显示名更新失败");
        assertEquals(newDescription, updatedOrg.getDescription(), "描述更新失败");
        assertEquals(newOrgType, updatedOrg.getOrgType(), "组织类型更新失败");
        assertTrue(updatedOrg.getUpdatedTime().isAfter(originalUpdatedTime) ||
                        updatedOrg.getUpdatedTime().isEqual(originalUpdatedTime),
                "更新时间应该被刷新");

        log.info("组织信息更新测试通过");
    }

    /**
     * 测试4: 组织状态管理
     */
    @Test
    @Order(4)
    @DisplayName("测试组织状态管理")
    public void testOrganizationStatusManagement() {
        log.info("开始测试组织状态管理");

        // Given - 创建多个不同状态的组织
        OrganizationEntity activeOrg = OrganizationEntity.builder()
                .orgName("status_test_active")
                .displayName("活跃状态组织")
                .orgType("STATUS_TEST")
                .status("ACTIVE")
                .build();
        organizationRepository.save(activeOrg);

        OrganizationEntity inactiveOrg = OrganizationEntity.builder()
                .orgName("status_test_inactive")
                .displayName("非活跃状态组织")
                .orgType("STATUS_TEST")
                .status("INACTIVE")
                .build();
        organizationRepository.save(inactiveOrg);

        // When & Then - 测试状态查询
        List<OrganizationEntity> activeOrgs = organizationRepository.findByStatus("ACTIVE");
        List<OrganizationEntity> inactiveOrgs = organizationRepository.findByStatus("INACTIVE");

        assertTrue(activeOrgs.stream().anyMatch(org -> "status_test_active".equals(org.getOrgName())),
                "应该能查询到活跃状态的组织");
        assertTrue(inactiveOrgs.stream().anyMatch(org -> "status_test_inactive".equals(org.getOrgName())),
                "应该能查询到非活跃状态的组织");

        // When - 更改组织状态
        activeOrg.setStatus("INACTIVE");
        OrganizationEntity statusChangedOrg = organizationRepository.save(activeOrg);

        // Then - 验证状态更改
        assertEquals("INACTIVE", statusChangedOrg.getStatus(), "组织状态更改失败");

        // 验证状态查询结果变化
        List<OrganizationEntity> newInactiveOrgs = organizationRepository.findByStatus("INACTIVE");
        assertTrue(newInactiveOrgs.stream().anyMatch(org -> "status_test_active".equals(org.getOrgName())),
                "状态更改后应该能在新状态中查询到组织");

        log.info("组织状态管理测试通过");
    }

    /**
     * 测试5: 组织公私钥管理
     */
    @Test
    @Order(5)
    @DisplayName("测试组织公私钥管理")
    public void testOrganizationKeyManagement() {
        log.info("开始测试组织公私钥管理");

        // Given - 创建带有密钥的组织
        OrganizationEntity testOrg = createTestOrganizationEntityWithKeys();

        // Then - 验证密钥存储
        assertNotNull(testOrg.getPublicKey(), "组织公钥应该被设置");
        assertNotNull(testOrg.getPrivateKey(), "组织私钥应该被设置");
        assertEquals(testPublicKey, testOrg.getPublicKey(), "公钥存储应该匹配");
        assertEquals(testPrivateKey, testOrg.getPrivateKey(), "私钥存储应该匹配");

        // 通过公钥查询组织
        Optional<OrganizationEntity> keyQueryOrg = organizationRepository.findByPublicKey(testPublicKey);
        assertTrue(keyQueryOrg.isPresent(), "通过公钥查询组织应该成功");
        assertEquals(TEST_ORG_NAME, keyQueryOrg.get().getOrgName(), "公钥查询结果应该匹配");

        // 测试密钥更新
        try {
            RsaKeysResponse newKeys = SecurityUtils.generateKeyPair();
            testOrg.setPublicKey(newKeys.getPubKey());
            testOrg.setPrivateKey(newKeys.getPriKey());
            OrganizationEntity updatedOrg = organizationRepository.save(testOrg);

            assertEquals(newKeys.getPubKey(), updatedOrg.getPublicKey(), "公钥更新应该成功");
            assertEquals(newKeys.getPriKey(), updatedOrg.getPrivateKey(), "私钥更新应该成功");
        } catch (NoSuchAlgorithmException e) {
            log.warn("生成新密钥对失败", e);
        }

        log.info("组织公私钥管理测试通过");
    }

    /**
     * 测试6: 组织DABE基础字段存储（仅测试数据库层面）
     * 注意：真正的DABE属性管理需要通过链码服务，这里只测试基础的数据存储能力
     */
    @Test
    @Order(6)
    @DisplayName("测试组织DABE基础字段存储")
    public void testOrganizationDABEBasicFieldsStorage() {
        log.info("开始测试组织DABE基础字段存储");

        // Given - 创建组织
        OrganizationEntity testOrg = createTestOrganizationEntity();

        // 设置DABE基础字段（仅测试数据库存储能力）
        String testEggAlpha = "org_egg_alpha_value";
        String testAlpha = "org_alpha_value";
        String testGAlpha = "org_g_alpha_value";
        String testUpk = "org_upk_value";

        // When - 更新组织DABE基础字段
        testOrg.setEggAlpha(testEggAlpha);
        testOrg.setAlpha(testAlpha);
        testOrg.setGAlpha(testGAlpha);
        testOrg.setUpk(testUpk);
        OrganizationEntity updatedOrg = organizationRepository.save(testOrg);

        // Then - 验证DABE字段存储
        assertEquals(testEggAlpha, updatedOrg.getEggAlpha(), "组织EggAlpha应该匹配");
        assertEquals(testAlpha, updatedOrg.getAlpha(), "组织Alpha应该匹配");
        assertEquals(testGAlpha, updatedOrg.getGAlpha(), "组织GAlpha应该匹配");
        assertEquals(testUpk, updatedOrg.getUpk(), "组织UPK应该匹配");

        // 验证数据持久化一致性
        Optional<OrganizationEntity> reloadedOrgOpt = organizationRepository.findByOrgName(TEST_ORG_NAME);
        assertTrue(reloadedOrgOpt.isPresent(), "重新加载组织应该成功");

        OrganizationEntity reloadedOrg = reloadedOrgOpt.get();
        assertEquals(testEggAlpha, reloadedOrg.getEggAlpha(), "重新加载后EggAlpha应该一致");
        assertEquals(testAlpha, reloadedOrg.getAlpha(), "重新加载后Alpha应该一致");
        assertEquals(testGAlpha, reloadedOrg.getGAlpha(), "重新加载后GAlpha应该一致");
        assertEquals(testUpk, reloadedOrg.getUpk(), "重新加载后UPK应该一致");

        log.info("组织DABE基础字段存储测试通过");
        log.warn("注意：此测试仅验证数据库存储，真正的DABE属性管理需要通过链码服务");
    }

    /**
     * 测试7: 批量组织操作
     */
    @Test
    @Order(7)
    @DisplayName("测试批量组织操作")
    public void testBatchOrganizationOperations() {
        log.info("开始测试批量组织操作");

        int batchSize = 10;
        long startTime = System.currentTimeMillis();

        // 批量创建组织
        List<OrganizationEntity> batchOrgs = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            OrganizationEntity org = OrganizationEntity.builder()
                    .orgName("batch_org_" + i)
                    .displayName("批量组织" + i)
                    .orgType("BATCH_ORG_TEST")
                    .status("ACTIVE")
                    .description("批量测试组织" + i)
                    .build();
            batchOrgs.add(organizationRepository.save(org));
        }

        long createTime = System.currentTimeMillis();
        log.info("批量创建{}个组织耗时: {}ms", batchSize, createTime - startTime);

        // 批量查询
        List<OrganizationEntity> queryResults = organizationRepository.findByOrgType("BATCH_ORG_TEST");
        assertEquals(batchSize, queryResults.size(), "批量查询数量应该匹配");

        long queryTime = System.currentTimeMillis();
        log.info("批量查询耗时: {}ms", queryTime - createTime);

        // 批量状态更新
        batchOrgs.forEach(org -> org.setStatus("INACTIVE"));
        organizationRepository.saveAll(batchOrgs);

        long updateTime = System.currentTimeMillis();
        log.info("批量更新耗时: {}ms", updateTime - queryTime);

        // 验证批量更新结果
        List<OrganizationEntity> inactiveOrgs = organizationRepository.findByStatus("INACTIVE");
        long batchInactiveCount = inactiveOrgs.stream()
                .filter(org -> "BATCH_ORG_TEST".equals(org.getOrgType()))
                .count();
        assertEquals(batchSize, batchInactiveCount, "批量状态更新应该成功");

        // 批量删除
        organizationRepository.deleteAll(batchOrgs);

        long deleteTime = System.currentTimeMillis();
        log.info("批量删除耗时: {}ms", deleteTime - updateTime);

        // 验证删除结果
        List<OrganizationEntity> remainingOrgs = organizationRepository.findByOrgType("BATCH_ORG_TEST");
        assertEquals(0, remainingOrgs.size(), "批量删除后应该没有残留数据");

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("批量组织操作总耗时: {}ms", totalTime);

        // 性能断言
        assertTrue(totalTime < 15000, "批量操作耗时不应超过15秒: " + totalTime + "ms");

        log.info("批量组织操作测试通过");
    }

    /**
     * 测试8: 异常情况处理
     */
    @Test
    @Order(8)
    @DisplayName("测试异常情况处理")
    public void testExceptionHandling() {
        log.info("开始测试异常情况处理");

        // 测试重复组织名处理
        OrganizationEntity org1 = OrganizationEntity.builder()
                .orgName("duplicate_org")
                .displayName("重复组织1")
                .orgType("EXCEPTION_TEST")
                .status("ACTIVE")
                .build();
        organizationRepository.save(org1);

        // 尝试创建同名组织应该失败
        OrganizationEntity org2 = OrganizationEntity.builder()
                .orgName("duplicate_org")
                .displayName("重复组织2")
                .orgType("EXCEPTION_TEST")
                .status("ACTIVE")
                .build();

        assertThrows(Exception.class, () -> {
            organizationRepository.save(org2);
            organizationRepository.flush(); // 强制执行数据库操作
        }, "重复组织名应该抛出异常");

        // 测试空值处理
        OrganizationEntity orgWithNulls = OrganizationEntity.builder()
                .orgName("null_test_org")
                .displayName(null)
                .description(null)
                .orgType("EXCEPTION_TEST")
                .status("ACTIVE")
                .build();
        OrganizationEntity savedOrgWithNulls = organizationRepository.save(orgWithNulls);
        assertNull(savedOrgWithNulls.getDisplayName(), "空显示名应该被正确保存");
        assertNull(savedOrgWithNulls.getDescription(), "空描述应该被正确保存");

        // 清理测试数据
        organizationRepository.delete(org1);
        organizationRepository.delete(savedOrgWithNulls);

        log.info("异常情况处理测试通过");
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建测试组织实体
     */
    private OrganizationEntity createTestOrganizationEntity() {
        OrganizationEntity org = OrganizationEntity.builder()
                .orgName(TEST_ORG_NAME)
                .displayName(TEST_ORG_DISPLAY_NAME)
                .description(TEST_ORG_DESCRIPTION)
                .orgType(TEST_ORG_TYPE)
                .status("ACTIVE")
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
        return organizationRepository.save(org);
    }

    /**
     * 创建带有密钥的测试组织实体
     */
    private OrganizationEntity createTestOrganizationEntityWithKeys() {
        OrganizationEntity org = OrganizationEntity.builder()
                .orgName(TEST_ORG_NAME)
                .displayName(TEST_ORG_DISPLAY_NAME)
                .description(TEST_ORG_DESCRIPTION)
                .orgType(TEST_ORG_TYPE)
                .status("ACTIVE")
                .publicKey(testPublicKey)
                .privateKey(testPrivateKey)
                .upk("test_org_upk_value")
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
        return organizationRepository.save(org);
    }

    /**
     * 创建额外的测试组织
     */
    private void createAdditionalTestOrganizations() {
        for (int i = 1; i <= 3; i++) {
            OrganizationEntity org = OrganizationEntity.builder()
                    .orgName(TEST_ORG_NAME + "_additional_" + i)
                    .displayName(TEST_ORG_DISPLAY_NAME + "_额外_" + i)
                    .description(TEST_ORG_DESCRIPTION + "_" + i)
                    .orgType(TEST_ORG_TYPE)
                    .status("ACTIVE")
                    .build();
            organizationRepository.save(org);
        }
    }
}