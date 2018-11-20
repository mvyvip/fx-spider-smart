package com.fx.spider.controller;

import com.alibaba.fastjson.JSON;
import com.fx.spider.AccountUtil;
import com.fx.spider.constant.SystemConstant;
import com.fx.spider.model.OrderAccount;
import com.fx.spider.model.Page;
import com.fx.spider.model.ProxyEntity;
import com.fx.spider.service.AccountService;
import com.fx.spider.util.PandaProxyUtil;
import com.fx.spider.util.ProxyUtil;
import com.fx.spider.util.UserAgentUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
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
                    int finalI = i;
                    int finalJ = j;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                datas.add(ac);
                                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyEntities.get(finalJ).getIp(), proxyEntities.get(finalJ).getPort()));
                                Map<String, String> cookies = initCookies(ac.getPhone(), ac.getPassword(), new HashMap<String, String>(), proxy);
                                if(MapUtils.isNotEmpty(cookies)) {
                                    if(initVc(cookies, proxy, ac)) {
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

        List<Proxy> proxy = PandaProxyUtil.getProxy();
        List<ProxyEntity> proxyEntities = new ProxyUtil("f0c35c13b2fffac65e411939bc2de921").get50WdProxy();
        for (int j = 0; j < temp.size(); j++) {
            OrderAccount ac = temp.get(j);
            int finalJ = j;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        datas.add(ac);
                        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyEntities.get(finalJ).getIp(), proxyEntities.get(finalJ).getPort()));
                        Map<String, String> cookies = initCookies(ac.getPhone(), ac.getPassword(), new HashMap<String, String>(), proxy);
                        if(MapUtils.isNotEmpty(cookies)) {
                            if(initVc(cookies, proxy, ac)) {
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
            Thread.sleep(3000);
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

    private boolean initVc(Map<String, String> cookies, Proxy proxy, OrderAccount ac) {
        try {
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
            log.info("获取vc信息失败: " + e.getMessage());
            return false;
        }
    }


    public Proxy initProxy() {
        return new ProxyUtil("f0c35c13b2fffac65e411939bc2de921").getProxy();
    }

    public Map<String, String> initCookies(String phone, String password, Map<String, String> cookies, Proxy proxy) {
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
            checkStatus(response);
            if (response.statusCode() == 403) {
                proxy = new ProxyUtil("f0c35c13b2fffac65e411939bc2de921").getWdProxy();
            } else {
                cookies.put("__jsl_clearance", getck(body).split("=")[1]);
                cookies = cookies;
            }
            return toLoginPage(phone, password, cookies, proxy);
        } catch (Exception e) {
            log.error("初始化登录失败: " + e.getMessage());
            if (e.getMessage().contains("HTTP error fetching URL") || e.getMessage().contains("Read timed out")
                    || e.getMessage().contains("Connection refused") || e.getMessage().contains("Connection reset")
                    || e.getMessage().contains("Connection timed out")
                    || e.getMessage().contains("403")
                    ) {
                proxy = initProxy();
            }
            return initCookies(phone, password, cookies, proxy);
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

    public Map<String, String> toLoginPage(String phone, String password, Map<String, String> cookies, Proxy proxy) {
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
            if (execute.statusCode() != 200) {
                log.error("登录界面ck返回异常， code: [{}]", execute.statusCode());
                if (execute.statusCode() == 403 || execute.statusCode() == 521) {
                    return initCookies(phone, password, cookies, new ProxyUtil("f0c35c13b2fffac65e411939bc2de921").getWdProxy());
                } else {
                    return toLoginPage(phone, password, cookies, proxy);
                }
            } else {
                cookies.putAll(execute.cookies());
                return doLogin(phone, password, cookies, proxy, 0);
            }
        } catch (Exception e) {
            log.error("获取 登录界面 ck 失败，{}", e.getMessage());
        }
        return null;
    }

    public Map<String, String> doLogin(String phone, String password, Map<String, String> cookies, Proxy proxy, int tryCount) {
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
                return doLogin(phone, password, cookies, proxy, tryCount);
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
