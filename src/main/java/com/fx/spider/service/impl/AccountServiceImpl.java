package com.fx.spider.service.impl;

import com.fx.spider.mapper.AccountMapper;
import com.fx.spider.model.OrderAccount;
import com.fx.spider.model.Page;
import com.fx.spider.service.AccountService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public List<OrderAccount> findByStatus() {
        return accountMapper.findByStatus(1);
    }

    @Override
    public List<OrderAccount> findAll() {
        return accountMapper.findAll();
    }

    @Override
    public String findConfigByKey(String goodsUrl) {
        return accountMapper.findConfigByKey(goodsUrl);
    }

    @Override
    public List<OrderAccount> findPage(Page page) {
        return accountMapper.findPage(page);
    }
}
