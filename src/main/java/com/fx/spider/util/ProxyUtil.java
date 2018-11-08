package com.fx.spider.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fx.spider.constant.SystemConstant;
import com.fx.spider.model.OrderAccount;
import com.fx.spider.model.ProxyEntity;
import com.fx.spider.task.runnable.SpliderRunnable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Slf4j
public class ProxyUtil {

    private String key;

    private Proxy proxy;

//    private ProxyEntity proxyEntity;

    private volatile AtomicInteger index = new AtomicInteger(0);

    public ProxyUtil(String key) {
        this.key = key;
    }

    public synchronized void initIps() {
        try {
            Connection.Response execute = Jsoup.connect("http://webapi.http.zhimacangku.com/getip?num=" + 50 + "&type=2&pro=&city=0&yys=0&port=11&time=2&ts=1&ys=0&cs=0&lb=1&sb=0&pb=45&mr=2&regions=")
                    .timeout(5000)
                    .execute();
            System.out.println(execute.body());

            JSONArray datas = JSON.parseObject(execute.body()).getJSONArray("data");

            List<ProxyEntity> proxyEntities = new ArrayList<>();
            for (Object data : datas) {
                JSONObject jsonObject = JSONObject.parseObject(data.toString());
                proxyEntities.add(new ProxyEntity(jsonObject.getString("ip"), jsonObject.getInteger("port"), jsonObject.getDate("expire_time")));
            }
            proxyEntities.sort((proxyEntity1, proxyEntity2) -> Integer.parseInt((proxyEntity2.getExpireTime().getTime() - proxyEntity1.getExpireTime().getTime()) + ""));
            log.info("IP过期时间为：" + proxyEntities.get(0).getExpireTime().toLocaleString());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            initIps();
        }
    }

    public synchronized Proxy getProxy() {
        return proxy;
    }


    public synchronized Proxy getWdProxy() throws Exception {
        List<ProxyEntity> proxyEntities = new ArrayList<>();
        Connection.Response response = Jsoup.connect("http://h.wandouip.com/get/ip-list?pack=0&num=" + 1 + "&xy=2&type=2&lb=\\r\\n&mr=2&app_key=" + key)
                .timeout(SystemConstant.TIME_OUT)
                .ignoreContentType(true)
                .header("Content-Type", "application/json; charset=UTF-8")
                .execute();
        JSONArray datas = JSONObject.parseObject(response.body()).getJSONArray("data");
        for (Object data : datas) {
            JSONObject jsonObject = JSONObject.parseObject(data.toString());
            proxyEntities.add(new ProxyEntity(jsonObject.getString("ip"), jsonObject.getInteger("port"), jsonObject.getDate("expire_time")));
        }
        log.info("IP过期时间为：" + proxyEntities.get(0).getExpireTime().toLocaleString());
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyEntities.get(0).getIp(), proxyEntities.get(0).getPort()));
        return proxy;
    }

    public static void main(String[] args) throws Exception {
/*
        Proxy proxy = new ProxyUtil("f0c35c13b2fffac65e411939bc2de921").getProxy();
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            Jsoup.connect("http://test.abuyun.com/proxy.php")
                    .timeout(10000)
                    .proxy(proxy)
                    .execute().body();
            System.out.println(System.currentTimeMillis() - start);
        }
*/

        OrderAccount orderAccount = new OrderAccount("13648045607", "li5201314");
        SpliderRunnable spliderRunnable = new SpliderRunnable(orderAccount, "W1", "https://mall.phicomm.com/cart-fastbuy-14-1.html", "23900", "f0c35c13b2fffac65e411939bc2de921");
        spliderRunnable.run();

    }


}
