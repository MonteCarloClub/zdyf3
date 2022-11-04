package com.weiyan.atp.data.response.chaincode.plat;

import lombok.Data;

import java.util.List;

/**
 * @author : 魏延thor
 * @since : 2020/6/14
 */
@Data
public class BaseListResponse<T> {
    private List<CCResult<T>> result;

    private MetaData responseMetadata;

    @Data
    public static class CCResult<T> {
        private String key;
        private T record;
    }

    @Data
    public static class MetaData {
        private String recordsCount;
        private String bookmark;
    }
}
