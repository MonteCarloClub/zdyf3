package com.weiyan.atp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiyan.atp.AbeTrustPlatformApplication;
import com.weiyan.atp.data.request.web.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@Slf4j
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AbeTrustPlatformApplication.class)
class DABEServiceImplTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4j.class);


    @Test
    void createUserInone() throws Exception {
        String uri = "/user/create";
        CreateUserInOneRequest mockRequest = new CreateUserInOneRequest();
        mockRequest.setUserName("user101");
        mockRequest.setPassword("123");
        mockRequest.setUserType("user");
        mockRequest.setChannel("fudan");

        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(uri)
                        .content(objectMapper.writeValueAsString(mockRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        int status = result.getResponse().getStatus();
        Assert.assertEquals("错误", 200, status);
    }

    @Test
    void createUserBatch() throws Exception {
        String uri = "/user/create";
        int batch_size = 10;
        CreateUserInOneRequest mockRequest = new CreateUserInOneRequest();
        String filename = "filename_", password = "password_";
        long loTimestamp = System.currentTimeMillis();
        int logStep = batch_size / 10;
        for (int i = 0; i < batch_size; i++) {
            mockRequest.setUserName(filename+i);
            mockRequest.setPassword(password+i);
            mockRequest.setUserType("user");
            mockRequest.setChannel("test_createUser_Batch");
            MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            MvcResult result = mvc.perform(
                    MockMvcRequestBuilders.post(uri)
                            .content(objectMapper.writeValueAsString(mockRequest))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
            ).andReturn();
            if (i > 0 && i % logStep == 0) {
                long miTimestamp = System.currentTimeMillis();
                System.out.printf("/createUserBatch: %d user(s) handled in %d ms%n", i, miTimestamp - loTimestamp);
            }
        }
        long hiTimestamp = System.currentTimeMillis();
        System.out.printf("/createUserBatch: %d user(s) handled in %d ms%n", batch_size, hiTimestamp - loTimestamp);
    }



    @Test
    void getUser2() throws Exception {
        String uri = "/dabe/user2";

        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(uri)
                        .param("fileName", "user100")
                        .param("password", "123")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        int status = result.getResponse().getStatus();
        Assert.assertEquals("错误", 200, status);
    }

    @Test
    void declareAttr() throws Exception {
        String uri = "/dabe/user/attr";
        String attrname = "fudan1:attr5",filename = "fudan1";
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(uri)
                        .param("fileName", filename)
                        .param("attrName", attrname)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
    }


    @Test
    void declareAttrBatch() throws Exception {
        String uri = "/dabe/user/attr";
        int batch_size = 10;
        String attrname = "Attr_",filename = "filename_";
        long loTimestamp = System.currentTimeMillis();
        int logStep = batch_size / 10;
        for (int i = 0; i < batch_size; i++) {
            MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            MvcResult result = mvc.perform(
                    MockMvcRequestBuilders.post(uri)
                            .param("fileName", filename+i)
                            .param("attrName", filename+i+":"+attrname+i)
                            .accept(MediaType.APPLICATION_JSON)
            ).andReturn();
            if (i > 0 && i % logStep == 0) {
                long miTimestamp = System.currentTimeMillis();
                System.out.printf("/declareAttrBatch: %d user(s) handled in %d ms%n", i, miTimestamp - loTimestamp);
            }
        }
        long hiTimestamp = System.currentTimeMillis();
        System.out.printf("/declareAttrBatch: %d user(s) handled in %d ms%n", batch_size, hiTimestamp - loTimestamp);
    }

    @Test
    void ApplyAttr() throws Exception {
        String uri = "/user/attr/apply";
        ApplyUserAttrRequest mockRequest = new ApplyUserAttrRequest();
        mockRequest.setAttrName("filename_3:Attr_3");
        mockRequest.setFileName("filename_1");
        mockRequest.setRemark("attr apply");
        mockRequest.setToOrgName("");
        mockRequest.setToUserName("filename_3");
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(uri)
                        .content(objectMapper.writeValueAsString(mockRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
    }

    @Test
    void approveAttrApply() throws Exception {
        String uri = "/user/attr/approval";
        ApproveAttrApplyRequest mockRequest = new ApproveAttrApplyRequest();
        mockRequest.setAttrName("filename_3:Attr_3");
        mockRequest.setFileName("filename_3");
        mockRequest.setRemark("tongyi");
        mockRequest.setToUserName("filename_1");
        mockRequest.setAgree(true);
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(uri)
                        .content(objectMapper.writeValueAsString(mockRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
    }



    @Test
    void DecryptFile() throws Exception {
        String uri = "/content/decryption";

        DecryptContentRequest mockRequest = new DecryptContentRequest();
        mockRequest.setCipher("50f0cb242140e7abbca37f5ff973ab4e");
        mockRequest.setFileName("深圳市 深圳市福田区 气象 福田区-气象数据.xlsx.xlsx");
        mockRequest.setSharedUser("深圳市气象局");
        mockRequest.setTags(Arrays.asList("深圳市", "深圳市福田区", "气象", "福田区-气象数据.xlsx"));
        mockRequest.setUserName("深圳市");

        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(uri)
                        .content(objectMapper.writeValueAsString(mockRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();


        int status = result.getResponse().getStatus();
//        Assert.assertEquals("错误",200,status);

    }
}