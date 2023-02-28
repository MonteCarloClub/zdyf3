package com.weiyan.atp.data.response.web;

import com.weiyan.atp.data.bean.PlatContent;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author : 魏延thor
 * @since : 2020/6/14
 */
@Data
@Builder
public class PlatContentsResponse {
    private List<PlatContent> contents;

    private String bookmark;

    private int pageSize;
    private int count;
}
