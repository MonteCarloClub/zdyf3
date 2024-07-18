//package com.weiyan.atp.service.impl;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.weiyan.atp.AbeTrustPlatformApplication;
//import com.weiyan.atp.data.request.web.DecryptContentRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.jupiter.api.Test;
//
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Arrays;
//
//@Slf4j
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = AbeTrustPlatformApplication.class)
//class DABEServiceImplTest {
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4j.class);
//
//    @Test
//    void getUser() throws Exception {
//
//    }
//
//    @Test
//    void getUser2() throws Exception {
//        String uri = "/dabe/user2";
//
//        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        MvcResult result = mvc.perform(
//                MockMvcRequestBuilders.post(uri)
//                        .param("fileName", "kris")
//                        .param("password", "123")
//                        .accept(MediaType.APPLICATION_JSON)
//        ).andReturn();
//
//        int status = result.getResponse().getStatus();
//        Assert.assertEquals("错误", 200, status);
//    }
//
//    @Test
//    void getUser2DryRun() throws Exception {
//        String uri = "/dabe/user2_dry_run";
//        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        MvcResult result = mvc.perform(
//                MockMvcRequestBuilders.get(uri)
//                        .param("filename", "filename")
//                        .param("password", "password")
//                        .accept(MediaType.APPLICATION_JSON)
//        ).andReturn();
//        int status = result.getResponse().getStatus();
//        Assert.assertEquals("/dabe/user2_dry_run: fail", 200, status);
//    }
//
//    @Test
//    void getUser2BatchDryRun() throws Exception {
//        String uri = "/dabe/user2_batch_dry_run";
//        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        MvcResult result = mvc.perform(
//                MockMvcRequestBuilders.get(uri)
//                        .param("batch_size", String.valueOf(10000000))
//                        .accept(MediaType.APPLICATION_JSON)
//        ).andReturn();
//        int status = result.getResponse().getStatus();
//        Assert.assertEquals("/dabe/user2_batch_dry_run: fail", 200, status);
//    }
//
//    @Test
//    void getUser3() {
//    }
//
//    @Test
//    void createUser() {
//    }
//
//    @Test
//    void testCreateUser() {
//    }
//
//    @Test
//    void declareAttr() {
//    }
//
//    @Test
//    void approveAttrApply() {
//    }
//
//    @Test
//    void verifyABSCert() {
//    }
//
//    @Test
//    void DecryptFile() throws Exception {
//        String uri = "/content/decryption";
//
//        DecryptContentRequest mockRequest = new DecryptContentRequest();
//        mockRequest.setCipher("4b3d7bee314e074ff621ebe8d0db5bb0");
//        mockRequest.setFileName("123.docx");
//        mockRequest.setSharedUser("深圳市气象局");
//        mockRequest.setTags(Arrays.asList("1", "2", "3", "4"));
//        mockRequest.setUserName("深圳市应急局");
//
//        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        MvcResult result = mvc.perform(
//                MockMvcRequestBuilders.post(uri)
//                        .content(objectMapper.writeValueAsString(mockRequest))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//        ).andReturn();
//
//
//        int status = result.getResponse().getStatus();
//        System.out.println(status);
//
//    }
//
//    @Test
//    void judge_user_attrs() throws Exception {
//        String uri = "/content/judge_user_attrs";
//
//        DecryptContentRequest mockRequest = new DecryptContentRequest();
//        mockRequest.setCipher("4b3d7bee314e074ff621ebe8d0db5bb0");
//        mockRequest.setFileName("123.docx");
//        mockRequest.setSharedUser("深圳市气象局");
//        mockRequest.setTags(Arrays.asList("1", "2", "3", "4"));
//        mockRequest.setUserName("深圳市交通局");
////        mockRequest.setUserName("深圳市应急局");
//
//        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        MvcResult result = mvc.perform(
//                MockMvcRequestBuilders.post(uri)
//                        .content(objectMapper.writeValueAsString(mockRequest))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//        ).andReturn();
//        String responseContent = result.getResponse().getContentAsString();
//        System.out.println(responseContent);
//
//
//    }
//}
