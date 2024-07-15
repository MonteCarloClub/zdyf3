//package com.weiyan.atp.app.controller;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author BerryChen0w0
// * @date 2024-07-15 01:29
// */
//
//// [br]目前参照IpfsServiceImplTest.java来写
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class DBRecordControllerTest {
//    @Autowired
//    private DBRecordController dbRecordController;
//
//    // 注：24.7.15 1:42写测试用例时，u2已经获得u1的u1a1,u1a2授权，没有u1a3授权
//
//    @Test
//    public void testAddDBRecord() throws IOException {
//        String caseID = "TestCaseID";
//        Map<String, String> policies = new HashMap<>();
//        policies.put("TestField1", "(u1:u1a1 AND u1:u1a2)");
//        policies.put("TestField2", "((u1:u1a1 OR u1:u1a2) AND u1:u1a3)");
//        policies.put("TestField3", "(u1:u1a1 OR u1:u1a3)");
//        String userID = "u1";
////        Reselt<Object> result = dbRecordController.addDBRecord(caseID, policies, userID);
//        dbRecordController.addDBRecord(caseID, policies, userID);
//        return;     // 之后需要检查本地文件，atp/data/dbRecord/这里有没有对应的记录
//    }
//
//    @Test
//    public void testQueryDBRecord() throws IOException {
//        // TODO
//        String userID = "u2";
//        String caseID = "TestCaseID";
//        // 为方便，暂时把queryDBRecord函数改成返回List<String>的函数
//        List<String> accessibleFields = dbRecordController.queryDBRecord(userID, caseID);
//        System.out.println(accessibleFields);
//        return;
//    }
//}
