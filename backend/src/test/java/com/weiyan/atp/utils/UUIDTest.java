package com.weiyan.atp.utils;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * @author Eric
 * @date 2021/9/5 13:50
 */
@Slf4j
public class UUIDTest {
    @Test
    public void test(){
        UUID uuid = UUID.nameUUIDFromBytes("123".getBytes());
        System.out.println(uuid);
        System.out.println(uuid.toString().length());
    }

    @Test
    public void test2(){
        Date date = new Date();
        System.out.println(date.toString());
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.getTime().toString());
    }

}
