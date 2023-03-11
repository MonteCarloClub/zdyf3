package com.weiyan.atp.app.controller;

import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.data.response.web.RsaKeysResponse;
import com.weiyan.atp.utils.CCUtils;
import com.weiyan.atp.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */

@RestController
@RequestMapping("/common")
@Slf4j
@CrossOrigin //支持跨域访问
public class CommonController {
    @Value("${atp.path.privateKey}")
    private String priKeyPath;
    @Value("${atp.path.publicKey}")
    private String pubKeyPath;

  //  @GetMapping("/rsa")
    public Result<Object> generateRsaKeys() {
        try {
            return Result.okWithData(SecurityUtils.generateKeyPair());
        } catch (Exception e) {
            log.warn("get rsa keys error", e);
            return Result.internalError("get rsa keys error");
        }
    }

 //   @PostMapping("/rsa")
    public Result<Object> generateRsaKeysFile(String fileName) {
        try {
//            if (FileUtils.getFile(priKeyPath + fileName).exists()) {
//                return Result.okWithData(RsaKeysResponse.builder()
//                    .priKey(
//                        FileUtils.readFileToString(FileUtils.getFile(priKeyPath + fileName),
//                            Charset.defaultCharset()))
//                    .pubKey(
//                        FileUtils.readFileToString(FileUtils.getFile(pubKeyPath + fileName),
//                            Charset.defaultCharset()))
//                    .build());
//            }
            RsaKeysResponse keysResponse = SecurityUtils.generateKeyPair();
            keysResponse.setPriKey(keysResponse.getPriKey().replace("\r\n", ""));
            keysResponse.setPriKey(keysResponse.getPriKey().replace("\n", ""));
            keysResponse.setPubKey(keysResponse.getPubKey().replace("\r\n", ""));
            keysResponse.setPubKey(keysResponse.getPubKey().replace("\n", ""));
            System.out.println("pubkeypubkey");
            System.out.println(keysResponse.getPriKey());
            String filePath1 = "atp/priKey/" + fileName;
            String filePath2 = "atp/pubKey/" + fileName;
            System.out.println("ppppppppppppppppppppppppppppppppppppppppppppppp");
            System.out.println(filePath1);
            CCUtils.saveDABEUser(filePath1,keysResponse.getPriKey());
            CCUtils.saveDABEUser(filePath2,keysResponse.getPubKey());

//            FileUtils.write(new File(priKeyPath + fileName), keysResponse.getPriKey(),
//                StandardCharsets.UTF_8);
//            FileUtils.write(new File(pubKeyPath + fileName), keysResponse.getPubKey(),
//                StandardCharsets.UTF_8);
            System.out.println("88888888888888888888888");
            return Result.okWithData(keysResponse);
        } catch (Exception e) {
            log.warn("generate rsa keys file error", e);
            return Result.internalError("generate rsa keys file error" + e.getMessage());
        }
    }
}
