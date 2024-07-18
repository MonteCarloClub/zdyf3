//package com.weiyan.atp.service.impl;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.weiyan.atp.AbeTrustPlatformApplication;
//import com.weiyan.atp.app.controller.SHA256hash;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.sql.*;
//
//@Slf4j
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = AbeTrustPlatformApplication.class)
//public class gBaseTest {
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4j.class);
//
//
//    @Value("${spring.datasource.url}")
//    private String gbaseUrl;
//
//    @Value("${spring.datasource.username}")
//    private String gbaseUser;
//
//    @Value("${spring.datasource.password}")
//    private String gbasePassword;
//
//    @Value("${spring.datasource.driver-class-name}")
//    private String Driver;
//
//
//    @Test
//    public void ConnctDriver(){
//        try {
//            Class.forName(Driver);  //加载驱动。
//        }catch( Exception e ){
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void AddTable() {
//
//        try {
//            //创建连接。
//            Connection conn = DriverManager.getConnection(gbaseUrl, gbaseUser, gbasePassword);
//            System.out.println("Connection succeed!");
//            Statement stmt = conn.createStatement();
//
//            int rc = stmt.executeUpdate("CREATE TABLE DataId(data_id VARCHAR(100)  not null primary key,path VARCHAR(100),permission VARCHAR(1000));");
//            stmt.close();
//            conn.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void DeleteTable() {
//
//
//        try {
//            //创建连接。
//            Connection conn = DriverManager.getConnection(gbaseUrl, gbaseUser, gbasePassword);
//            System.out.println("Connection succeed!");
//            Statement stmt = conn.createStatement();
//
//            int rc = stmt.executeUpdate("DROP TABLE DataId");
//
//            stmt.close();
//            conn.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void AddData(){
//
//        try {
//            //创建连接。
//            Connection conn = DriverManager.getConnection(gbaseUrl, gbaseUser, gbasePassword);
//            System.out.println("Connection succeed!");
//
//            //获取时间与文件名哈希作为标识
//            SHA256hash foo = new SHA256hash();
//            String id = foo.getSHA("测试.docx");
//
//
//            //插入标识与文件路径
//            String sql = "insert into DataId(data_id,path,permission) values(?,?,?)";
//            java.sql.PreparedStatement pstmt = null;
//
//            try {
//                pstmt = conn.prepareStatement(sql);
//                pstmt.setString(1, id);
//                pstmt.setString(2, "12345");
//                pstmt.setString(3, "(((深圳市气象局:气象 OR 深圳市气象局:紧急气象) AND 深圳市:福田区) OR 深圳市气象局:气象应急管理)");
//                int i = pstmt.executeUpdate();
//                if (i>0) {
//                    System.out.println("添加成功");
//                }else{
//                    System.out.println("添加失败");
//                }
//            }catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }finally{
//                try {
//                    conn.close();
//                    pstmt.close();
//                } catch (SQLException throwables) {
//                    throwables.printStackTrace();
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void SelectData(){
//        try{
//            //创建连接。
//            Connection conn = DriverManager.getConnection(gbaseUrl, gbaseUser, gbasePassword);
//            System.out.println("Connection succeed!");
//
//
//            //编写sql语句
//            String sql="SELECT data_id,path,permission From DataId";
//            PreparedStatement pstm = null;
//            ResultSet rs = null;
//
//           try {
//               //预编译需要执行的sql
//               pstm = conn.prepareStatement(sql);
//               //执行sql并返回查询结果
//               rs = pstm.executeQuery();
//
//               // 展开结果集数据库
//               while(rs.next()) {
//                   // 通过字段检索
//                   String id = rs.getString("data_id");
//                   String path = rs.getString("path");
//                   String policy = rs.getString("permission");
//
//                   // 输出数据
//                   System.out.print("data_id: " + id);
//                   System.out.print(", 路径: " + path);
//                   System.out.print(", 访问策略: " + policy);
//                   System.out.print("\n");
//               }
//           }catch (SQLException e) {
//               e.printStackTrace();
//           }finally {
//               try {
//                   // 关闭链接
//                   conn.close();
//                   pstm.close();
//                   rs.close();
//               } catch (SQLException throwables) {
//                   throwables.printStackTrace();
//               }
//
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//}
