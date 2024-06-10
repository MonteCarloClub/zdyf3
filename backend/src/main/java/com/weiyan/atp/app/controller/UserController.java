package com.weiyan.atp.app.controller;

import com.alibaba.fastjson.parser.Feature;
import com.weiyan.atp.constant.AttrApplyStatusEnum;
import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.*;
import com.weiyan.atp.data.request.chaincode.plat.QueryUserCCRequest;
import com.weiyan.atp.data.request.web.*;
import com.weiyan.atp.data.response.intergration.ApplyAttrResponse;
import com.weiyan.atp.data.response.intergration.CreateUserResponse;
import com.weiyan.atp.service.AttrService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.service.UserRepositoryService;
import com.weiyan.atp.utils.JsonProviderHolder;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.asynchttpclient.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@RequestMapping("/user")
@RestController
@Slf4j
@CrossOrigin //支持跨域访问
public class UserController {
    private final UserRepositoryService userRepositoryService;
    private final AttrService attrService;

    private final DABEService dabeService;

    @Value("${atp.devMode.channelName}")
    private String channelName;


    public UserController(UserRepositoryService userRepositoryService, AttrService attrService, DABEService dabeService) {
        this.userRepositoryService = userRepositoryService;
        this.attrService = attrService;
        this.dabeService = dabeService;
    }

    @PostMapping("/")
    public Result<Object> createUser(@RequestBody @Validated CreateUserRequest request) {
        ChaincodeResponse response = userRepositoryService.createUser(request);

        //对接
//            String url = baseUrl+"/attruser";
//            HttpClient client = HttpClients.createDefault();
//            //默认post请求
//            HttpPost post = new HttpPost(url);
//            //拼接多参数
//            JSONObject json = new JSONObject();
//            try {
//                json.put("channel_name", channelName);
//                json.put("certificate",cert);
//                json.put("uid",request.getFileName());
//                json.put("timestamp",String.valueOf(System.currentTimeMillis()));
//                String message = "[" + json + "]";
//                log.info(message);
//                post.addHeader("Content-type", "application/json; charset=utf-8");
//                post.setHeader("Accept", "application/json");
//                post.setEntity(new StringEntity(message, StandardCharsets.UTF_8));
//                HttpResponse httpResponse = client.execute(post);
//                HttpEntity entity = httpResponse.getEntity();
//                System.err.println("状态:" + httpResponse.getStatusLine());
//                System.err.println("参数:" + EntityUtils.toString(entity));
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        return Result.okWithData(response.getResult(str -> str));
    }

