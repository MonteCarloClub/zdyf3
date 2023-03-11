package com.weiyan.atp.app.controller;

import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.data.request.web.DecryptContentRequest;
import com.weiyan.atp.data.request.web.RevokeUserAttrRequest;
import com.weiyan.atp.data.request.web.ShareContentRequest;
import com.weiyan.atp.data.response.intergration.EncryptionResponse;
import com.weiyan.atp.data.response.web.PlatContentsResponse;
import com.weiyan.atp.service.AttrService;
import com.weiyan.atp.service.ContentService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.utils.JsonProviderHolder;
import com.weiyan.atp.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;

/**
 * @author : 魏延thor
 * @since : 2020/6/10
 * @author : dongliangCai
 * @since : 2022/11/4
 */
@RestController
@RequestMapping("/content")
@Slf4j
@CrossOrigin //支持跨域访问
public class ContentController {
    private final ContentService contentService;
    private final AttrService attrService;

    private final DABEService dabeService;

    @Value("${atp.devMode.baseUrl}")
    private String baseUrl;

    @Value("${atp.path.shareData}")
    private String shareDataPath;

    @Value("${atp.path.encryptData}")
    private String encryptDataPath;

    @Value("${atp.path.cipherData}")
    private String cipherDataPath;

    @Value("${atp.path.dabeUser}")
    private String userPath;

    @Value("${spring.datasource.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.username}")
    private String mysqlUser;

    @Value("${spring.datasource.password}")
    private String mysqlPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String Driver;

    private static int cnt = 0;

    public ContentController(ContentService contentService, AttrService attrService, DABEService dabeService) {
        this.contentService = contentService;
        this.attrService = attrService;
        this.dabeService = dabeService;
    }

    @PostMapping("/")
    public Result<Object> encryptContent(@RequestBody @Validated ShareContentRequest request) {
        int tagSize = request.getTags().size();
        if (tagSize == 0 || tagSize > 10) {
            return Result.internalError("tags length error");
        }
        EncryptionResponse encryptionResponse = contentService.encContent(request);

//        //对接
//        String url = baseUrl+"/attrpolicy";
//        HttpClient client = HttpClients.createDefault();
//        //默认post请求
//        HttpPost post = new HttpPost(url);
//        //拼接多参数
//        JSONObject json = new JSONObject();
//        JSONArray array = new JSONArray();
//        try {
//            json.put("contentHash", encryptionResponse.getCipherHash());
//            json.put("policy",request.getPolicy());
//            json.put("uid",request.getFileName());
//            array.put(request.getTags().get(0));
//            array.put(request.getTags().get(1));
//            array.put(request.getTags().get(2));
//            array.put(request.getTags().get(3));
//            json.put("tags",array);
//            json.put("timestamp",encryptionResponse.getTimeStamp());
//            String message = "[" + json + "]";
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

        return Result.success();
    }

//    @PostMapping("/")
    @PostMapping("/share")
    public Result<Object> shareContent(@RequestBody @Validated ShareContentRequest request) {
        int tagSize = request.getTags().size();
        if (tagSize == 0 || tagSize > 10) {
            return Result.internalError("tags length error");
        }
        contentService.shareContent(request);
        return Result.success();
    }

