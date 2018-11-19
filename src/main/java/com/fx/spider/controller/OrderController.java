package com.fx.spider.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fx.spider.constant.SystemConstant;
import com.fx.spider.model.OrderAccount;
import com.fx.spider.model.Page;
import com.fx.spider.model.ProxyEntity;
import com.fx.spider.model.ViewData;
import com.fx.spider.service.AccountService;
import com.fx.spider.util.CookieUtils;
import com.fx.spider.util.ProxyUtil;
import com.fx.spider.util.UserAgentUtil;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@Slf4j
@RestController
@SuppressWarnings("all")
public class OrderController {

    private String key = "f0c35c13b2fffac65e411939bc2de921";

    @Autowired
    private AccountService accountService;

    @GetMapping("/cookies")
    public ViewData cookies(){
        return ViewData.builder().total(CookieUtils.getCookies().size()).data(CookieUtils.getCookies()).build();
    }


    @GetMapping("/fast/orders")
    public Object fastFindAllOrder(Integer page, Integer limit) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<OrderAccount> orders = new ArrayList<>();
        List<OrderAccount> needGet = new ArrayList<>();

        AtomicInteger atomicInteger = new AtomicInteger(0);
        ConcurrentHashMap<OrderAccount, Map<String, String>> cookies = CookieUtils.getCookies();

        Set<Map.Entry<OrderAccount, Map<String, String>>> entries = cookies.entrySet();
        CountDownLatch countDownLatch = new CountDownLatch(entries.size());
        for (Map.Entry<OrderAccount, Map<String, String>> entry : entries) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Connection.Response response = getOrders2(entry.getKey().getProxy(), entry.getValue(), 0);
                        if(response != null && !response.body().contains("暂无")) {
                            Document document = Jsoup.parse(response.body());
                            Elements tables = document.getElementsByTag("table");
                            for (int i = 1; i < tables.size(); i++) {
                                OrderAccount order = new OrderAccount();
                                OrderAccount orderAccount = entry.getKey();
                                Element table = tables.get(i);

                                Elements as = table.getElementsByTag("a");
                                order.setPayBase64("---");
                                for (Element a : as) {
                                    if(a.text().contains("立即付款")) {
//                                        String base64 = getBase64(a, entry.getValue(), 0);
                                        String base64 = getPayUrl(a, entry.getValue(), 0, entry.getKey().getProxy());
                                        order.setPayBase64(base64);
                                    }
                                }

                                Elements elements = table.getElementsByClass("text-muted");
                                order.setPhone(orderAccount.getPhone());
                                order.setPassword(orderAccount.getPassword());
                                order.setGoodsName(elements.get(1).text().trim());
                                order.setOrderNo(elements.get(0).text().replaceAll("  ", "").trim());
                                order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                order.setStatus(table.getElementsByTag("span").text().trim());
//                                order.setCookie(entry.getValue().get("_SID").toString());
                                Date date = DateUtils.parseDateStrictly(order.getOrderCreateDate(), "yyyy-MM-dd HH:mm");
                                if(date.getTime() > DateUtils.addDays(new Date(), -7).getTime() && !order.getStatus().equals("已取消")) {
                                    Elements aElements = table.getElementsByTag("a");
                                    order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                    orders.add(order);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            }).start();
        }

        countDownLatch.await();

        result.put("code", 0);
        result.put("count", orders.size());
        result.put("data", orders);

