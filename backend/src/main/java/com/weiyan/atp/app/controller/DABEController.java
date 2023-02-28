package com.weiyan.atp.app.controller;

import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.service.DABEService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

/**
 * @author : 魏延thor
 * @since : 2020/6/2
 */
@RestController
@Slf4j
@RequestMapping("/dabe")
@CrossOrigin//支持跨域访问
public class DABEController {
    private final DABEService dabeService;

    public DABEController(DABEService dabeService) {
        this.dabeService = dabeService;
    }

    @GetMapping("/user")
    public Result<DABEUser> getUser(String fileName) {
        return handleUser(dabeService.getUser(fileName));
    }

    //增加密码判断
    @PostMapping("/user2")
    public Result<DABEUser> getUser2(String fileName,String password) {
        return handleUser(dabeService.getUser2(fileName,password));
    }

    @PostMapping("/user3")
    public Result<DABEUser> getUser3(String fileName,String cert) {
        return handleUser1(dabeService.getUser3(fileName,cert));
    }

    @PostMapping("/user")
    public Result<DABEUser> createUser(String fileName, String userName, String userType,String password){
        return handleUser(dabeService.createUser(fileName, userName, userType,"myc", password));
    }

    @PostMapping("/user/attr")
    public Result<DABEUser> declareAttr(String fileName, String attrName){
        return handleUser(dabeService.declareAttr(fileName, attrName));
    }

    private Result<DABEUser> handleUser(DABEUser user) {
        if (user == null) {
            return Result.internalError("no user or password error");
        } else {
            return Result.okWithData(user);
        }
    }
    private Result<DABEUser> handleUser1(DABEUser user) {
        if (user == null) {
            return Result.internalError("The certificate is invalid or expired");
        } else {
            return Result.okWithData(user);
        }
    }

    @GetMapping("/user/apply")
    public Result<Object> approveAttrApply(String fileName, String attrName, String toUserName){
        return dabeService.approveAttrApply(fileName, attrName, toUserName)
            .getResult(str -> str);
    }
}
