package com.weiyan.atp.app.controller;

import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.service.DABEService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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
@Tag(name = "DABE接口", description = "基于属性的加密用户管理接口")
public class DABEController {
    private final DABEService dabeService;

    @Value("${atp.pattern.attr}")
    private String AttrPattern;

    public DABEController(DABEService dabeService) {
        this.dabeService = dabeService;
    }

    @Operation(summary = "获取用户信息", description = "根据文件名获取用户信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息",
                    content = @Content(schema = @Schema(implementation = Result.class))),
        @ApiResponse(responseCode = "500", description = "用户不存在或服务器错误")
    })
    @GetMapping("/user")
    public Result<DABEUser> getUser(
            @Parameter(description = "用户文件名", required = true) @RequestParam String fileName) {
        return handleUser(dabeService.getUser(fileName));
    }

    @Operation(summary = "验证用户密码", description = "根据文件名和密码验证用户")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "密码验证成功",
                    content = @Content(schema = @Schema(implementation = Result.class))),
        @ApiResponse(responseCode = "500", description = "用户不存在或密码错误")
    })
    @PostMapping("/user2")
    public Result<DABEUser> getUser2(
            @Parameter(description = "用户文件名", required = true) @RequestParam String fileName,
            @Parameter(description = "用户密码", required = true) @RequestParam String password) {
        return handleUser(dabeService.getUser2(fileName, password));
    }

    @Operation(summary = "用户验证干运行", description = "用户验证的干运行模式，不实际验证密码")
    @GetMapping("/user2_dry_run")
    public Result<DABEUser> getUser2DryRun(
            @Parameter(description = "用户文件名") @RequestParam String filename,
            @Parameter(description = "用户密码") @RequestParam String password) {
        dabeService.getUser2DryRun(filename, password);
        return handleUserDryRun();
    }

    @Operation(summary = "批量用户验证干运行", description = "批量执行用户验证的干运行模式，用于性能测试")
    @GetMapping("/user2_batch_dry_run")
    public Result<DABEUser> getUser2BatchDryRun(
            @Parameter(description = "批处理大小", required = true) @RequestParam int batch_size) {
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

    @Operation(summary = "通过证书验证用户", description = "根据文件名和证书验证用户")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "证书验证成功",
                    content = @Content(schema = @Schema(implementation = Result.class))),
        @ApiResponse(responseCode = "500", description = "证书无效或已过期")
    })
    @PostMapping("/user3")
    public Result<DABEUser> getUser3(
            @Parameter(description = "用户文件名", required = true) @RequestParam String fileName,
            @Parameter(description = "用户证书", required = true) @RequestParam String cert) {
        return handleUser1(dabeService.getUser3(fileName, cert));
    }

    @Operation(summary = "创建用户", description = "创建新用户并存储到数据库和区块链")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户创建成功",
                    content = @Content(schema = @Schema(implementation = Result.class))),
        @ApiResponse(responseCode = "500", description = "用户创建失败")
    })
    @PostMapping("/user")
    public Result<DABEUser> createUser(
            @Parameter(description = "用户文件名", required = true) @RequestParam String fileName,
            @Parameter(description = "用户名", required = true) @RequestParam String userName,
            @Parameter(description = "用户类型", required = true) @RequestParam String userType,
            @Parameter(description = "用户密码", required = true) @RequestParam String password) {
        return handleUser(dabeService.createUser(fileName, userName, userType, "myc", password));
    }

    @Operation(summary = "声明属性", description = "为用户声明属性")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "属性声明成功",
                    content = @Content(schema = @Schema(implementation = Result.class))),
        @ApiResponse(responseCode = "500", description = "属性声明失败或属性名不合法")
    })
    @PostMapping("/user/attr")
    public Result<DABEUser> declareAttr(
            @Parameter(description = "用户文件名", required = true) @RequestParam String fileName,
            @Parameter(description = "属性名称", required = true) @RequestParam String attrName) {
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

    @Operation(summary = "审批属性申请", description = "审批其他用户的属性申请")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "审批成功",
                    content = @Content(schema = @Schema(implementation = Result.class))),
        @ApiResponse(responseCode = "500", description = "审批失败")
    })
    @GetMapping("/user/apply")
    public Result<Object> approveAttrApply(
            @Parameter(description = "审批者文件名", required = true) @RequestParam String fileName,
            @Parameter(description = "属性名称", required = true) @RequestParam String attrName,
            @Parameter(description = "申请者用户名", required = true) @RequestParam String toUserName) {
        return dabeService.approveAttrApply(fileName, attrName, toUserName).getResult(str -> str);
    }
}
