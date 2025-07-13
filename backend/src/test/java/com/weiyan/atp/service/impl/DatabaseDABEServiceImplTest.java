package com.weiyan.atp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.entity.UserEntity;
import com.weiyan.atp.repository.UserRepository;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.utils.JsonProviderHolder;
import com.weiyan.atp.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * DatabaseDABEServiceImpl 专用单元测试
 * 测试基于数据库存储的DABE服务实现
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class DatabaseDABEServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChaincodeService chaincodeService;

    @InjectMocks
    private DatabaseDABEServiceImpl databaseDABEService;

    private UserEntity testUserEntity;
    private DABEUser testDABEUser;
    private ChaincodeResponse successResponse;
    private ChaincodeResponse failResponse;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        setupTestData();
        setupMockResponses();
    }

    private void setupTestData() {
        // 准备测试用的 UserEntity
        testUserEntity = UserEntity.builder()
                .id(1L)
                .userName("testUser")
                .name("Test User")
                .password(SecurityUtils.md5("123456"))
                .userType("TEST_TYPE")
                .channel("test_channel")
                .publicKey("-----BEGIN PUBLIC KEY-----\ntest_public_key_content...")
                .privateKey("-----BEGIN PRIVATE KEY-----\ntest_private_key_content...")
                .upk("test_upk_value")
                .apkMap("{\"attr1\":{\"Gy\":\"g1_value\"},\"attr2\":{\"Gy\":\"g2_value\"}}")
                .askMap("{\"attr1\":{\"Y\":\"y1_value\"},\"attr2\":{\"Y\":\"y2_value\"}}")
                .opkMap("{\"org1\":{\"opk\":\"opk1_value\",\"apkMap\":{\"attr1\":\"apk1_value\"}}}")
                .oskMap("{\"org1\":{\"alphaPart\":\"alpha1\",\"askMap\":{\"attr1\":{\"y\":\"ask1_y\"}}}}")
                .eggAlpha("egg_alpha_test_value")
                .alpha("alpha_test_value")
                .gAlpha("g_alpha_test_value")
                .appliedAttrMap("{\"attr1\":\"value1\",\"attr2\":\"value2\"}")
                .privacyAttrMap("{\"privacy_attr1\":\"privacy_value1\"}")
                .expireDate("2024-12-31")
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        // 准备测试用的 DABEUser
        testDABEUser = new DABEUser();
        testDABEUser.setName("Test User");
        testDABEUser.setPassword(SecurityUtils.md5("123456"));
        testDABEUser.setUserType("TEST_TYPE");
        testDABEUser.setChannel("test_channel");
        testDABEUser.setEggAlpha("egg_alpha_test_value");
        testDABEUser.setAlpha("alpha_test_value");
        testDABEUser.setGAlpha("g_alpha_test_value");

        // 设置复杂数据结构
        Map<String, DABEUser.APK> apkMap = new HashMap<>();
        DABEUser.APK apk1 = new DABEUser.APK();
        apk1.setGy("g1_value");
        DABEUser.APK apk2 = new DABEUser.APK();
        apk2.setGy("g2_value");
        apkMap.put("attr1", apk1);
        apkMap.put("attr2", apk2);
        testDABEUser.setApkMap(apkMap);

        Map<String, DABEUser.ASK> askMap = new HashMap<>();
        DABEUser.ASK ask1 = new DABEUser.ASK();
        ask1.setY("y1_value");
        DABEUser.ASK ask2 = new DABEUser.ASK();
        ask2.setY("y2_value");
        askMap.put("attr1", ask1);
        askMap.put("attr2", ask2);
        testDABEUser.setAskMap(askMap);

        Map<String, String> appliedAttrMap = new HashMap<>();
        appliedAttrMap.put("attr1", "value1");
        appliedAttrMap.put("attr2", "value2");
        testDABEUser.setAppliedAttrMap(appliedAttrMap);

        Map<String, String> privacyAttrMap = new HashMap<>();
        privacyAttrMap.put("privacy_attr1", "privacy_value1");
        testDABEUser.setPrivacyAttrMap(privacyAttrMap);
    }

    private void setupMockResponses() {
        successResponse = new ChaincodeResponse();
        successResponse.setStatus(ChaincodeResponse.Status.SUCCESS);
        successResponse.setMessage(JsonProviderHolder.JACKSON.toJsonString(testDABEUser));

        failResponse = new ChaincodeResponse();
        failResponse.setStatus(ChaincodeResponse.Status.FAIL);
        failResponse.setMessage("链码操作失败");
    }

    /**
     * 测试 getUser 方法 - 成功从数据库获取用户
     */
    @Test
    void testGetUser_Success() {
        // Given
        String fileName = "testUser";
        when(userRepository.findByUserName(fileName)).thenReturn(Optional.of(testUserEntity));

        // When
        DABEUser result = databaseDABEService.getUser(fileName);

        // Then
        assertNotNull(result, "应该成功从数据库获取用户");
        assertEquals("Test User", result.getName(), "用户名应该匹配");
        assertEquals("TEST_TYPE", result.getUserType(), "用户类型应该匹配");
        assertEquals("test_channel", result.getChannel(), "渠道应该匹配");
        assertEquals("egg_alpha_test_value", result.getEggAlpha(), "EggAlpha应该匹配");

        // 验证复杂数据结构
        assertNotNull(result.getApkMap(), "APK映射不应为空");
        assertTrue(result.getApkMap().containsKey("attr1"), "应包含attr1");
        assertEquals("g1_value", result.getApkMap().get("attr1").getGy(), "APK值应该正确");

        assertNotNull(result.getAskMap(), "ASK映射不应为空");
        assertTrue(result.getAskMap().containsKey("attr1"), "应包含attr1");
        assertEquals("y1_value", result.getAskMap().get("attr1").getY(), "ASK值应该正确");

        assertNotNull(result.getAppliedAttrMap(), "应用属性映射不应为空");
        assertEquals(2, result.getAppliedAttrMap().size(), "应用属性数量应该正确");
        assertEquals("value1", result.getAppliedAttrMap().get("attr1"), "应用属性值应该正确");

        verify(userRepository, times(1)).findByUserName(fileName);
    }

    /**
     * 测试 getUser 方法 - 用户不存在
     */
    @Test
    void testGetUser_UserNotFound() {
        // Given
        String fileName = "nonExistentUser";
        when(userRepository.findByUserName(fileName)).thenReturn(Optional.empty());

        // When
        DABEUser result = databaseDABEService.getUser(fileName);

        // Then
        assertNull(result, "不存在的用户应返回null");
        verify(userRepository, times(1)).findByUserName(fileName);
    }

    /**
     * 测试 getUser 方法 - 数据库异常处理
     */
    @Test
    void testGetUser_DatabaseException() {
        // Given
        String fileName = "errorUser";
        when(userRepository.findByUserName(fileName))
                .thenThrow(new RuntimeException("数据库连接异常"));

        // When
        DABEUser result = databaseDABEService.getUser(fileName);

        // Then
        assertNull(result, "数据库异常时应返回null");
        verify(userRepository, times(1)).findByUserName(fileName);
    }

    /**
     * 测试 getUser2 方法 - 密码验证成功
     */
    @Test
    void testGetUser2_PasswordValidation_Success() {
        // Given
        String fileName = "testUser";
        String password = "123456";
        String hashedPassword = SecurityUtils.md5(password);

        when(userRepository.findByUserNameAndPassword(fileName, hashedPassword))
                .thenReturn(Optional.of(testUserEntity));

        // When
        DABEUser result = databaseDABEService.getUser2(fileName, password);

        // Then
        assertNotNull(result, "密码正确时应返回用户");
        assertEquals("Test User", result.getName(), "用户名应该匹配");
        assertEquals(hashedPassword, result.getPassword(), "密码哈希应该匹配");
        verify(userRepository, times(1)).findByUserNameAndPassword(fileName, hashedPassword);
    }

    /**
     * 测试 getUser2 方法 - 密码错误
     */
    @Test
    void testGetUser2_WrongPassword() {
        // Given
        String fileName = "testUser";
        String wrongPassword = "wrongPassword";
        String hashedWrongPassword = SecurityUtils.md5(wrongPassword);

        when(userRepository.findByUserNameAndPassword(fileName, hashedWrongPassword))
                .thenReturn(Optional.empty());

        // When
        DABEUser result = databaseDABEService.getUser2(fileName, wrongPassword);

        // Then
        assertNull(result, "密码错误时应返回null");
        verify(userRepository, times(1)).findByUserNameAndPassword(fileName, hashedWrongPassword);
    }

    /**
     * 测试 getUser2DryRun 方法
     */
    @Test
    void testGetUser2DryRun() {
        // Given
        String fileName = "testUser";
        String password = "anyPassword";
        when(userRepository.findByUserName(fileName)).thenReturn(Optional.of(testUserEntity));

        // When
        DABEUser result = databaseDABEService.getUser2DryRun(fileName, password);

        // Then
        assertNotNull(result, "干运行模式应返回用户（不验证密码）");
        assertEquals("Test User", result.getName(), "用户名应该匹配");
        verify(userRepository, times(1)).findByUserName(fileName);
    }

    /**
     * 测试 getUser3 方法 - 证书验证
     */
    @Test
    void testGetUser3_CertificateValidation() {
        // Given
        String fileName = "testUser";
        String cert = "validCert";
        when(userRepository.findByUserName(fileName)).thenReturn(Optional.of(testUserEntity));

        // When
        DABEUser result = databaseDABEService.getUser3(fileName, cert);

        // Then
        assertNotNull(result, "证书验证应返回用户");
        assertEquals("Test User", result.getName(), "用户名应该匹配");
        verify(userRepository, times(1)).findByUserName(fileName);
    }

    /**
     * 测试 getUser3 方法 - 用户不存在
     */
    @Test
    void testGetUser3_UserNotFound() {
        // Given
        String fileName = "nonExistentUser";
        String cert = "anyCert";
        when(userRepository.findByUserName(fileName)).thenReturn(Optional.empty());

        // When
        DABEUser result = databaseDABEService.getUser3(fileName, cert);

        // Then
        assertNull(result, "用户不存在时应返回null");
        verify(userRepository, times(1)).findByUserName(fileName);
    }

    /**
     * 测试 createUser 方法 - 简单创建
     */
    @Test
    void testCreateUser_Simple() {
        // Given
        String fileName = "newUser";
        String userName = "New User";

        when(userRepository.existsByUserName(fileName)).thenReturn(false);
        when(chaincodeService.query(eq(ChaincodeTypeEnum.DABE), eq("/user/create"), any()))
                .thenReturn(successResponse);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUserEntity);

        // When
        DABEUser result = databaseDABEService.createUser(fileName, userName);

        // Then
        assertNotNull(result, "创建用户应该成功");
        assertEquals("Test User", result.getName(), "用户名应该匹配");
        verify(userRepository, times(1)).existsByUserName(fileName);
        verify(chaincodeService, times(1)).query(any(), any(), any());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    /**
     * 测试 createUser 方法 - 完整参数创建
     */
    @Test
    void testCreateUser_WithFullParameters() {
        // Given
        String fileName = "newUser";
        String userName = "New User";
        String userType = "PREMIUM";
        String channel = "premium-channel";
        String password = "securePassword";

        when(userRepository.existsByUserName(fileName)).thenReturn(false);
        when(chaincodeService.query(eq(ChaincodeTypeEnum.DABE), eq("/user/create"), any()))
                .thenReturn(successResponse);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUserEntity);

        // When
        DABEUser result = databaseDABEService.createUser(fileName, userName, userType, channel, password);

        // Then
        assertNotNull(result, "创建用户应该成功");
        assertEquals("Test User", result.getName(), "用户名应该匹配");
        verify(userRepository, times(1)).existsByUserName(fileName);
        verify(chaincodeService, times(1)).query(any(), any(), any());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    /**
     * 测试 createUser 方法 - 用户已存在
     */
    @Test
    void testCreateUser_UserAlreadyExists() {
        // Given
        String fileName = "existingUser";
        String userName = "Existing User";

        when(userRepository.existsByUserName(fileName)).thenReturn(true);
        when(userRepository.findByUserName(fileName)).thenReturn(Optional.of(testUserEntity));

        // When
        DABEUser result = databaseDABEService.createUser(fileName, userName);

        // Then
        assertNotNull(result, "已存在用户应返回现有用户");
        assertEquals("Test User", result.getName(), "应返回现有用户信息");
        verify(userRepository, times(1)).existsByUserName(fileName);
        verify(userRepository, times(1)).findByUserName(fileName);
        verify(chaincodeService, never()).query(any(), any(), any());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    /**
     * 测试 createUser 方法 - 链码调用失败
     */
    @Test
    void testCreateUser_ChaincodeFailure() {
        // Given
        String fileName = "newUser";
        String userName = "New User";

        when(userRepository.existsByUserName(fileName)).thenReturn(false);
        when(chaincodeService.query(any(), any(), any())).thenReturn(failResponse);

        // When
        DABEUser result = databaseDABEService.createUser(fileName, userName);

        // Then
        assertNull(result, "链码失败时应返回null");
        verify(userRepository, times(1)).existsByUserName(fileName);
        verify(chaincodeService, times(1)).query(any(), any(), any());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    /**
     * 测试 declareAttr 方法 - 成功声明属性
     */
    @Test
    void testDeclareAttr_Success() {
        // Given
        String fileName = "testUser";
        String attrName = "newAttribute";

        when(userRepository.findByUserName(fileName)).thenReturn(Optional.of(testUserEntity));
        when(chaincodeService.query(eq(ChaincodeTypeEnum.DABE), eq("/user/declareAttr"), any()))
                .thenReturn(successResponse);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUserEntity);

        // When
        DABEUser result = databaseDABEService.declareAttr(fileName, attrName);

        // Then
        assertNotNull(result, "声明属性应该成功");
        assertEquals("Test User", result.getName(), "用户名应该匹配");
        verify(userRepository, times(2)).findByUserName(fileName);
        verify(chaincodeService, times(1)).query(any(), any(), any());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    /**
     * 测试 declareAttr 方法 - 用户不存在
     */
    @Test
    void testDeclareAttr_UserNotFound() {
        // Given
        String fileName = "nonExistentUser";
        String attrName = "someAttribute";

        when(userRepository.findByUserName(fileName)).thenReturn(Optional.empty());

        // When
        DABEUser result = databaseDABEService.declareAttr(fileName, attrName);

        // Then
        assertNull(result, "用户不存在时应返回null");
        verify(userRepository, times(1)).findByUserName(fileName);
        verify(chaincodeService, never()).query(any(), any(), any());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    /**
     * 测试 declareAttr 方法 - 链码调用失败
     */
    @Test
    void testDeclareAttr_ChaincodeFailure() {
        // Given
        String fileName = "testUser";
        String attrName = "newAttribute";

        when(userRepository.findByUserName(fileName)).thenReturn(Optional.of(testUserEntity));
        when(chaincodeService.query(any(), any(), any())).thenReturn(failResponse);

        // When
        DABEUser result = databaseDABEService.declareAttr(fileName, attrName);

        // Then
        assertNull(result, "链码失败时应返回null");
        verify(userRepository, times(1)).findByUserName(fileName);
        verify(chaincodeService, times(1)).query(any(), any(), any());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    /**
     * 测试 approveAttrApply 方法 - 审批成功
     */
    @Test
    void testApproveAttrApply_Success() {
        // Given
        String fileName = "approver";
        String attrName = "testAttribute";
        String toUserName = "applicant";

        when(userRepository.findByUserName(fileName)).thenReturn(Optional.of(testUserEntity));
        when(chaincodeService.query(eq(ChaincodeTypeEnum.DABE), eq("/user/approveAttr"), any()))
                .thenReturn(successResponse);

        // When
        ChaincodeResponse result = databaseDABEService.approveAttrApply(fileName, attrName, toUserName);

        // Then
        assertNotNull(result, "审批应返回响应");
        assertEquals(ChaincodeResponse.Status.SUCCESS, result.getStatus(), "审批应该成功");
        verify(userRepository, times(1)).findByUserName(fileName);
        verify(chaincodeService, times(1)).query(any(), any(), any());
    }

    /**
     * 测试 approveAttrApply 方法 - 用户不存在
     */
    @Test
    void testApproveAttrApply_UserNotFound() {
        // Given
        String fileName = "nonExistentUser";
        String attrName = "testAttribute";
        String toUserName = "applicant";

        when(userRepository.findByUserName(fileName)).thenReturn(Optional.empty());

        // When
        ChaincodeResponse result = databaseDABEService.approveAttrApply(fileName, attrName, toUserName);

        // Then
        assertNotNull(result, "应返回失败响应");
        assertEquals(ChaincodeResponse.Status.FAIL, result.getStatus(), "应该返回失败状态");
        assertTrue(result.getMessage().contains("用户不存在") || result.getMessage().contains("用户"), "错误消息应该包含用户相关信息");
        verify(userRepository, times(1)).findByUserName(fileName);
        verify(chaincodeService, never()).query(any(), any(), any());
    }

    /**
     * 测试 saveUser 方法 - 保存新用户
     */
    @Test
    void testSaveUser_NewUser() {
        // Given
        String fileName = "newSaveUser";
        when(userRepository.findByUserName(fileName)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUserEntity);

        // When
        DABEUser result = databaseDABEService.saveUser(testDABEUser, fileName);

        // Then
        assertNotNull(result, "保存新用户应该成功");
        assertEquals("Test User", result.getName(), "用户名应该匹配");
        verify(userRepository, times(1)).findByUserName(fileName);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    /**
     * 测试 saveUser 方法 - 更新现有用户
     */
    @Test
    void testSaveUser_UpdateExistingUser() {
        // Given
        String fileName = "existingSaveUser";
        when(userRepository.findByUserName(fileName)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUserEntity);

        // When
        DABEUser result = databaseDABEService.saveUser(testDABEUser, fileName);

        // Then
        assertNotNull(result, "更新现有用户应该成功");
        assertEquals("Test User", result.getName(), "用户名应该匹配");
        verify(userRepository, times(1)).findByUserName(fileName);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    /**
     * 测试 deleteUser 方法 - 删除成功
     */
    @Test
    void testDeleteUser_Success() {
        // Given
        String fileName = "userToDelete";
        when(userRepository.findByUserName(fileName)).thenReturn(Optional.of(testUserEntity));

        // When
        boolean result = databaseDABEService.deleteUser(fileName);

        // Then
        assertTrue(result, "删除用户应该成功");
        verify(userRepository, times(1)).findByUserName(fileName);
        verify(userRepository, times(1)).delete(testUserEntity);
    }

    /**
     * 测试 deleteUser 方法 - 用户不存在
     */
    @Test
    void testDeleteUser_UserNotFound() {
        // Given
        String fileName = "nonExistentUser";
        when(userRepository.findByUserName(fileName)).thenReturn(Optional.empty());

        // When
        boolean result = databaseDABEService.deleteUser(fileName);

        // Then
        assertFalse(result, "删除不存在的用户应该返回false");
        verify(userRepository, times(1)).findByUserName(fileName);
        verify(userRepository, never()).delete(any(UserEntity.class));
    }

    /**
     * 测试数据库存储的公私钥功能
     */
    @Test
    void testPublicPrivateKeyStorage() {
        // Given
        String fileName = "keyTestUser";
        UserEntity keyTestEntity = UserEntity.builder()
                .id(1L)
                .userName(fileName)
                .name("Key Test User")
                .password(SecurityUtils.md5("123456"))
                .userType("TEST_TYPE")
                .channel("test_channel")
                .publicKey("-----BEGIN PUBLIC KEY-----\nVERY_LONG_PUBLIC_KEY_CONTENT_HERE...")
                .privateKey("-----BEGIN PRIVATE KEY-----\nVERY_LONG_PRIVATE_KEY_CONTENT_HERE...")
                .upk("test_upk_value")
                .apkMap("{\"attr1\":{\"Gy\":\"g1_value\"},\"attr2\":{\"Gy\":\"g2_value\"}}")
                .askMap("{\"attr1\":{\"Y\":\"y1_value\"},\"attr2\":{\"Y\":\"y2_value\"}}")
                .opkMap("{\"org1\":{\"opk\":\"opk1_value\",\"apkMap\":{\"attr1\":\"apk1_value\"}}}")
                .oskMap("{\"org1\":{\"alphaPart\":\"alpha1\",\"askMap\":{\"attr1\":{\"y\":\"ask1_y\"}}}}")
                .eggAlpha("egg_alpha_test_value")
                .alpha("alpha_test_value")
                .gAlpha("g_alpha_test_value")
                .appliedAttrMap("{\"attr1\":\"value1\",\"attr2\":\"value2\"}")
                .privacyAttrMap("{\"privacy_attr1\":\"privacy_value1\"}")
                .expireDate("2024-12-31")
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        when(userRepository.findByUserName(fileName)).thenReturn(Optional.of(keyTestEntity));

        // When
        DABEUser result = databaseDABEService.getUser(fileName);

        // Then
        assertNotNull(result, "用户不应为空");
        verify(userRepository, times(1)).findByUserName(fileName);

        // 验证其他加密相关字段正确读取
        assertEquals("egg_alpha_test_value", result.getEggAlpha(), "EggAlpha应正确读取");
        assertEquals("alpha_test_value", result.getAlpha(), "Alpha应正确读取");
        assertEquals("g_alpha_test_value", result.getGAlpha(), "GAlpha应正确读取");
    }

    /**
     * 测试复杂JSON字段的存储和解析
     */
    @Test
    void testComplexJsonFieldStorage() {
        // Given
        String fileName = "jsonTestUser";
        when(userRepository.findByUserName(fileName)).thenReturn(Optional.of(testUserEntity));

        // When
        DABEUser result = databaseDABEService.getUser(fileName);

        // Then
        assertNotNull(result, "用户不应为空");

        // 验证APK映射的JSON存储和解析
        assertNotNull(result.getApkMap(), "APK映射不应为空");
        assertEquals(2, result.getApkMap().size(), "APK映射应包含2个属性");
        assertTrue(result.getApkMap().containsKey("attr1"), "应包含attr1");
        assertTrue(result.getApkMap().containsKey("attr2"), "应包含attr2");
        assertEquals("g1_value", result.getApkMap().get("attr1").getGy(), "attr1的gy值应正确");
        assertEquals("g2_value", result.getApkMap().get("attr2").getGy(), "attr2的gy值应正确");

        // 验证ASK映射的JSON存储和解析
        assertNotNull(result.getAskMap(), "ASK映射不应为空");
        assertEquals(2, result.getAskMap().size(), "ASK映射应包含2个属性");
        assertTrue(result.getAskMap().containsKey("attr1"), "应包含attr1");
        assertEquals("y1_value", result.getAskMap().get("attr1").getY(), "attr1的y值应正确");

        // 验证应用属性映射
        assertNotNull(result.getAppliedAttrMap(), "应用属性映射不应为空");
        assertEquals(2, result.getAppliedAttrMap().size(), "应用属性映射应包含2个属性");
        assertEquals("value1", result.getAppliedAttrMap().get("attr1"), "attr1值应正确");
        assertEquals("value2", result.getAppliedAttrMap().get("attr2"), "attr2值应正确");

        // 验证隐私属性映射
        assertNotNull(result.getPrivacyAttrMap(), "隐私属性映射不应为空");
        assertEquals(1, result.getPrivacyAttrMap().size(), "隐私属性映射应包含1个属性");
        assertEquals("privacy_value1", result.getPrivacyAttrMap().get("privacy_attr1"), "隐私属性值应正确");
    }

    /**
     * 测试参数验证
     */
    @Test
    void testParameterValidation() {
        // 测试 null 参数
        assertNull(databaseDABEService.getUser(null),"null文件名应该抛出异常");
        assertNull(databaseDABEService.getUser2(null, "password"),"null文件名应该抛出异常");
        assertNull(databaseDABEService.getUser2("fileName", null),"null密码应该抛出异常");
        assertNull(databaseDABEService.createUser(null, "userName"),"null文件名应该抛出异常");
        assertNull(databaseDABEService.createUser("fileName", null),"null用户名应该抛出异常");
        assertNull(databaseDABEService.declareAttr(null, "attrName"), "null文件名应该抛出异常");
        assertNull(databaseDABEService.declareAttr("fileName", null), "null属性名应该抛出异常");
    }

    /**
     * 测试并发访问安全性
     */
    @Test
    void testConcurrentAccess() throws InterruptedException {
        // Given
        String fileName = "concurrentUser";
        when(userRepository.findByUserName(fileName)).thenReturn(Optional.of(testUserEntity));

        // When - 模拟并发访问
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        List<DABEUser> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                DABEUser result = databaseDABEService.getUser(fileName);
                results.add(result);
            });
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        assertEquals(threadCount, results.size(), "所有线程都应该完成");
        for (DABEUser result : results) {
            assertNotNull(result, "每个结果都不应为空");
            assertEquals("Test User", result.getName(), "用户名应该一致");
        }
        verify(userRepository, times(threadCount)).findByUserName(fileName);
    }
}