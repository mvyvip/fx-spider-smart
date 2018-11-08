package com.fx.spider.model;

import lombok.Data;

@Data
public class Page {

    private int page;

    private int limit;

    public Integer getPage() {
        return (this.page - 1) * this.limit;
    }

}

