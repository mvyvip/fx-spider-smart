package com.fx.spider.util;

import com.fx.spider.model.OrderAccount;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CookieUtils {

    public static volatile ConcurrentHashMap<OrderAccount, Map<String, String>> cookiesMap = new ConcurrentHashMap<>();

    public static synchronized void addCookies(OrderAccount orderAccount, Map<String, String> cookies) {
        for (Map.Entry<OrderAccount, Map<String, String>> entry : cookiesMap.entrySet()) {
            if(entry.getKey().getPhone().equals(orderAccount.getPhone())) {
                entry.getKey().setProxy(orderAccount.getProxy());
                entry.setValue(cookies);
                return;
            }
        }
        cookiesMap.put(orderAccount, cookies);
    }

    public static synchronized ConcurrentHashMap<OrderAccount, Map<String, String>> getCookies() {
        return cookiesMap;
    }

}
