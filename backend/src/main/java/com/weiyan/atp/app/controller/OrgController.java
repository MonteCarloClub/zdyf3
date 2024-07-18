package com.weiyan.atp.app.controller;

import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.OrgApplyTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.PlatOrgApply;
import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.data.request.web.ApproveOrgApplyRequest;
import com.weiyan.atp.data.request.web.CreateOrgRequest;
import com.weiyan.atp.data.request.web.DeclareOrgAttrRequest;
import com.weiyan.atp.service.OrgRepositoryService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : 魏延thor
 * @since : 2020/6/16
 */
@RequestMapping("/org")
@RestController
@Slf4j
@CrossOrigin //支持跨域访问
public class OrgController {

    @Value("${atp.devMode.couchdbUrl}")
    private String couchdbUrl;

    @Value("${atp.pattern.name}")
    private String NamePattern;
    @Value("${atp.pattern.attr}")
    private String AttrPattern;

    private final OrgRepositoryService orgRepositoryService;

    public OrgController(OrgRepositoryService orgRepositoryService) {
        this.orgRepositoryService = orgRepositoryService;
    }

    /**
     * 申请创建组织
     */
    @PostMapping("/apply/creation")
    public Result<Object> applyCreateOrg(@RequestBody @Validated CreateOrgRequest request) {
//        return orgRepositoryService.applyCreateOrg2(request).getResult(str -> str);
        // [br]增加：组织名要符合apt.pattern.name的格式
        System.out.println("[br]in OrgController.applyCreateOrg(): NamePattern = " + NamePattern);
        if (!Pattern.matches(NamePattern, request.getOrgName())) {
            return Result.internalError("组织名不合法："+request.getOrgName()+"（应该由大小写字母、数字或汉字组成）");
        }
        // [br]增加：检查组织名中不能出现"AND"和"OR"，否则加解密会出问题
        Matcher matcher = Pattern.compile("(AND)|(OR)").matcher(request.getOrgName());
        if (matcher.find()) {
            return Result.internalError("组织名不能包含\"AND\"和\"OR\"字样");
        }
        // [br]改：改为，申请创建的用户，创建之后，自动同意
        ChaincodeResponse response = orgRepositoryService.applyCreateOrg2(request);
        approveCreateOrgApply2(ApproveOrgApplyRequest.builder()
                .fileName(request.getFileName())
                .orgName(request.getOrgName())
                .build());
        return response.getResult(str -> str);
    }

    /**
     * 审批/同意加入组织
     * 自动提交分享给其他人的秘密
     */
    @PostMapping("/apply/creation/approval")
    public Result<Object> approveCreateOrgApply(@RequestBody @Validated ApproveOrgApplyRequest request) {
        // [br]增加：catch BaseException，获取其中的提示信息，返回给前端
        try {
            orgRepositoryService.approveOrgApply2(OrgApplyTypeEnum.CREATION, request);
        } catch (BaseException be) {
            // 如果是BaseException，则承载的是链码上的提示信息，这里做一下处理
            if (be.getMessage().equals("generate share in dabe error: already has this org")) {
                // 是 2. dabe生成对应的秘密 这一步的报错，且报错信息是“已经有该属性”，那就是说，调用者在重复加入组织，弹出提示
                return Result.ResultWithMessage("您已经加入组织");
            }
            // 如果是其他错误，还是直接扔出去
            throw be;
        }
        return Result.success();
    }

    // [br]跟approveCreateOrgApply逻辑一样。用于给applyCreateOrg调用
    public Result<Object> approveCreateOrgApply2(ApproveOrgApplyRequest request) {
        orgRepositoryService.approveOrgApply2(OrgApplyTypeEnum.CREATION, request);
        return Result.success();
    }

    /**
     * 申请声明属性
     */
    @PostMapping("/apply/attribute")
    public Result<Object> applyDeclareAttr(@RequestBody @Validated DeclareOrgAttrRequest request) {
//        return orgRepositoryService.applyDeclareOrgAttr2(request).getResult(str -> str);
        // [br]增加：属性名要符合apt.pattern.attr的格式
        System.out.println("[br]in OrgController.applyDeclareAttr(): AttrPattern = " + AttrPattern);
        if (!Pattern.matches(AttrPattern, request.getAttrName())) {
            return Result.internalError("属性名不合法："+request.getAttrName()+"（应该由大小写字母、数字或汉字组成）");
        }
        // [br]增加：检查属性名中不能出现"AND"和"OR"，否则加解密会出问题
        Matcher matcher = Pattern.compile("(AND)|(OR)").matcher(request.getAttrName());
        if (matcher.find()) {
            return Result.internalError("属性名不能包含\"AND\"和\"OR\"字样");
        }
        // [br]改：同组织创建，改为发起声明的用户自动同意
        ChaincodeResponse response =  orgRepositoryService.applyDeclareOrgAttr2(request);
        approveDeclareAttrApply2(ApproveOrgApplyRequest.builder()
                .fileName(request.getFileName())
                .orgName(request.getOrgName())
                .build());
        return response.getResult(str -> str);
    }

    /**
     * 审批/同意声明属性
     * 自动提交分享给其他人的秘密
     */
    @PostMapping("/apply/attribute/approval")
    public Result<Object> approveDeclareAttrApply(@RequestBody @Validated ApproveOrgApplyRequest request) {
        orgRepositoryService.approveOrgApply2(OrgApplyTypeEnum.ATTRIBUTE, request);
        return Result.success();
    }

    // [br]跟approveDeclareAttrApply一样，用于给applyDeclareAttr调用
    public Result<Object> approveDeclareAttrApply2(ApproveOrgApplyRequest request) {
        orgRepositoryService.approveOrgApply2(OrgApplyTypeEnum.ATTRIBUTE, request);
        return Result.success();
    }

    /**
     * 检查是否能生成part-pk，即其他成员是不是都给自己分享了秘密
     * 创建组织/声明属性
     */
    @GetMapping("/part-pk")
    public Result<Object> canGeneratePartPk() {
        return Result.success();
    }

    /**
     * 提交自己生成的part pk
     * 创建组织/声明属性
     */
    @PostMapping("/part-pk")
    public Result<Object> submitPartPk(String type, String orgName, String fileName, String attrName) {
        orgRepositoryService.submitPartPk2(OrgApplyTypeEnum.valueOf(type), orgName, fileName, attrName);
        return Result.success();
    }

    /**
     * 让合约整合part pk生成组织公钥或属性公钥
     * 创建组织/声明属性
     */
    @PostMapping("/complete-pk")
    public Result<Object> mixPartPk(String type, String orgName, String attrName, String fileName) {
        orgRepositoryService.mixPartPk2(OrgApplyTypeEnum.valueOf(type), orgName, attrName, fileName);
        return Result.success();
    }

    /**
     * 查询组织
     */
    @GetMapping("/")
    public Result<Object> queryOrg(String orgName) {
        return Result.okWithData(orgRepositoryService.queryOrg(orgName));
    }

    /**
     * 查询组织申请或组织属性申请
     */
    @GetMapping("/apply")
    public Result<PlatOrgApply> queryOrgApply(String orgName, String type, String attrName) {
        return Result.okWithData(
            orgRepositoryService.queryOrgApply(orgName, OrgApplyTypeEnum.valueOf(type), attrName));
    }
}
