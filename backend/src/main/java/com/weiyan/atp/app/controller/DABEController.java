package com.weiyan.atp.app.controller;

import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.service.DABEService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Attr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Value("${atp.pattern.attr}")
    private String AttrPattern;

    public DABEController(DABEService dabeService) {
        this.dabeService = dabeService;
    }

    @GetMapping("/user")
    public Result<DABEUser> getUser(String fileName) {
        return handleUser(dabeService.getUser(fileName));
    }

    //增加密码判断
    @PostMapping("/user2")
    public Result<DABEUser> getUser2(String fileName, String password) {
        return handleUser(dabeService.getUser2(fileName, password));
    }

    @GetMapping("/user2_dry_run")
    public Result<DABEUser> getUser2DryRun(String filename, String password) {
        dabeService.getUser2DryRun(filename, password);
        return handleUserDryRun();
    }

    @GetMapping("/user2_batch_dry_run")
    public Result<DABEUser> getUser2BatchDryRun(int batch_size) {
        String filename = "filename_", password = "password_";
        long loTimestamp = System.currentTimeMillis();
        int logStep = batch_size / 10;
        for (int i = 0; i < batch_size; i++) {
            dabeService.getUser2DryRun(filename + i, password + i);
            if (i > 0 && i % logStep == 0) {
                long miTimestamp = System.currentTimeMillis();
                System.out.printf("/user2_batch dry run: %d user(s) handled in %d ms%n", i, miTimestamp - loTimestamp);
            }
        }
        long hiTimestamp = System.currentTimeMillis();
        System.out.printf("/user2_batch dry run: %d user(s) handled in %d ms%n", batch_size, hiTimestamp - loTimestamp);
        return handleUserDryRun();
    }

    @PostMapping("/user3")
    public Result<DABEUser> getUser3(String fileName, String cert) {
        return handleUser1(dabeService.getUser3(fileName, cert));
    }

    @PostMapping("/user")
    public Result<DABEUser> createUser(String fileName, String userName, String userType, String password) {
        return handleUser(dabeService.createUser(fileName, userName, userType, "myc", password));
    }

    @PostMapping("/user/attr")
    public Result<DABEUser> declareAttr(String fileName, String attrName) {
        // [br]增加：属性名要符合apt.pattern.attr的格式
        System.out.println("[br]in DABEController.declareAttr(): AttrPattern = " + AttrPattern);
        if (!Pattern.matches(AttrPattern, attrName)) {
            return Result.internalError("属性名不合法："+attrName+"（应该由大小写字母、数字或汉字组成）");
        }
        // [br]增加：检查属性名中不能出现"AND"和"OR"，否则加解密会出问题
        Matcher matcher = Pattern.compile("(AND)|(OR)").matcher(attrName);
        if (matcher.find()) {
            return Result.internalError("属性名不能包含\"AND\"和\"OR\"字样");
        }
        return handleUser(dabeService.declareAttr(fileName, attrName));
    }

    private Result<DABEUser> handleUser(DABEUser user) {
        if (user == null) {
            return Result.internalError("no user or password error");
        } else {
            return Result.okWithData(user);
        }
    }

    private Result<DABEUser> handleUserDryRun() {
        return Result.success();
    }

    private Result<DABEUser> handleUser1(DABEUser user) {
        if (user == null) {
            return Result.internalError("The certificate is invalid or expired");
        } else {
            return Result.okWithData(user);
        }
    }

    @GetMapping("/user/apply")
    public Result<Object> approveAttrApply(String fileName, String attrName, String toUserName) {
        return dabeService.approveAttrApply(fileName, attrName, toUserName).getResult(str -> str);
    }
}
