package com.fx.spider.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViewData {

    private int code;

    private Object data;

    private int total;

    private String message;

}
