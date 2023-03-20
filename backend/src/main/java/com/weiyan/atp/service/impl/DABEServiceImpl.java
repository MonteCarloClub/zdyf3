package com.weiyan.atp.service.impl;

import com.alibaba.fastjson.parser.Feature;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.utils.CCUtils;
import com.weiyan.atp.utils.JsonProviderHolder;

import com.weiyan.atp.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.validation.constraints.NotEmpty;
import com.alibaba.fastjson.JSONObject;
//import sun.security.util.Debug;


/**
 * @author : 魏延thor
 * @since : 2020/6/2
 */
@Service
@Slf4j
@Validated
public class DABEServiceImpl implements DABEService {
    private final ChaincodeService chaincodeService;
    @Value("${atp.path.dabeUser}")
    private String userPath;

    @Value("${atp.path.cert}")
    private String certPath;

    public DABEServiceImpl(ChaincodeService chaincodeService) {
        this.chaincodeService = chaincodeService;
    }

    @Override
    public DABEUser getUser(@NotEmpty String fileName) {
        try {
            String filePath = userPath + fileName;
            String resource = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            return JsonProviderHolder.JACKSON.parse(resource, DABEUser.class);
        } catch (Exception e) {
            log.warn("get user error", e);
            return null;
        }
    }

    @Override
    public DABEUser getUser2(@NotEmpty String fileName, @NotEmpty String password) {
        try {
            String filePath = userPath + fileName;
            String resource = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            System.out.println(resource);
            DABEUser user = JsonProviderHolder.JACKSON.parse(resource, DABEUser.class);
            System.out.println("sorryyyyyyyy");
            String hash = SecurityUtils.md5(password);
            System.out.println(user.getPassword());
            boolean ans = user.getPassword().equals(hash);
            System.out.println(ans);
            System.out.println("goodddddd");
            if (!user.getPassword().equals(hash)) {
                System.out.println("qaqaqaqaqaqaq");
                return null;
            }
            return user;
        } catch (Exception e) {
            log.warn("get user error", e);
            return null;
        }
    }

    @Override
    public DABEUser getUser3(@NotEmpty String fileName, @NotEmpty String cert) {
        try {

            String filePath1 = userPath + fileName;
            String resource1 = FileUtils.readFileToString(new File(filePath1), StandardCharsets.UTF_8);
            DABEUser user = JsonProviderHolder.JACKSON.parse(resource1, DABEUser.class);


            String filePath = certPath + fileName;
            String resource = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            System.out.println(resource);
            JSONObject object = JSONObject.parseObject(resource, Feature.OrderedField);
            String num = object.getJSONObject("certificate").getString("serialNumber");
            System.out.println(num);
            String certNum = fileName + "-" + cert;
            System.out.println(certNum);

            if (!num.equals(certNum)) {
                return null;
            }
            System.out.println(object.toJSONString());
            //verify certificate
            String result = verifyABSCert("http://10.176.40.46/dpki/VerifyABSCert", object.toJSONString());
            System.out.println("verification----------------" + result);
            if (!result.equals("True")) {
                return null;
            }
            return user;
        } catch (Exception e) {
            log.warn("get user error", e);
            return null;
        }
    }

    @Override
    public DABEUser createUser(@NotEmpty String fileName, @NotEmpty String userName) {
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/user/create", new ArrayList<>(Collections.singletonList(userName)));
        CCUtils.saveResponse2(certPath, fileName, response);
        return CCUtils.saveResponse(userPath, fileName, response);
    }

    @Override
    public DABEUser createUser(@NotEmpty String fileName, @NotEmpty String userName, @NotEmpty String userType, @NotEmpty String channel, @NotEmpty String password) {
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/user/create", new ArrayList<>(Collections.singletonList(userName)));
        CCUtils.saveResponse2(certPath, fileName, response);
        return CCUtils.saveResponse(userPath, fileName, userType, channel, password, false, response);
    }

    @Override
    public DABEUser declareAttr(@NotEmpty String fileName, @NotEmpty String attrName) {
        DABEUser user = getUser(fileName);
        if (user == null) {
            log.info("no user found");
            return null;
        }
        String userJson = JsonProviderHolder.JACKSON.toJsonString(user);
        System.out.println("22222222222");
        System.out.println(userJson);
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/user/declareAttr",
                new ArrayList<>(Arrays.asList(userJson, attrName)));
        return CCUtils.saveResponse(userPath, fileName, user.getUserType(), user.getChannel(), user.getPassword(), true, response);
    }

    @Override
    public ChaincodeResponse approveAttrApply(@NotEmpty String fileName, @NotEmpty String attrName,
                                              @NotEmpty String toUserName) {
        DABEUser user = getUser(fileName);
        if (user == null) {
            log.info("no user found");
            return null;
        }
        String userJson = JsonProviderHolder.JACKSON.toJsonString(user);
        return chaincodeService.query(ChaincodeTypeEnum.DABE, "/user/approveAttr",
                new ArrayList<>(Arrays.asList(userJson, toUserName, attrName)));
    }


    public static String verifyABSCert(String url, String JSONBody) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");
        httpPost.setEntity(new StringEntity(JSONBody, StandardCharsets.UTF_8));
        try {
            HttpResponse response = httpClient.execute(httpPost);
            System.out.println(response.getStatusLine().getStatusCode() + "\n");
            HttpEntity entity = response.getEntity();
            String responseContent = EntityUtils.toString(entity, "UTF-8");
            System.out.println(responseContent);
            return responseContent;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}