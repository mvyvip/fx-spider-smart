package com.fx.spider.service;

import com.fx.spider.model.OrderAccount;
import com.fx.spider.model.Page;
import java.util.List;

public interface AccountService {

    List<OrderAccount> findByStatus();

    List<OrderAccount> findAll();

    String findConfigByKey(String goodsUrl);

    List<OrderAccount> findPage(Page page);

    void updateStatus(String phone);
}