    @PostMapping("/decryption")
    public Result<String> decryptContent(@RequestBody @Validated DecryptContentRequest request,HttpServletRequest req) {
        String ipAddress = SecurityUtils.getIpAddr(req);
        System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
        System.out.println(request.toString());
        request.setIp(ipAddress);
        ChaincodeResponse response = null;
        if(!request.getUserName().equals(request.getSharedUser())){

              String filePath = encryptDataPath +request.getSharedUser()+"/"+ request.getFileName();
 //           String filePath = "atp\\data\\enc\\深圳市气象局\\深圳市\\ 深圳市福田区\\ 气象\\ 福田区-气象数据.xlsx";
            System.out.println("ppppppppppppppppppp");
            System.out.println(filePath);
            try {
                String cipher= FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
                System.out.println("ccccccccccccccccccccccccccccc");
                System.out.println(cipher);
                request.setCipher(cipher);
            } catch (IOException e) {
                e.printStackTrace();
            }
            response = contentService.decryptContent2(request.getCipher(), request.getUserName(), request.getFileName(), request.getSharedUser());
        }else{
            response = new ChaincodeResponse();
            response.setStatus(ChaincodeResponse.Status.SUCCESS);
        }

        //对接
        String url = baseUrl+"/share_judgement";
        HttpClient client = HttpClients.createDefault();
        //默认post请求
        HttpPost post = new HttpPost(url);
        //拼接多参数
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            if(response.isFailed()){
                json.put("result","false");
            }else{
                json.put("result","true");
            }
            json.put("contentHash", SecurityUtils.md5(request.getCipher()));
            String filePath = userPath + request.getUserName();
            String resource = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            DABEUser user = JsonProviderHolder.JACKSON.parse(resource, DABEUser.class);
            json.put("userChannel",user.getChannel());
            json.put("uid",request.getUserName());
            json.put("userIP",request.getIp());
            array.put(request.getTags().get(0));
            array.put(request.getTags().get(1));
            array.put(request.getTags().get(2));
            array.put(request.getTags().get(3));
            json.put("tags",array);
            json.put("timestamp",new Date().toString());
            json.put("result","true");
            String message = "" + json + "";
            post.addHeader("Content-type", "application/json; charset=utf-8");
            post.setHeader("Accept", "application/json");
            post.setEntity(new StringEntity(message, StandardCharsets.UTF_8));
            HttpResponse httpResponse = client.execute(post);
            HttpEntity entity = httpResponse.getEntity();
            System.err.println("状态:" + httpResponse.getStatusLine());
            System.err.println("参数:" + EntityUtils.toString(entity));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response.isFailed()){
            throw new BaseException("decryption error: " + response.getMessage());
        }

