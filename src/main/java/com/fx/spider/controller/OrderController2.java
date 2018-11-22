package com.fx.spider.controller;

import com.alibaba.fastjson.JSON;
import com.fx.spider.constant.SystemConstant;
import com.fx.spider.model.OrderAccount;
import com.fx.spider.model.Page;
import com.fx.spider.model.ProxyEntity;
import com.fx.spider.service.AccountService;
import com.fx.spider.util.ProxyUtil;
import com.fx.spider.util.UserAgentUtil;
import java.net.Proxy.Type;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
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
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@SuppressWarnings("all")
public class OrderController2 {

    @Autowired
    private AccountService accountService;

    @GetMapping("/orders2")
    public Object orders2(Page page) throws Exception {
        List<OrderAccount> orderAccountList = accountService.findPage(page);
        List<OrderAccount> temp = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("count", accountService.findAll().size());
        result.put("data", orderAccountList);
        CountDownLatch countDownLatch = new CountDownLatch(orderAccountList.size());
        AtomicInteger atomicInteger = new AtomicInteger(0);

        for (int i = 0; i < orderAccountList.size(); i++) {
            temp.add(orderAccountList.get(i));
            if(temp.size() % 100 == 0) {
                doTask(temp, countDownLatch, atomicInteger);
                temp.clear();
                System.out.println(i + "----" + temp.size());
            }
        }
        System.out.println(temp.size() + "---count: " + orderAccountList.size());
        doTask(temp, countDownLatch, atomicInteger);

        return result;
    }

    public Map<String, String> initCk(OrderAccount ac, String phone, String password, Map<String, String> cookies, Proxy proxy, int i) throws Exception {
        Map<String, String> cks = initCookies(ac, ac.getPhone(), ac.getPassword(), new HashMap<String, String>(), proxy);
        if(MapUtils.isEmpty(cookies) || (ac.getRemark() != null && !ac.getRemark().contains("--"))) {
            if(i > 3) {
                return null;
            }
            i++;
            log.info("{}----{}----第[{}]次重试登录", phone, password, i);
            return initCk(ac, phone, password, cookies, getNewProxy(), i);
        }
        return cks;
    }