        return result;
    }

    private String getPayUrl(Element a, Map<String, String> cookies, Integer tryCount, Proxy proxy) {
        try {
            String payUrl = "https://mall.phicomm.com/" + a.attr("href").replace("payment", "dopayment");
            Document dc = Jsoup.connect(payUrl)
                .timeout(60 * 1000)
                .proxy(proxy).validateTLSCertificates(false)
                .cookies(cookies).userAgent(UserAgentUtil.get())
                .execute().parse();
            Elements inputs = dc.getElementsByTag("input");
            StringBuilder sb = new StringBuilder("https://mapi.alipay.com/gateway.do?_input_charset=utf-8");
            for (Element input : inputs) {
                if(!input.attr("name").equals("_input_charset=utf-8")) {
                    sb.append("&" + input.attr("name") + "=" + input.val());
                }
            }
            return sb.toString();
        } catch (Exception e){
            log.error("获取二维码失败: " + e.getMessage());
            tryCount++;
            if (tryCount < 2) {
                return getPayUrl(a, cookies, tryCount, proxy);
            }
            return "---";
        }
    }

    private Connection.Response getOrders2(Proxy proxy, Map<String, String> cookies, Integer tryCount) {
        try {
            Connection.Response response = Jsoup.connect("https://mall.phicomm.com/index.php/my-orders.html")
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .timeout(60000)
                .userAgent(UserAgentUtil.get())
                .proxy(proxy).validateTLSCertificates(false)
                .cookies(cookies)
                .execute();
            return response;
        } catch (Exception e) {
            tryCount++;
            if(tryCount > 2) {
                return null;
            }
            return getOrders2(proxy, cookies, tryCount);
        }
    }



    @GetMapping("/orders")
    public Object findAllOrder(String name, String type, Page page) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<OrderAccount> orders = new ArrayList<>();
        List<OrderAccount> temp = new ArrayList<>();
        List<OrderAccount> orderAccountList = accountService.findPage(page);

        List<OrderAccount> faildList = new ArrayList<>();

        String[] values = accountService.findConfigByKey(SystemConstant.GOODS_URL).split("-");
        String goods = values[0];

        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < orderAccountList.size(); i++) {
            temp.add(orderAccountList.get(i));
            if(i%100 == 0) {
                List<ProxyEntity> wdProxy = new ProxyUtil(key).get50WdProxy();
                for (int j = 0; j < temp.size(); j++) {
                    OrderAccount ac = temp.get(j);
                    int finalI = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(wdProxy.get(finalI).getIp(), wdProxy.get(finalI).getPort()));
                                Map<String, String> cookies = getCookies(ac.getPhone(), ac.getPassword(), 0, proxy);
                                ac.setVc("0");
                                if(MapUtils.isEmpty(cookies)) {
                                    ac.setRemark(ac.getRemark() + "---密码错误");
                                    orders.add(ac);
                                } else {
                                    String body = getOrders(cookies, proxy, 1);
                                    if(body.contains("暂无00")) {
                                        ac.setRemark(ac.getRemark() + "---" + "查单失败");
                                        orders.add(ac);
                                    } else {
                                        if(!body.contains("暂无")) {
                                            Document document = Jsoup.parse(body);
                                            Elements tables = document.getElementsByTag("table");
                                            boolean flag = true;
                                            for (int i = 1; i < tables.size(); i++) {
                                                OrderAccount order = new OrderAccount();
                                                Element table = tables.get(i);
                                                Elements elements = table.getElementsByClass("text-muted");
                                                ac.setGoodsName(elements.get(1).text().trim());
                                                ac.setOrderNo(elements.get(0).text().replaceAll("  ", "").trim());
                                                ac.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                                ac.setStatus(table.getElementsByTag("span").text().trim());
                                                Date date = DateUtils.parseDateStrictly(order.getOrderCreateDate(), "yyyy-MM-dd HH:mm");

                                                Elements as = table.getElementsByTag("a");
                                                for (Element a : as) {
                                                    if(a.text().contains("确认收货")) {
                                                        try {
                                                            String href = a.attr("href");
                                                            Thread.sleep(5000);
                                                            Connection.Response execute = Jsoup.connect("https://mall.phicomm.com" + href)
                                                                .cookies(cookies).userAgent(UserAgentUtil.get())
                                                                .timeout(SystemConstant.TIME_OUT)
                                                                .proxy(proxy).validateTLSCertificates(false)
                                                                .execute();
                                                            if(execute.body().contains("订单确认收货成功")) {
                                                                log.info(ac.getPhone() + "--- " + "收货成功");
                                                            } else {
                                                                log.info(ac.getPhone() + " ----" + ac.getPassword() + "--- " + "收货失败");
                                                            }
                                                        } catch (Exception e) {
                                                            log.info("收货失败--" + e.getMessage() + "----" + ac.getPhone() + "----" + ac.getPassword());
                                                        }
                                                    }
                                                }

                                                if(date.getTime() > DateUtils.addDays(new Date(), -15).getTime()) {
                                                    Elements aElements = table.getElementsByTag("a");
                                                    Thread.sleep(5000);
                                                    setDetail(cookies, aElements, order, 0, proxy);
                                                    order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                                    flag = false;

                                                    if(StringUtils.isNotEmpty(name) && !order.getAddress().contains(name)) {
                                                        continue;
                                                    }

                                                    if(StringUtils.isNotEmpty(type)) {
                                                        if(type.equals("1")  && !order.getLogisticsInfo().contains("签收")) {
                                                            continue;
                                                        } else if(type.equals("2") && order.getLogisticsInfo().contains("签收")) {
                                                            continue;
                                                        }
                                                    }
                                                    orders.add(order);
                                                }

                                            }
                                        }
                                    }

                                }
                            } catch (Exception e) {
                                System.out.println("查询失败---" + e.getMessage() + "---" + ac.getPhone() + "----" + ac.getPassword());
                            } finally {
                                System.err.println("当前处理的条数>>> " + atomicInteger.incrementAndGet());
                            }
                        }
                    }).start();

                    temp.clear();
                    Thread.sleep(15 * 1000);
                }
            }
        }

        List<ProxyEntity> wdProxy = new ProxyUtil(key).get50WdProxy();
        for (int i = 0; i < temp.size(); i++) {
            OrderAccount ac = temp.get(i);
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<String, String> cookies = getCookies(ac.getPhone(), ac.getPassword(), 0,
                            new Proxy(Proxy.Type.HTTP, new InetSocketAddress(wdProxy.get(finalI).getIp(), wdProxy.get(finalI).getPort())));
                        ac.setVc("0");
                        if(MapUtils.isEmpty(cookies)) {
                            ac.setRemark(ac.getRemark() + "---密码错误");
                            orders.add(ac);
                        } else {
                            String body = getOrders(cookies, new Proxy(Proxy.Type.HTTP, new InetSocketAddress(wdProxy.get(finalI).getIp(), wdProxy.get(finalI).getPort())), 1);
                            if(body.contains("暂无00")) {
                                ac.setRemark(ac.getRemark() + "---" + "查单失败");
                                orders.add(ac);
                            } else {
                                if(!body.contains("暂无")) {
                                    Document document = Jsoup.parse(body);
                                    Elements tables = document.getElementsByTag("table");
                                    boolean flag = true;
                                    for (int i = 1; i < tables.size(); i++) {
                                        OrderAccount order = new OrderAccount();
                                        Element table = tables.get(i);
                                        Elements elements = table.getElementsByClass("text-muted");
                                        ac.setGoodsName(elements.get(1).text().trim());
                                        ac.setOrderNo(elements.get(0).text().replaceAll("  ", "").trim());
                                        ac.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                        ac.setStatus(table.getElementsByTag("span").text().trim());
                                        Date date = DateUtils.parseDateStrictly(order.getOrderCreateDate(), "yyyy-MM-dd HH:mm");

                                        Elements as = table.getElementsByTag("a");
                                        for (Element a : as) {
                                            if(a.text().contains("确认收货")) {
                                                try {
                                                    String href = a.attr("href");
                                                    Thread.sleep(5000);
                                                    Connection.Response execute = Jsoup.connect("https://mall.phicomm.com" + href)
                                                        .cookies(cookies).userAgent(UserAgentUtil.get())
                                                        .timeout(SystemConstant.TIME_OUT)
                                                        .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(wdProxy.get(finalI).getIp(), wdProxy.get(finalI).getPort()))).validateTLSCertificates(false)
                                                        .execute();
                                                    if(execute.body().contains("订单确认收货成功")) {
                                                        log.info(ac.getPhone() + "--- " + "收货成功");
                                                    } else {
                                                        log.info(ac.getPhone() + " ----" + ac.getPassword() + "--- " + "收货失败");
                                                    }
                                                } catch (Exception e) {
                                                    log.info("收货失败--" + e.getMessage() + "----" + ac.getPhone() + "----" + ac.getPassword());
                                                }
                                            }
                                        }

                                        if(date.getTime() > DateUtils.addDays(new Date(), -15).getTime()) {
                                            Elements aElements = table.getElementsByTag("a");
                                            Thread.sleep(5000);
                                            setDetail(cookies, aElements, order, 0, new Proxy(Proxy.Type.HTTP, new InetSocketAddress(wdProxy.get(finalI).getIp(), wdProxy.get(finalI).getPort())));
                                            order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                            flag = false;

                                            if(StringUtils.isNotEmpty(name) && !order.getAddress().contains(name)) {
                                                continue;
                                            }

                                            if(StringUtils.isNotEmpty(type)) {
                                                if(type.equals("1")  && !order.getLogisticsInfo().contains("签收")) {
                                                    continue;
                                                } else if(type.equals("2") && order.getLogisticsInfo().contains("签收")) {
                                                    continue;
                                                }
                                            }
                                            orders.add(order);
                                        }

                                    }
                                }
                            }

                        }
                    } catch (Exception e) {
                        System.out.println("查询失败---" + e.getMessage() + "---" + ac.getPhone() + "----" + ac.getPassword());
                    } finally {
                        System.err.println("当前处理的条数>>> " + atomicInteger.incrementAndGet());
                    }
                }
            }).start();
        }


       /* for (OrderAccount orderAccount : orderAccountList) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ProxyUtil proxyUtil = new ProxyUtil(key);
                        Proxy wdProxy = proxyUtil.getWdProxy();
                        Map<String, String> cookies = getCookies(orderAccount.getPhone(), orderAccount.getPassword(), 0, wdProxy);
                        if(MapUtils.isEmpty(cookies)) {
                            System.out.println(orderAccount.getPhone() + "----" + orderAccount.getPassword() + "---登录失败了------######");
                            faildList.add(orderAccount);
                        }
                        if(MapUtils.isNotEmpty(cookies)) {
                            // String vc = getVc(cookies);
                            Thread.sleep(5000);
                            String vc = "0";
                            String body = getOrders(cookies, wdProxy, 1);
                            if(body.endsWith("00")) {
                                faildList.add(orderAccount);
                            }
                            orderAccount.setVc(vc);
                            if(body.contains("暂无")) {
                                needGet.add(orderAccount);
                                log.info(orderAccount.getPhone() + "----" + orderAccount.getPassword() + "----无订单");
                            } else {
                                Document document = Jsoup.parse(body);
                                Elements tables = document.getElementsByTag("table");

                                boolean flag = true;
                                for (int i = 1; i < tables.size(); i++) {
                                    OrderAccount order = new OrderAccount();
                                    order.setVc(vc);
                                    order.setRemark(orderAccount.getRemark());
                                    Element table = tables.get(i);

                                    Elements elements = table.getElementsByClass("text-muted");
                                    order.setPhone(orderAccount.getPhone());
                                    order.setPassword(orderAccount.getPassword());
                                    order.setGoodsName(elements.get(1).text().trim());
                                    order.setOrderNo(elements.get(0).text().replaceAll("  ", "").trim());
                                    order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                    order.setStatus(table.getElementsByTag("span").text().trim());
                                    Date date = DateUtils.parseDateStrictly(order.getOrderCreateDate(), "yyyy-MM-dd HH:mm");

                                    Elements as = table.getElementsByTag("a");
                                    for (Element a : as) {
                                        if(a.text().contains("确认收货")) {
                                            String href = a.attr("href");
                                            Connection.Response execute = Jsoup.connect("https://mall.phicomm.com" + href)
                                                    .cookies(cookies).userAgent(UserAgentUtil.get())
                                                    .timeout(SystemConstant.TIME_OUT)
                                                    .proxy(wdProxy).validateTLSCertificates(false)
                                                    .execute();
                                            if(execute.body().contains("订单确认收货成功")) {
                                                log.info(orderAccount.getPhone() + "--- " + "收货成功");
                                            } else {
                                                log.info(orderAccount.getPhone() + " ----" + orderAccount.getPassword() + "--- " + "收货失败");
                                            }
                                        }
                                    }


                                    if(date.getTime() > DateUtils.addDays(new Date(), -15).getTime()) {
                                        Elements aElements = table.getElementsByTag("a");
                                        Thread.sleep(5000);
                                        setDetail(cookies, aElements, order, 0, wdProxy);
                                        order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                        flag = false;

                                        if(StringUtils.isNotEmpty(name) && !order.getAddress().contains(name)) {
                                            continue;
                                        }

                                        if(StringUtils.isNotEmpty(type)) {
                                            if(type.equals("1")  && !order.getLogisticsInfo().contains("签收")) {
                                                continue;
                                            } else if(type.equals("2") && order.getLogisticsInfo().contains("签收")) {
                                                continue;
                                            }
                                        }
                                        orders.add(order);
                                    }
                                }
                                if(flag) {
                                    needGet.add(orderAccount);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("登录失败： " + e.getMessage());
                    } finally {
                        System.err.println("xxx>>> " + atomicInteger.incrementAndGet());
                    }
                }
            }).start();
            Thread.sleep(5200);
        }
*/
        while (!(atomicInteger.get() == orderAccountList.size())) {
            // 等待线程执行完毕
        }
        System.out.println("开始");

        result.put("code", 0);
        result.put("count", accountService.findAll().size());
        result.put("data", orders);

        return result;
    }

