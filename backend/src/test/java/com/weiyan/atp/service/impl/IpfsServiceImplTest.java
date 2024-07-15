//package com.weiyan.atp.service.impl;
//
//import com.weiyan.atp.service.IpfsService;
//import org.junit.Test;
//
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.IOException;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class IpfsServiceImplTest {
//
//    @Autowired
//    private IpfsService ipfsService;
//
//    // 上传
//    @Test
//    public void uploadIpfs() throws IOException {
//        byte[] data = "hello world".getBytes();
//        String hash = ipfsService.uploadToIpfs("C:\\Users\\90744\\Desktop\\demo.txt");
//        // Qmf412jQZiuVUtdgnB36FXFX7xg5V6KEbSJ4dpQuhkLyfD
//        System.out.println(hash);
//    }
//
//    // 下载
//    @Test
//    public void downloadIpfs() {
//        String hash = "QmNbRcSBB71hDXymmGqMtDa4pstnANTx9nH7rMABw6mhMz";
//        byte[] data = ipfsService.downFromIpfs(hash);
//        System.out.println(new String(data));
//    }
//
//
//}
//