    @PostMapping("/create")
    public Result<Object> createUserInOne(@RequestBody @Validated CreateUserInOneRequest request) {
        // 检查user是否已经创建，若已经创建则报错，否则正常创建
//        String url = COUCHDB_URL + "/" + DATABASE_NAME + "/" + documentId;
        String url = "http://180.167.127.16:5984/myc_plat/ID:" + request.getUserName();
        System.out.println("[br]检查注册的user是否已经存在：" + url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet checkUserRequest = new HttpGet(url);
            HttpResponse response = httpClient.execute(checkUserRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Document exists
                return Result.internalError("User Already Exists");
            } else if (statusCode == 404) {
                // Document does not exist
                // 即用户尚未创建，则正常创建用户即可
                ;
            } else {
                // Handle other status codes if needed
                System.out.println("Unexpected response status: " + statusCode);
                return Result.internalError("Unexpected response status: " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.internalError(e.getMessage());
        }

        System.out.println("[br] User does not exists, start creating user...");
        System.out.println("[br] invoke dabeService.createUser()");
        dabeService.createUser(request.getUserName(), request.getUserName(), request.getUserType(), request.getChannel(), request.getPassword());
        System.out.println("[br] dabeService.createUser() finished, invoke ChaincodeResponse response =...");
        ChaincodeResponse response = userRepositoryService.createUserInOne(request.getUserName(), request.getUserType(), request.getChannel());
        System.out.println("[br] got chaincode response: {}" + response);

        //对接
//        try {
//            String url = baseUrl + "/attruser";
//            AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(new DefaultAsyncHttpClientConfig.Builder()
//                    .setConnectTimeout(10000)
//                    .setRequestTimeout(10000)
//                    .build());
//            //默认post请求
//            HttpPost post = new HttpPost(url);
//            //拼接多参数
//            JSONObject json = new JSONObject();
//
//            com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject.parseObject(cert, Feature.OrderedField);
//            com.alibaba.fastjson.JSONObject num = object.getJSONObject("certificate");
//
//            json.put("channel_name", request.getChannel());
//            json.put("certificate", num);
//            json.put("uid", request.getUserName());
//            json.put("timestamp", new Date().toString());
//            String message = ""+json+"";
//            log.info(message);
//
//            RequestBuilder builder = new RequestBuilder();
//            builder.setUrl(url);
//            builder.addHeader("Content-type", "application/json; charset=utf-8");
//            builder.addHeader("Accept", "application/json");
//            builder.setBody(message);
//            asyncHttpClient.executeRequest(builder.build());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return Result.okWithData(response.getResult(str->str));
    }

    @GetMapping("/")
    public Result<PlatUser> getUser(QueryUserRequest request) {
        if (StringUtils.isEmpty(request.getUserName()) && StringUtils.isEmpty(request.getPubKey())) {
            return Result.internalError("all empty request");
        }
        return Result.okWithData(userRepositoryService.queryUser(request));
    }

    @PostMapping("/attr")
    public Result<Object> declareAttr(@RequestBody @Validated DeclareUserAttrRequest request) {

        ChaincodeResponse response = attrService.declareUserAttr2(request);
        //对接
//        String url = baseUrl + "/creatattr";
//        HttpClient client = HttpClients.createDefault();
//        //默认post请求
//        HttpPost post = new HttpPost(url);
//        //拼接多参数
//        JSONObject json = new JSONObject();
//        try {
//            if (response.isFailed()) {
//                json.put("result", "false");
//            } else {
//                json.put("result", "true");
//            }
//            DABEUser user = dabeService.getUser(request.getFileName());
//            json.put("channelName", user.getChannel());
//            json.put("attrName", request.getAttrName());
//            json.put("userName", request.getFileName());
//            json.put("timestamp", new Date().toString());
//            json.put("result","true");
//            String message = "" + json + "";
//            log.info(message);
//            post.addHeader("Content-type", "application/json; charset=utf-8");
//            post.setHeader("Accept", "application/json");
//            post.setEntity(new StringEntity(message, StandardCharsets.UTF_8));
//            HttpResponse httpResponse = client.execute(post);
//            HttpEntity entity = httpResponse.getEntity();
//            System.err.println("状态:" + httpResponse.getStatusLine());
//            System.err.println("参数:" + EntityUtils.toString(entity));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return response.getResult(str -> str);
    }

    @PostMapping("/batchAttr")
    public Result<Object> batchDeclareAttr(@RequestBody @Validated DeclareUserAttrRequest request) {
        return attrService.batchDeclareUserAttr(request)
                .getResult(str -> str);
    }

    /**
     * 申请他人属性
     */
    @PostMapping("/attr/apply")
    public Result<Object> applyAttr(@RequestBody @Validated ApplyUserAttrRequest request) {
        ChaincodeResponse chaincodeResponse = attrService.applyAttr2(request);

        return Result.okWithData(chaincodeResponse.getResult(str -> str));
    }

    /**
     * 撤销他人属性
     */
    @PostMapping("/attr/revoke")
    public Result<Object> revokeAttr(@RequestBody @Validated RevokeUserAttrRequest request) {
        ChaincodeResponse chaincodeResponse = attrService.revokeAttr(request);
        DABEUser user = dabeService.getUser(request.getUserName());

//        //对接
//        String url = baseUrl + "/addattr";
//        HttpClient client = HttpClients.createDefault();
//        //默认post请求
//        HttpPost post = new HttpPost(url);
//        //拼接多参数
//        JSONObject json = new JSONObject();
//        try {
//
//            json.put("result", "revocation");
//            json.put("channel_name", user.getChannel());
//            json.put("fromUserName", request.getUserName());
//            //json.put("fromOrgName",request.getToOrgName());
//            json.put("toUserName", request.getToUserName());
//            json.put("attrName", request.getAttrName());
//            json.put("timestamp", new Date().toString());
//            String message = "" + json + "";
//            log.info(message);
//            post.addHeader("Content-type", "application/json; charset=utf-8");
//            post.setHeader("Accept", "application/json");
//            post.setEntity(new StringEntity(message, StandardCharsets.UTF_8));
//            HttpResponse httpResponse = client.execute(post);
//            HttpEntity entity = httpResponse.getEntity();
//            System.err.println("状态:" + httpResponse.getStatusLine());
//            System.err.println("参数:" + EntityUtils.toString(entity));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return Result.okWithData(chaincodeResponse.getResult(str -> str));
    }

    /**
     * 申请他人属性
     */
//    @PostMapping("/attr/apply")
//    public Result<Object> applyAttr(@RequestBody @Validated ApplyUserAttrRequest request) {
//        return attrService.applyAttr(request)
//                .getResult(str -> str);
//    }

    /**
     * 批量申请他人属性
     */
    @PostMapping("/attr/batchApply")
    public Result<Object> batchApplyAttr(@RequestBody @Validated ApplyUserAttrRequest request) {
        return attrService.batchApplyAttr(request)
                .getResult(str -> str);
    }

    /**
     * 查询属性申请
     *
     * @param toId     查询的对方id
     * @param type     类型 0 for user； 1 for org
     * @param userName 用户名
     * @param status   状态
     */
    @GetMapping("/attr/apply")
    public Result<Object> queryAttrApply(String toId, Integer type, String userName,
                                         String status) {
        System.out.println("1111111111111111111");
        if (type != 0 && type != 1) {
            throw new BaseException("wrong type");
        }
        System.out.println(userName);
        System.out.println("1111111111111111111111111111111111111");
        return attrService.queryAttrApply(type == 0 ? toId : "",
                        type == 1 ? toId : "", userName, AttrApplyStatusEnum.valueOf(status))
                .getResult(str -> JsonProviderHolder.JACKSON.parseList(str, PlatUserAttrApply.class));
    }

    /**
     * 审批他人申请
     */
    @PostMapping("/attr/approval")
    public Result<Object> approveAttrApply(@RequestBody @Validated ApproveAttrApplyRequest request) {
        ChaincodeResponse response = attrService.approveAttrApply2(request);
        DABEUser applyUser = dabeService.getUser(request.getToUserName());
//        //对接
//        String url = baseUrl + "/addattr";
//        HttpClient client = HttpClients.createDefault();
//        //默认post请求
//        HttpPost post = new HttpPost(url);
//        //拼接多参数
//        JSONObject json = new JSONObject();
//        try {
//            if (response.isFailed()) {
//                json.put("result", "false");
//            } else {
//                json.put("result", "true");
//            }
//            json.put("channel_name", applyUser.getChannel());
//            json.put("fromUserName", request.getFileName());
//            //json.put("fromOrgName",request.getToOrgName());
//            json.put("toUserName", request.getToUserName());
//            json.put("attrName", request.getAttrName());
//            json.put("timestamp", new Date().toString());
//            json.put("result","true");
//            String message = "" + json + "";
//            log.info(message);
//            post.addHeader("Content-type", "application/json; charset=utf-8");
//            post.setHeader("Accept", "application/json");
//            post.setEntity(new StringEntity(message, StandardCharsets.UTF_8));
//            HttpResponse httpResponse = client.execute(post);
//            HttpEntity entity = httpResponse.getEntity();
//            System.err.println("状态:" + httpResponse.getStatusLine());
//            System.err.println("参数:" + EntityUtils.toString(entity));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return response.getResult(str -> str);
    }

    /**
     * 同步属性
     */
    @PostMapping("/attr/sync")
    public Result<DABEUser> syncSuccessApply(@RequestBody @Validated SyncAttrRequest request) {
        if (request.getType() == null) {
            return Result.okWithData(attrService.syncSuccessAttrApply(request.getFileName()));
        }
        return Result.okWithData(
                attrService.syncSuccessAttrApply2(request.getFileName(),
                        request.getType() == 0 ? request.getToId() : "",
                        request.getType() == 1 ? request.getToId() : ""));
    }

    /**
     * 属性历史记录
     */
    @PostMapping("/attr/history")
    public Result<Object> attrHistory(String userName) {
        return attrService.queryAttrHistory(userName)
                .getResult(str -> JsonProviderHolder.JACKSON.parseList(str, PlatUserAttrHistory.class));
    }

    /**
     * 批量注册
     */
    @GetMapping("/batchRegister")
    public Result<Object> attrHistory() {
        String base = "iotdevice";
        String type = "user";
        String pwd = "123";
        String[] ch = new String[]{"温湿度传感器", "淹水重传感器"};
        for (int i = 0; i < 200; i++) {
            String userName = base + i;
            CreateUserInOneRequest request = new CreateUserInOneRequest();
            request.setUserName(userName);
            request.setUserType(type);
            if (i < 100) {
                request.setChannel(ch[0]);
            } else {
                request.setChannel(ch[1]);
            }
            request.setPassword(pwd);
            createUserInOne(request);
        }
        return Result.success();
    }
}