/*

    @GetMapping("/orders")
    public Object findAllOrder(String name, String type, Page page) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<OrderAccount> orders = new ArrayList<>();
        List<OrderAccount> needGet = new ArrayList<>();
        List<OrderAccount> orderAccountList = accountService.findPage(page);

        List<OrderAccount> faildList = new ArrayList<>();

        String[] values = accountService.findConfigByKey(SystemConstant.GOODS_URL).split("-");
        String goods = values[0];

        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (OrderAccount orderAccount : orderAccountList) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ProxyUtil proxyUtil = new ProxyUtil(key);
                        Proxy wdProxy = proxyUtil.getWdProxy();
                        Map<String, String> cookies = getCookies(orderAccount.getPhone(), orderAccount.getPassword(), 0, wdProxy);
                        if(MapUtils.isEmpty(cookies)) {
                            System.out.println(orderAccount.getPhone() + "----" + orderAccount.getPassword() + "---登录失败了------######");
                            faildList.add(orderAccount);
                        }
                        if(MapUtils.isNotEmpty(cookies)) {
                            // String vc = getVc(cookies);
                            Thread.sleep(5000);
                            String vc = "0";
                            String body = getOrders(cookies, wdProxy, 1);
                            if(body.endsWith("00")) {
                                faildList.add(orderAccount);
                            }
                            orderAccount.setVc(vc);
                            if(body.contains("暂无")) {
                                needGet.add(orderAccount);
                                log.info(orderAccount.getPhone() + "----" + orderAccount.getPassword() + "----无订单");
                            } else {
                                Document document = Jsoup.parse(body);
                                Elements tables = document.getElementsByTag("table");

                                boolean flag = true;
                                for (int i = 1; i < tables.size(); i++) {
                                    OrderAccount order = new OrderAccount();
                                    order.setVc(vc);
                                    order.setRemark(orderAccount.getRemark());
                                    Element table = tables.get(i);

                                    Elements elements = table.getElementsByClass("text-muted");
                                    order.setPhone(orderAccount.getPhone());
                                    order.setPassword(orderAccount.getPassword());
                                    order.setGoodsName(elements.get(1).text().trim());
                                    order.setOrderNo(elements.get(0).text().replaceAll("  ", "").trim());
                                    order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                    order.setStatus(table.getElementsByTag("span").text().trim());
                                    Date date = DateUtils.parseDateStrictly(order.getOrderCreateDate(), "yyyy-MM-dd HH:mm");

                                    Elements as = table.getElementsByTag("a");
                                    for (Element a : as) {
                                        if(a.text().contains("确认收货")) {
                                            String href = a.attr("href");
                                            Connection.Response execute = Jsoup.connect("https://mall.phicomm.com" + href)
                                                .cookies(cookies).userAgent(UserAgentUtil.get())
                                                .timeout(SystemConstant.TIME_OUT)
                                                .proxy(wdProxy).validateTLSCertificates(false)
                                                .execute();
                                            if(execute.body().contains("订单确认收货成功")) {
                                                log.info(orderAccount.getPhone() + "--- " + "收货成功");
                                            } else {
                                                log.info(orderAccount.getPhone() + " ----" + orderAccount.getPassword() + "--- " + "收货失败");
                                            }
                                        }
                                    }


                                    if(date.getTime() > DateUtils.addDays(new Date(), -15).getTime()) {
                                        Elements aElements = table.getElementsByTag("a");
                                        Thread.sleep(5000);
                                        setDetail(cookies, aElements, order, 0, wdProxy);
                                        order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                        flag = false;

                                        if(StringUtils.isNotEmpty(name) && !order.getAddress().contains(name)) {
                                            continue;
                                        }

                                        if(StringUtils.isNotEmpty(type)) {
                                            if(type.equals("1")  && !order.getLogisticsInfo().contains("签收")) {
                                                continue;
                                            } else if(type.equals("2") && order.getLogisticsInfo().contains("签收")) {
                                                continue;
                                            }
                                        }
                                        orders.add(order);
                                    }
                                }
                                if(flag) {
                                    needGet.add(orderAccount);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("登录失败： " + e.getMessage());
                    } finally {
                        System.err.println("xxx>>> " + atomicInteger.incrementAndGet());
                    }
                }
            }).start();
            Thread.sleep(5200);
        }

        while (!(atomicInteger.get() == orderAccountList.size())) {
            // 等待线程执行完毕
        }
        System.out.println("开始");

        System.out.println("===================================");
        faildList.stream().forEach(orderAccount -> {
            System.err.println(orderAccount.getPhone() + "----" + orderAccount.getPassword() + "----" + orderAccount.getVc());
        });
        System.out.println("===================================");

        needGet.stream().forEach(orderAccount -> {
            System.err.println(orderAccount.getPhone() + "----" + orderAccount.getPassword() + "----" + orderAccount.getVc());
        });

        result.put("code", 0);
        result.put("count", accountService.findAll().size());
        result.put("data", orders);

        return result;
    }
*/


    @GetMapping("/accounts")
    public Object accounts(String name, String type) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<OrderAccount> orders = new ArrayList<>();
        List<OrderAccount> orderAccountList = accountService.findAll();
        CountDownLatch countDownLatch = new CountDownLatch(orderAccountList.size());

        int i = 0;
        for (OrderAccount orderAccount : orderAccountList) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ProxyUtil proxyUtil = new ProxyUtil(key);
                        Proxy wdProxy = proxyUtil.getWdProxy();
                        Map<String, String> cookies = getCookies(orderAccount.getPhone(), orderAccount.getPassword(), 0, wdProxy);
                        orders.add(orderAccount);

                        if(cookies != null) {
                            setDefaultAddress(orderAccount, cookies, wdProxy);

                            orderAccount.setStatus2(orderAccount.getStatus());
                            if(MapUtils.isNotEmpty(cookies)) {
                                String vc = getVc(cookies, wdProxy);
                                orderAccount.setVc(vc);
                            }
                        }
                    } catch (Exception e) {
                        log.error("登录失败： " + e.getMessage());
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            }).start();
            System.err.println(++i);
            Thread.sleep(5300);
        }


        countDownLatch.await();
        System.out.println("开始");

        result.put("code", 0);
        result.put("count", orders.size());
        result.put("data", orders);

        return result;
    }

    private String getVc(Map<String, String> cookies, Proxy proxy) {
        try {
            Document parse = Jsoup.connect("https://mall.phicomm.com/my-vclist.html")
                .cookies(cookies)
                .userAgent(UserAgentUtil.get())
                .timeout(SystemConstant.TIME_OUT)
                .proxy(proxy).validateTLSCertificates(false)
                .execute().parse();
            String vc = parse.body().text().split("可用维C ")[1].split(" 冻结维C")[0];
            return vc;
        } catch (Exception e) {
            return "----";
        }
    }

    private void setDefaultAddress(OrderAccount orderAccount, Map<String, String> cookie, Proxy proxy) {
        try {
            Thread.sleep(3000);
            Document document = Jsoup.connect("https://mall.phicomm.com/my-receiver.html").method(Connection.Method.GET).cookies(cookie).timeout(SystemConstant.TIME_OUT)
                .proxy(proxy).validateTLSCertificates(false)
                .userAgent(UserAgentUtil.get())
                .timeout(SystemConstant.TIME_OUT)
                .execute().parse();
            Elements dts = document.getElementsByTag("dt");
            for (Element dt : dts) {
                if(dt.text().contains("默认")) {
                    String defaultAddress = dt.getElementsByTag("span").get(0).text() + document.getElementsByTag("dd").get(0).text();
                    orderAccount.setDefaultAddress(defaultAddress);
                }
            }
        } catch (Exception e) {
            log.error("获取默认地址失败：" + e.getMessage());
            orderAccount.setDefaultAddress("----");
        }
    }

    private void setDetail(Map<String, String> cookies, Elements aElements, OrderAccount order, Integer retryCount, Proxy proxy) {
        try {
            Document detailOrder = Jsoup.connect("https://mall.phicomm.com" + (aElements.get(aElements.size() - 1).attr("href")))
                    .cookies(cookies).userAgent(UserAgentUtil.get())
                    .proxy(proxy).validateTLSCertificates(false)
                    .timeout(SystemConstant.TIME_OUT).execute().parse();

            Elements dd = detailOrder.getElementsByTag("dd");
            order.setAddress(dd.get(2).text());
            for (Element element : dd) {
                if(StringUtils.isNotEmpty(element.attr("data-deliveryid"))) {
                    String body = getWuliu(element, cookies, proxy);
                    JSONObject jsonObject = JSONObject.parseObject(body);
                    order.setLogisticsNum(jsonObject.getJSONObject("data").getString("logi_no"));
                    JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("logi_log");
                    if(jsonArray.size() > 0) {
                        order.setLogisticsInfo(JSONObject.parseObject(jsonArray.get(0).toString()).getString("context"));
                    }
                }
            }
        } catch (Exception e){
            System.out.println("设置详情失败--" + cookies.get("_SID") + "---" + e.getMessage() + "####");
//            setDetail(cookies, aElements, order, retryCount, proxy);
        }
    }

    private String getWuliu(Element element, Map<String, String> cookies, Proxy proxy) {
        try {
            String body = Jsoup.connect("https://mall.phicomm.com/order-logistics_tracker-" + element.attr("data-deliveryid") + ".html")
                    .timeout(SystemConstant.TIME_OUT)
                    .proxy(proxy)
                    .cookies(cookies).userAgent(UserAgentUtil.get())
                    .header("X-Requested-With", "XMLHttpRequest")
                    .ignoreContentType(true)
                    .execute().body();
            return body;
        } catch (Exception e) {
            return "获取物流失败";
        }
    }
    public static String replaceByPrefix(String str, String start, String end, String replace) {
        if(!str.contains(start)) {
            System.out.println("start : " + start + " is not exist");
            return str;
        }
        if(!str.contains(end)) {
            System.out.println("end : " + end + " is not exist");
            return str;
        }
        StringBuilder sb = new StringBuilder();
        String s1 = sb.append(str.split(start)[0].substring(0, str.split(start)[0].length() - 9))
                .append(replace)
                .append(str.split(start)[1].split(end)[1]).toString();
        return s1;
    }
    private Map<String,String> getCookies(String phone, String password, int i, Proxy proxy) {
        try {
            Connection.Response response = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .header("Host", "mall.phicomm.com")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Referer", "https://mall.phicomm.com/passport-login.html")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .timeout(SystemConstant.TIME_OUT)
                    .proxy(proxy)
                    .ignoreHttpErrors(true)
                    .execute();

            Map<String, String> cookies = response.cookies();
            String body = response.body();
            cookies.put("__jsl_clearance", getck(body).split("=")[1]);
            return toLoginPage(proxy, cookies, phone, password);
        } catch (Exception e) {
//            log.error("获取 __jsl_clearance 失败，{}", e.getMessage());
            if(e.getMessage().equals("HTTP error fetching URL") || e.getMessage().equals("Read timed out")
                    || e.getMessage().equals("Connection refused: connect")
                    || e.getMessage().equals("Connection timed out: connect")) {
                System.out.println(("ip被封禁或过期，换ip中----" + e.getMessage()));
                try {
                    proxy = new ProxyUtil(key).getWdProxy();
                } catch (Exception e2) {
                    System.err.println(e2.getMessage());
                }
            }
            if(i++ > 30) {
                return null;
            }
            return getCookies(phone, password, i++, proxy);
        }
    }

    public static String getck(String s) throws Exception {
        StringBuilder sb = new StringBuilder()
                .append("function getClearance(){")
                .append(s.split("</script>")[0].replace("<script>", "").replaceAll("try\\{eval", "try{return"))
                .append("};");
        String resHtml = sb.toString().replace("</script>", "").replace("eval", "return").replace("<script>", "");

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        resHtml = new String(resHtml);
        engine.eval(resHtml);
        Invocable invocable = (Invocable) engine;
        //一级解密结果
        String resJs = (String) invocable.invokeFunction("getClearance");
        String overJs = "function getClearance2(){ var a" + resJs.split("document.cookie")[1].split("Path=/;'")[0] + "Path=/;';return a;};";
        overJs = overJs.replace("window.headless", "'undefined'");
        overJs = overJs.replace("return return", "return eval");
        overJs = replaceByPrefix(overJs,"createElement", "firstChild.href", "\"https://mall.phicomm.com/\"");
        engine.eval(overJs);
        Invocable invocable2 = (Invocable) engine;
        String over = (String) invocable2.invokeFunction("getClearance2");
        return over;
    }

    public Map<String, String> toLoginPage(Proxy proxy, Map<String, String> cookies, String phone, String password) {
        try {
            Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .timeout(100000)
                    .header("Host", "mall.phicomm.com")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Referer", "https://mall.phicomm.com/passport-login.html")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .proxy(proxy)
                    .cookies(cookies)
                    .ignoreHttpErrors(true)
                    .execute();
            if(execute.statusCode() != 200) {
                log.error("登录界面ck返回异常， code: [{}]", execute.statusCode());
                return toLoginPage(proxy, cookies, phone, password);
            } else {
                cookies.putAll(execute.cookies());
                return doLogin(cookies, proxy, 0, phone, password);
            }
        } catch (Exception e) {
            log.error("获取 登录界面 ck 失败，{}", e.getMessage());
        }
        return null;
    }

    public Map<String, String> doLogin(Map<String, String> cookies, Proxy proxy, int tryCount, String phone, String password) {
        try {
            Connection.Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html")
                    .method(Connection.Method.POST)
                    .timeout(SystemConstant.TIME_OUT)
                    .cookies(cookies)
                    .proxy(proxy)
                    .ignoreContentType(true)
                    .header("Host", "mall.phicomm.com")
                    .header("Connection", "keep-alive")
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .header("Origin", "https://mall.phicomm.com")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Referer", "https://mall.phicomm.com/passport-login.html")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .data("forward", "")
                    .data("uname", phone)
                    .data("password", password)
                    .execute();
            System.out.println((JSON.parseObject(loginResponse.body()).toString()));
            if(JSON.parseObject(loginResponse.body()).toString().contains("账号或密码错误")) {
                return null;
            }
            if (loginResponse.body().contains("error")) {
                throw new RuntimeException("账号或密码错误");
            }
            cookies.putAll(loginResponse.cookies());
        } catch (Exception e) {
            System.out.println("登录失败--" + phone + "---" + password + "---" + e.getMessage());
        }
        return cookies;
    }

    private String getOrders(Map<String, String> cookies, Proxy proxy, int i) {
        try {
            Thread.sleep(3000);
            Connection.Response response = Jsoup.connect("https://mall.phicomm.com/index.php/my-orders.html")
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .timeout(50000)
                    .userAgent(UserAgentUtil.get())
                    .proxy(proxy).validateTLSCertificates(false)
                    .cookies(cookies)
                    .execute();
            return response.body();
        } catch (Exception e) {
            i++;
            if(i > 2) {
                System.out.println("获取订单失败-######-" + cookies.get("_SID") + "---" + e.getMessage());
                return "暂无00";
            }
            return getOrders(cookies, proxy, i);
        }
    }

}