        if(cnt>=5){
            revokeUserAttr(request.getUserName(),"频繁解密异常");
            attrService.syncSuccessAttrApply(request.getUserName());
        }
        cnt++;
        return Result.okWithData(null);
    }

    //@PostMapping("/decryption")
    @PostMapping("/decrypt")
    public Result<String> decContent(@RequestBody @Validated DecryptContentRequest request) {
        return Result.okWithData(
                contentService.decryptContent(request.getCipher(), request.getFileName()));
    }

    @GetMapping("/list")
    public Result<PlatContentsResponse> queryContents(String fromUserName, String tag,
                                                      int pageSize, String bookmark) {
       // fromUserName = "深圳市";
        PlatContentsResponse res = contentService.queryPlatContents(fromUserName, tag, pageSize, bookmark);
        return Result.okWithData(res);
    }

    @PostMapping("/testUpload")
    public Result<Object> encryptFile(@RequestBody @Validated ShareContentRequest request) throws IOException {
        //String fileNameHash = SecurityUtils.md5(request.getSharedfileName());
        String data = FileUtils.readFileToString(
                new File("atp/test/"+request.getSharedFileName()),
                StandardCharsets.UTF_8);
        FileUtils.write(new File(shareDataPath +request.getFileName()+"/"+ request.getSharedFileName()), data,
                StandardCharsets.UTF_8);
        request.setPlainContent(data);
        EncryptionResponse encryptionResponse = contentService.encContent2(request);

        FileUtils.write(new File(encryptDataPath +request.getFileName()+"/"+ request.getSharedFileName()), encryptionResponse.getCipher(),
                StandardCharsets.UTF_8);
        return Result.success();
    }


    @PostMapping("/upload")
    public Result<Object> upload(@ModelAttribute @Validated ShareContentRequest request,HttpServletRequest req) throws IOException {
        MultipartFile file = request.getFile();
        if(file.isEmpty()){
            return Result.internalError("file is empty");
        }
//        String ipAddress = SecurityUtils.getIpAddr(req);
////        request.setIp(ipAddress);
        if(request.getTags().get(2).equals("test")){
            request.setIp("101.201.49.180");
        }else{
            request.setIp("58.247.201.96");
        }
        //获取文件的原始名
        String filename = file.getOriginalFilename();
        System.out.println(filename);

        //连接mysql
        String driver = "com.mysql.cj.jdbc.Driver";	//驱动名，默认
        String url1 = "jdbc:mysql://localhost:3306/zdyf?serverTimezone=GMT&useSSL=false";	//将要访问的数据库名称zdyf
        String user = "root";	//mysql数据库用户名
        String password = "123456";	//mysql数据库用户密码


        //根据相对路径获取绝对路径
        File dest = new File(new File(shareDataPath).getAbsolutePath()+ "/" + request.getFileName()+"/"+filename);
        System.out.println(dest.getPath());
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }
        FileUtils.copyInputStreamToFile(file.getInputStream(), dest);
        //file.transferTo(dest);
        //FileCopyUtils.copy(file.getInputStream(),new FileOutputStream(dest));
        String data = FileUtils.readFileToString(
                dest,
                StandardCharsets.UTF_8);
        request.setPlainContent(data);
        request.setSharedFileName(filename);
        EncryptionResponse encryptionResponse = contentService.encContent2(request);

        FileUtils.write(new File(encryptDataPath +request.getFileName()+"/"+ filename), encryptionResponse.getCipher(),
                StandardCharsets.UTF_8);


        try {
            //加载驱动
            Class.forName(Driver);


            Connection conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword);	//创建connection对象,用来连接数据库
            if(!conn.isClosed())
                System.out.println("Succeed!");


            //获取时间与文件名哈希作为标识
            SHA256hash foo = new SHA256hash();
            String id = foo.getSHA(filename);

            //插入标识与文件路径
            String sql = "insert into DataId(id,path,permission) values(?,?,?)";
            java.sql.PreparedStatement pstmt = null;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, encryptDataPath +request.getFileName()+"/"+ filename);
            pstmt.setString(3, request.getPolicy());

            boolean row = pstmt.execute();
            System.out.println(row);

            //释放资源
            pstmt.close();
            conn.close();

        }catch(Exception e) {
            System.out.println("defeat!");
            System.out.println(e);
        }


        //System.out.println("驱动无法加载不是因为connection refused");

        //对接
        String url = baseUrl+"/attrpolicy";
        HttpClient client = HttpClients.createDefault();
        //默认post请求
        HttpPost post = new HttpPost(url);
        //拼接多参数
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            json.put("contentHash", SecurityUtils.md5(encryptionResponse.getCipherHash()));
            json.put("policy",request.getPolicy());
            json.put("uid",request.getFileName());
            array.put(request.getTags().get(0));
            array.put(request.getTags().get(1));
            array.put(request.getTags().get(2));
            array.put(request.getTags().get(3));
            json.put("tags",array);
            json.put("userIP",request.getIp());
            json.put("timestamp",encryptionResponse.getTimeStamp());
            String message = "" + json + "";
            post.addHeader("Content-type", "application/json; charset=utf-8");
            post.setHeader("Accept", "application/json");
            post.setEntity(new StringEntity(message, StandardCharsets.UTF_8));
            HttpResponse httpResponse = client.execute(post);
            HttpEntity entity = httpResponse.getEntity();
            System.err.println("状态:" + httpResponse.getStatusLine());
            System.err.println("参数:" + EntityUtils.toString(entity));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(request.getTags().get(2).equals("test")){
            revokeUserAttr(request.getFileName(),"位置异常");
            attrService.syncSuccessAttrApply(request.getFileName());
        }
        return Result.success();
    }

    //下载解密后的原文
    @GetMapping("/download")
    public  void download(String fileName, String sharedUser,HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取文件的绝对路径
        File dest = new File(new File(shareDataPath).getAbsolutePath()+ "/" + sharedUser+"/"+fileName);
        //获取输入流对象（用于读文件）
        FileInputStream fis = new FileInputStream(dest);
        //获取文件后缀（.txt）
        String extendFileName = fileName.substring(fileName.lastIndexOf('.'));
        //动态设置响应类型，根据前台传递文件类型设置响应类型
        //response.setContentType(request.getSession().getServletContext().getMimeType(extendFileName));
        //response.setContentType("content-type:octet-stream");
        response.setContentType("application/force-download");
        //设置响应头,attachment表示以附件的形式下载，inline表示在线打开
        response.setHeader("content-disposition","attachment;fileName="+ URLEncoder.encode(fileName,"UTF-8"));
        //获取输出流对象（用于写文件）
        ServletOutputStream os = response.getOutputStream();
        //下载文件,使用spring框架中的FileCopyUtils工具
        FileCopyUtils.copy(fis,os);
        //FileUtils.copyFile(dest,os);
        //return Result.success();
    }

    //下载密文
    @GetMapping("/cipher")
    public  void cipher(String userName,String fileName, String sharedUser,HttpServletRequest request, HttpServletResponse response) throws Exception {
        ChaincodeResponse resp = contentService.getCipher(userName, fileName, sharedUser);
        if(resp.isFailed()){
            throw new BaseException("download cipher error: " + resp.getMessage());
        }
        //保存密文
        FileUtils.write(new File(cipherDataPath +sharedUser+"/"+ fileName), resp.getMessage(),
                StandardCharsets.UTF_8);

        //获取文件的绝对路径
        File dest = new File(new File(cipherDataPath).getAbsolutePath()+ "/" + sharedUser+"/"+fileName);
        //获取输入流对象（用于读文件）
        FileInputStream fis = new FileInputStream(dest);
        //获取文件后缀（.txt）
        //String extendFileName = fileName.substring(fileName.lastIndexOf('.'));
        //动态设置响应类型，根据前台传递文件类型设置响应类型
        //response.setContentType(request.getSession().getServletContext().getMimeType(extendFileName));
        //response.setContentType("content-type:octet-stream");
        response.setContentType("application/force-download");
        //设置响应头,attachment表示以附件的形式下载，inline表示在线打开
        response.setHeader("content-disposition","attachment;fileName="+ URLEncoder.encode(fileName,"UTF-8"));
        //获取输出流对象（用于写文件）
        ServletOutputStream os = response.getOutputStream();
        //下载文件,使用spring框架中的FileCopyUtils工具
        FileCopyUtils.copy(fis,os);
        //FileUtils.copyFile(dest,os);
        //return Result.success();
    }


    //撤销用户所有属性
    public void revokeUserAttr(String userName,String msg) {
        DABEUser user = dabeService.getUser(userName);
        for(String attrName:user.getAppliedAttrMap().keySet()){
            RevokeUserAttrRequest request = new RevokeUserAttrRequest();
            request.setToUserName(userName);
            request.setAttrName(attrName);
            String owner = attrName.split(":")[0];
            request.setUserName(owner);
            request.setRemark(msg);
            ChaincodeResponse chaincodeResponse = attrService.revokeAttr(request);
            //对接
            String url = baseUrl+"/addattr";
            HttpClient client = HttpClients.createDefault();
            //默认post请求
            HttpPost post = new HttpPost(url);
            //拼接多参数
            JSONObject json = new JSONObject();
            try {

                json.put("result","revocation");
                json.put("channel_name", user.getChannel());
                json.put("fromUserName",request.getUserName());
                //json.put("fromOrgName",request.getToOrgName());
                json.put("toUserName",request.getToUserName());
                json.put("attrName",request.getAttrName());
                json.put("timestamp",new Date().toString());
                String message = "" + json + "";
                post.addHeader("Content-type", "application/json; charset=utf-8");
                post.setHeader("Accept", "application/json");
                post.setEntity(new StringEntity(message, StandardCharsets.UTF_8));
                HttpResponse httpResponse = client.execute(post);
                HttpEntity entity = httpResponse.getEntity();
                System.err.println("状态:" + httpResponse.getStatusLine());
                System.err.println("参数:" + EntityUtils.toString(entity));

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
