package com.fx.spider.mapper;

import com.fx.spider.model.OrderAccount;
import com.fx.spider.model.Page;
import java.util.List;

public interface AccountMapper {

    List<OrderAccount> findByStatus(int status);

    List<OrderAccount> findAll();

    String findConfigByKey(String goodsUrl);

    List<OrderAccount> findPage(Page page);

    void updateStatus(String phone);
}
