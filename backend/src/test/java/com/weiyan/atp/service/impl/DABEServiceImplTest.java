package com.weiyan.atp.service.impl;

import com.weiyan.atp.AbeTrustPlatformApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
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

@Slf4j
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AbeTrustPlatformApplication.class)
class DABEServiceImplTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4j.class);

    @Test
    void getUser() throws Exception {  

    }

    @Test
    void  getUser2() throws Exception {
        String uri = "/dabe/user2";

        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(uri)
                        .param("fileName", "kris")
                        .param("password", "123")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        int status = result.getResponse().getStatus();
        Assert.assertEquals("錯誤",200,status);
    }

    @Test
    void getUser3() {
    }

    @Test
    void createUser() {
    }

    @Test
    void testCreateUser() {
    }

    @Test
    void declareAttr() {
    }

    @Test
    void approveAttrApply() {
    }

    @Test
    void verifyABSCert() {
    }
}