    public void doTask(List<OrderAccount> temp, CountDownLatch countDownLatch, AtomicInteger atomicInteger) throws Exception {
        List<ProxyEntity> proxyEntities = new ProxyUtil("f0c35c13b2fffac65e411939bc2de921").get50WdProxy();
        for (int i = 0; i < temp.size(); i++) {
            OrderAccount orderAccount = temp.get(i);
            Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyEntities.get(i).getIp(), proxyEntities.get(i).getPort()));
            new Thread(new Runnable() {
                @Override
                public void run() {
                   try {
                       boolean flag = true;
                       Proxy proxy1 = proxy;
                       Map<String, String> cookies = initCookies(orderAccount, orderAccount.getPhone(), orderAccount.getPassword(), new HashMap<String, String>(), proxy1);
                       if(MapUtils.isEmpty(cookies) || (orderAccount.getRemark() != null && !orderAccount.getRemark().contains("--"))) {
                           proxy1 = getNewProxy();
                           cookies = initCk(orderAccount, orderAccount.getPhone(), orderAccount.getPassword(), new HashMap<String, String>(), proxy1, 1);
                           if(MapUtils.isEmpty(cookies)) {
                                flag = false;
                           }
                       }
                       if(flag) {
                           String body = getOrders(cookies, proxy1, 1);
                           if(body.contains("暂无")) {
                               log.info(orderAccount.getPhone() + "----" + orderAccount.getPassword() + "----无订单");
                           } else {
                               Document document = Jsoup.parse(body);
                               Elements tables = document.getElementsByTag("table");

                               boolean flag2 = true;
                               for (int i = 1; i < tables.size(); i++) {
                                   OrderAccount order = new OrderAccount();
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
                                               .proxy(proxy1).validateTLSCertificates(false)
                                               .execute();
                                           if(execute.body().contains("订单确认收货成功")) {
                                               log.info(orderAccount.getPhone() + "--- " + "收货成功");
                                           } else {
                                               log.info(orderAccount.getPhone() + " ----" + orderAccount.getPassword() + "--- " + "收货失败");
                                           }
                                       }
                                   }


                                   if(date.getTime() > DateUtils.addDays(new Date(), -7).getTime()) {
                                       Elements aElements = table.getElementsByTag("a");
                                       Thread.sleep(5000);
//                                       setDetail(cookies, aElements, order, 0, wdProxy);
                                       order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                       flag = false;

                                       if(order.getGoodsName().contains("T1") && !order.getGoodsName().contains("优购码")) {
                                           accountService.updateStatus(order.getPhone());
                                       }

                                      /* if(StringUtils.isNotEmpty(name) && !order.getAddress().contains(name)) {
                                           continue;
                                       }

                                       if(StringUtils.isNotEmpty(type)) {
                                           if(type.equals("1")  && !order.getLogisticsInfo().contains("签收")) {
                                               continue;
                                           } else if(type.equals("2") && order.getLogisticsInfo().contains("签收")) {
                                               continue;
                                           }
                                       }
                                       orders.add(order);*/
                                   }
                               }
                               if(flag) {
//                                   needGet.add(orderAccount);
                               }
                           }


                       }
                   } catch (Exception e) {
                       log.error("查单失败--" + e.getMessage());
                   } finally {
                       countDownLatch.countDown();
                       System.out.println("当前处理到--> " + atomicInteger.incrementAndGet());
                   }
                }
            }).start();
        }
        Thread.sleep(5300);
    }

    private String getOrders(Map<String, String> cookies, Proxy proxy, int i) {
        try {
            Thread.sleep(3000);
            Connection.Response response = Jsoup.connect("https://mall.phicomm.com/index.php/my-orders.html")
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .timeout(10 * 1000)
                .userAgent(UserAgentUtil.get())
                .proxy(proxy).validateTLSCertificates(false)
                .cookies(cookies)
                .execute();
            return response.body();
        } catch (Exception e) {
            i++;
            if(i > 5) {
                System.out.println("获取订单失败-######-" + cookies.get("_SID") + "---" + e.getMessage());
                return "暂无00";
            }
            return getOrders(cookies, proxy, i);
        }
    }

    @GetMapping("/accounts2")
    public Object accounts2(Page page) throws Exception {
        List<OrderAccount> orderAccountList = accountService.findPage(page);
        Map<String, Object> result = new HashMap<>();
        List<OrderAccount> datas = new ArrayList<>();
        List<OrderAccount> temp = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(orderAccountList.size());

        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < orderAccountList.size(); i++) {
            OrderAccount orderAccount = orderAccountList.get(i);
            temp.add(orderAccount);
            if(temp.size() % 95 == 0) {
                List<ProxyEntity> proxyEntities = new ProxyUtil("f0c35c13b2fffac65e411939bc2de921").get50WdProxy();
                for (int j = 0; j < temp.size(); j++) {
                    OrderAccount ac = temp.get(j);
                    datas.add(ac);
                    int finalI = i;
                    int finalJ = j;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyEntities.get(finalJ).getIp(), proxyEntities.get(finalJ).getPort()));
                                System.err.println(proxy);
                                Map<String, String> cookies = initCookies(ac, ac.getPhone(), ac.getPassword(), new HashMap<String, String>(), proxy);
                                if(MapUtils.isNotEmpty(cookies)) {
                                    Thread.sleep(7000);
                                    if(initVc(cookies, proxy, ac, 0)) {
                                        if(initRealName(cookies, proxy, ac)) {
                                            initDefaultAddress(cookies, proxy, ac);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                log.error("登陆失败--" + e.getMessage());
                            } finally {
                                countDownLatch.countDown();
                                System.out.println("当前处理到--> " + atomicInteger.incrementAndGet());
                            }
                        }
                    }).start();
                }
                temp.clear();
                Thread.sleep(6000);
            }
        }

        List<ProxyEntity> proxyEntities = new ProxyUtil("f0c35c13b2fffac65e411939bc2de921").get50WdProxy();
        for (int j = 0; j < temp.size(); j++) {
            OrderAccount ac = temp.get(j);
            datas.add(ac);
            int finalJ = j;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyEntities.get(finalJ).getIp(), proxyEntities.get(finalJ).getPort()));
                        System.out.println("proxy>>> " + proxy);
                        Map<String, String> cookies = initCookies(ac, ac.getPhone(), ac.getPassword(), new HashMap<String, String>(), proxy);
                        if(MapUtils.isNotEmpty(cookies)) {
                            if(initVc(cookies, proxy, ac, 1)) {
                                if(initRealName(cookies, proxy, ac)) {
                                    initDefaultAddress(cookies, proxy, ac);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("登陆失败--" + e.getMessage());
                    } finally {
                        countDownLatch.countDown();
                        System.out.println("当前处理到--> " + atomicInteger.incrementAndGet());
                    }
                }
            }).start();
        }

        countDownLatch.await();

        System.out.println("开始");

        result.put("code", 0);
        result.put("count", accountService.findAll().size());
        result.put("data", datas);

        return result;
    }

    private boolean initDefaultAddress(Map<String, String> cookies, Proxy proxy, OrderAccount ac) {
        try {
            Thread.sleep(7000);
            Document document = Jsoup.connect("https://mall.phicomm.com/my-receiver.html").method(Connection.Method.GET).cookies(cookies).timeout(SystemConstant.TIME_OUT)
                    .proxy(proxy).validateTLSCertificates(false)
                    .userAgent(UserAgentUtil.get())
                    .timeout(SystemConstant.TIME_OUT)
                    .execute().parse();
            Elements dts = document.getElementsByTag("dt");
            for (Element dt : dts) {
                if(dt.text().contains("默认")) {
                    String defaultAddress = dt.getElementsByTag("span").get(0).text() + document.getElementsByTag("dd").get(0).text();
                    ac.setDefaultAddress(defaultAddress);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("获取默认地址失败：" + e.getMessage());
            return false;
        }
    }

    private boolean initRealName(Map<String, String> cookies, Proxy proxy, OrderAccount ac) {
        try {
            Thread.sleep(7000);
            Document parse = Jsoup.connect("https://mall.phicomm.com/my-setting.html")
                    .cookies(cookies)
                    .userAgent(UserAgentUtil.get())
                    .timeout(SystemConstant.TIME_OUT)
                    .proxy(proxy).validateTLSCertificates(false)
                    .execute().parse();
            if(parse.body().text().contains("已认证")) {
                ac.setRenzheng("已认证");
            } else {
                ac.setRenzheng("未认证");
            }
            return true;
        } catch (Exception e) {
            log.info("获取实名信息失败: " + e.getMessage());
            return false;
        }
    }

    private boolean initVc(Map<String, String> cookies, Proxy proxy, OrderAccount ac, int i) {
        try {
            Thread.sleep(7000);
            Document parse = Jsoup.connect("https://mall.phicomm.com/my-vclist.html")
                    .cookies(cookies)
                    .userAgent(UserAgentUtil.get())
                    .timeout(SystemConstant.TIME_OUT)
                    .proxy(proxy).validateTLSCertificates(false)
                    .execute().parse();
            ac.setVc(parse.body().text().split("可用维C ")[1].split(" 冻结维C")[0]);
            ac.setVc2(parse.body().text().split("冻结维C ")[1].split(" ")[0]);
            return true;
        } catch (Exception e) {
            i++;
            if(i < 3) {
                initVc(cookies, proxy, ac, i);
            }
            log.info("获取vc信息失败: " + e.getMessage());
            return false;
        }
    }


    public Proxy initProxy() {
        return new ProxyUtil("f0c35c13b2fffac65e411939bc2de921").getProxy();
    }

    public Map<String, String> initCookies(OrderAccount ac, String phone, String password, Map<String, String> cookies, Proxy proxy) {
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

            cookies = response.cookies();
            String body = response.body();
            if (response.statusCode() == 403) {
                proxy = getNewProxy();
                return initCookies(ac, phone, password, cookies, proxy);
            } else {
                cookies.put("__jsl_clearance", getck(body).split("=")[1]);
                cookies = cookies;
                return toLoginPage(ac, phone, password, cookies, proxy);
            }
        } catch (Exception e) {
            log.error("初始化登录失败: " + e.getMessage());
            if (e.getMessage().contains("HTTP error fetching URL") || e.getMessage().contains("Read timed out")
                    || e.getMessage().contains("Connection refused") || e.getMessage().contains("Connection reset")
                    || e.getMessage().contains("Connection timed out")
                    || e.getMessage().contains("403")
                    ) {
                proxy = initProxy();
            }
            return initCookies(ac, phone, password, cookies, proxy);
        }
    }

    public boolean checkStatus(Connection.Response response) {
        if (response.statusCode() == 403) {
            log.info("ip被封禁，换ip中");
//            proxy =
            log.info("换ip成功，开始重新登录");
//            start();
            return false;
        }
        return true;
    }

    public synchronized Proxy getNewProxy() {
        try {
            Thread.sleep(5200);
            return new ProxyUtil("f0c35c13b2fffac65e411939bc2de921").getWdProxy();
        } catch (Exception e) {
            log.error("获取代理失败" + e.getMessage());
            return null;
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

        overJs = replaceByPrefix(overJs, "createElement", "firstChild.href", "\"https://mall.phicomm.com/\"");

        engine.eval(overJs);
        Invocable invocable2 = (Invocable) engine;
        String over = (String) invocable2.invokeFunction("getClearance2");
        return over;
    }

    public static String replaceByPrefix(String str, String start, String end, String replace) {
        if (!str.contains(start)) {
            return str;
        }
        if (!str.contains(end)) {
            System.out.println("end : " + end + " is not exist");
            return str;
        }
        StringBuilder sb = new StringBuilder();
        String s1 = sb.append(str.split(start)[0].substring(0, str.split(start)[0].length() - 9))
                .append(replace)
                .append(str.split(start)[1].split(end)[1]).toString();
        return s1;
    }

    public Map<String, String> toLoginPage(OrderAccount ac, String phone, String password, Map<String, String> cookies, Proxy proxy) {
        try {
            Thread.sleep(7000);
            Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .timeout(45 * 1000)
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
            if (execute.statusCode() != 200) {
                log.error("登录界面ck返回异常， code: [{}]", execute.statusCode());
                if (execute.statusCode() == 403 || execute.statusCode() == 521) {
                    return initCookies(ac, phone, password, cookies, getNewProxy());
                } else {
                    return toLoginPage(ac, phone, password, cookies, proxy);
                }
            } else {
                cookies.putAll(execute.cookies());
                return doLogin(ac, phone, password, cookies, proxy, 0);
            }
        } catch (Exception e) {
            log.error("获取 登录界面 ck 失败，{}", e.getMessage());
        }
        return null;
    }

    public Map<String, String> doLogin(OrderAccount orderAccount, String phone, String password, Map<String, String> cookies, Proxy proxy, int tryCount) {
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
            log.info(JSON.parseObject(loginResponse.body()).toString());
            if (JSON.parseObject(loginResponse.body()).toString().contains("错误") || loginResponse.body().contains("error")) {
//                this.remark = this.remark + "--密码错误";
                if(JSON.parseObject(loginResponse.body()).toString().contains("错误")) {
                    orderAccount.setRemark((orderAccount.getRemark() + "--" + "密码错误"));
                } else {
                    orderAccount.setRemark((orderAccount.getRemark() + "--" + "登录失败"));
                }
                return null;
            }
            cookies.putAll(loginResponse.cookies());
            return cookies;
        } catch (Exception e) {
            if (e.getMessage().equals("HTTP error fetching URL") || e.getMessage().equals("Read timed out") || e.getMessage().equals("Connection refused: connect")) {
                info(phone, password, "ip被封禁或过期，换ip中----" + e.getMessage());
//                proxy = initProxy();
                return null;
                //                start();
            }
            info(phone, password, "登录失败，" + e.getMessage());
            if (tryCount < 50) {
                tryCount = tryCount + 1;
                info(phone, password, "第" + tryCount + "次登录重试");
                return doLogin(orderAccount, phone, password, cookies, proxy, tryCount);
            } else {
                info(phone, password, "超过最大登录次数");
                return null;
            }
        }

    }

    private void info(String mobile, String password, String msg) {
        log.info(mobile + "----" + password + "----" + msg);
    }

}
