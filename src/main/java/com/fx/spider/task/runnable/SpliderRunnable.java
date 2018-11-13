package com.fx.spider.task.runnable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fx.spider.constant.SystemConstant;
import com.fx.spider.model.OrderAccount;
import com.fx.spider.util.CookieUtils;
import com.fx.spider.util.ProxyUtil;
import com.fx.spider.util.UserAgentUtil;
import com.fx.spider.util.feifei.FeiFeiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.net.Proxy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@SuppressWarnings("all")
public class SpliderRunnable implements Runnable {

    private Integer updateCodeSecond = SystemConstant.UPDATE_CODE_SECOND;

    private String goods;

    private String goodsUrl;

    private String vc;

    private String vcCodeJson;

    private String vcCodeUrl;

    private String addrId;

    private volatile String rsbody;

    private Map<String, String> cookies = new HashMap<>();

    private Date initCodeDate = new Date();

    private OrderAccount account;

    private ProxyUtil proxyUtil;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private AtomicBoolean initCodeFlag = new AtomicBoolean(true);

    private boolean isLogin = false;

    public SpliderRunnable(OrderAccount account, String goods, String goodsUrl, String vc, String key) {
        this.account = account;
        this.goods = goods;
        this.goodsUrl = goodsUrl;
        this.vc = vc;
        this.proxyUtil = new ProxyUtil(key);
        proxyUtil.initIps();
    }

    public boolean checkStatus(Connection.Response response) {
        if(response.statusCode() == 403) {
            info("ip被封禁，换ip中");
            proxyUtil.initIps();
            info("换ip成功，开始重新登录");
            start();
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        OrderAccount orderAccount = new OrderAccount("13648045607", "li5201314");
        SpliderRunnable spliderRunnable = new SpliderRunnable(orderAccount, "W3", "https://mall.phicomm.com/cart-fastbuy-197-1.html", "0", "f0c35c13b2fffac65e411939bc2de921");
//        SpliderRunnable spliderRunnable = new SpliderRunnable(orderAccount, "W1", "https://mall.phicomm.com/cart-fastbuy-14-1.html", "29900", "f0c35c13b2fffac65e411939bc2de921");
        spliderRunnable.run();
//        String s = "var _1c = function() {\n" +
//                "\t\t\n" +
//                "\t\t\tvar cookie = '__jsl_clearance=1541911057.905|0|' + (function() {\n" +
//                "\t\t\t\tvar _59 = [function(_1c) {\n" +
//                "\t\t\t\t\treturn eval('String.fromCharCode(' + _1c + ')')\n" +
//                "\t\t\t\t},\n" +
//                "\t\t\t\t(function() {\n" +
//                "\t\t\t\t\tvar _1c = document.createElement('div');\n" +
//                "\t\t\t\t\t_1c.innerHTML = '<a href=\\'/\\'>_16</a>';\n" +
//                "\t\t\t\t\t_1c = _1c.firstChild.href;\n" +
//                "\t\t\t\t\tvar _59 = _1c.match(/https?:\\/\\//)[0];\n" +
//                "\t\t\t\t\t_1c = _1c.substr(_59.length).toLowerCase();\n" +
//                "\t\t\t\t\treturn function(_59) {\n" +
//                "\t\t\t\t\t\tfor (var _16 = 0; _16 < _59.length; _16++) {\n" +
//                "\t\t\t\t\t\t\t_59[_16] = _1c.charAt(_59[_16])\n" +
//                "\t\t\t\t\t\t};\n" +
//                "\t\t\t\t\t\treturn _59.join('')\n" +
//                "\t\t\t\t\t}\n" +
//                "\t\t\t\t})()],\n" +
//                "\t\t\t\t_16 = [[(7 + [] + [[]][0])], [[6] + (5 + []), (7 + [] + [[]][0]) + [6], ( - ~~~ {} + [[]][0]) + [2] + (( + []) + [] + [[]][0]), ((2 ^ -~~~ {}) + [] + []) + (7 + [] + [[]][0]), (5 + []) + (( + []) + [] + [[]][0]), [6] + [6], (7 + [] + [[]][0]) + ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + [])], [(5 + [])], [( - ~~~ {} + [[]][0]) + ( - ~~~ {} + [[]][0]) + ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []), ( - ~~~ {} + [[]][0]) + ( - ~~~ {} + [[]][0]) + (( + []) + [] + [[]][0])], [[3 + ( - ~ - ~ [] ^ -~~~ {}) + ( - ~ - ~ [] ^ -~~~ {})]], [( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []) + ( - ~~~ {} + [[]][0]), [3 + ( - ~ - ~ [] ^ -~~~ {}) + ( - ~ - ~ [] ^ -~~~ {})] + ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + [])], [(7 + [] + [[]][0])], [( - ~~~ {} + [[]][0]) + (( + []) + [] + [[]][0]) + ((2 ^ -~~~ {}) + [] + []), [3 + ( - ~ - ~ [] ^ -~~~ {}) + ( - ~ - ~ [] ^ -~~~ {})] + (( + []) + [] + [[]][0]), (5 + []) + ( - ~~~ {} + [[]][0]), ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []) + (5 + []), ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []) + (4 + [] + [[]][0]), ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []) + (4 + [] + [[]][0])], [[3 + ( - ~ - ~ [] ^ -~~~ {}) + ( - ~ - ~ [] ^ -~~~ {})]], [[6] + ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []), ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []) + [2], [3 + ( - ~ - ~ [] ^ -~~~ {}) + ( - ~ - ~ [] ^ -~~~ {})] + (( + []) + [] + [[]][0]), ( - ~~~ {} + [[]][0]) + ( - ~~~ {} + [[]][0]) + [6], [6] + ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []), (7 + [] + [[]][0]) + ((2 ^ -~~~ {}) + [] + []), (7 + [] + [[]][0]) + ((2 ^ -~~~ {}) + [] + []), ((2 ^ -~~~ {}) + [] + []) + (7 + [] + [[]][0]), (5 + []) + ( - ~~~ {} + [[]][0]), [6] + ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + [])]];\n" +
//                "\t\t\t\tfor (var _1c = 0; _1c < _16.length; _1c++) {\n" +
//                "\t\t\t\t\t_16[_1c] = _59.reverse()[(( + []) + [] + [[]][0])](_16[_1c])\n" +
//                "\t\t\t\t};\n" +
//                "\t\t\t\treturn _16.join('')\n" +
//                "\t\t\t})() + ';Expires=Sun, 11-Nov-18 05:37:37 GMT;Path=/;'\n" +
//                "\t\t\treturn cookie;\n" +
//                "\t\t};";
//        String s2 = new String(s);
//        String[] var_s = s.split(" = document.createElement")[0].split("var ");
//        System.err.println(var_s[var_s.length - 1]);
//        System.err.println("var " + var_s[var_s.length - 1] + " = \"https://mall.phicomm.com/\";");
//        String s1 = replaceAll(s, "(function() {", "firstChild.href;", "########", true, true);
//        System.out.println(s1.split("########\n")[1].split(" = ")[0]);
//        System.out.println(replaceAll(s,"document.createElement('div')", "firstChild.href", "\"https://mall.phicomm.com/\"", true, true));
    }

    @Override
    public void run() {
        try {
            Thread.sleep(4 * 1000);
        } catch (Exception e) {
        }
        start();
    }

    private void start() {
        initCookies();
        if(isLogin && isNotOrdered() && vcIsEnough() && initData()) {
            try {
                account.setProxy(proxyUtil.getProxy());
                CookieUtils.addCookies(account, cookies);
                initBody(cookies);
                countDownLatch.await();


                Document document = Jsoup.parse(rsbody);
                String cart_md5 = getCartMd5(document);

                AtomicBoolean atomicBoolean = new AtomicBoolean(true);
                while (atomicBoolean.get()) {
                    try {
                        if (StringUtils.isEmpty(vcCodeJson) || (new Date().getTime() >= DateUtils.addSeconds(initCodeDate, 44).getTime())) {
                            vcCodeJson = FeiFeiUtil.validate(Jsoup.connect(vcCodeUrl)
                                    .ignoreContentType(true)
                                    .cookies(cookies)
                                    .userAgent(UserAgentUtil.get())
                                    .proxy(proxyUtil.getProxy())
                                    .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes());
                        }
                        String vcode = new String(vcCodeJson);
                        vcCodeJson = null;
                        // ===========           下单         ==============
                        CountDownLatch cd = new CountDownLatch(SystemConstant.TASK_COUNT);
                        for (int i = 0; i < SystemConstant.TASK_COUNT; i++) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Connection.Response createOrderResponse = Jsoup.connect("https://mall.phicomm.com/order-create-is_fastbuy.html").method(Connection.Method.POST)
                                                .proxy(proxyUtil.getProxy())
                                                .timeout(SystemConstant.TIME_OUT).ignoreContentType(true)
                                                .cookies(cookies)
                                                .userAgent(UserAgentUtil.get())
                                                .header("X-Requested-With", "XMLHttpRequest")
                                                .data("cart_md5", cart_md5)
                                                .data("addr_id", addrId)
                                                .data("dlytype_id", "1")
                                                .data("payapp_id", "alipay")
                                                .data("yougouma", "")
                                                .data("invoice_type", "")
                                                .data("invoice_title", "")
                                                .data("useVcNum", vc)
                                                .data("need_invoice2", "on")
                                                .data("useDdwNum", "0")
                                                .data("memo", "")
                                                .data("vcode", vcode)
                                                .execute();
                                        info(JSONObject.parseObject(createOrderResponse.body()).toJSONString());
                                        if (createOrderResponse.body().contains("success")) {
                                            info("抢购成功，请付款!!!!" + cookies.get("_SID"));
                                            atomicBoolean.set(false);
                                        }
                                    } catch (Exception e) {
                                        info("抢购失败--" + e.getMessage());
                                    } finally {
                                        info("-addrId: " + addrId + ", vcCodeUrl: " + vcCodeUrl + ", cookies: " + cookies + ", cart_md5:" +  cart_md5);
                                        cd.countDown();
                                    }
                                }
                            }).start();
                        }
                        cd.await();
                    } catch (Exception e) {

                        log.error("下单失败： " + e.getMessage());
                        if(e.getMessage().contains("HTTP error fetching URL")) {
                            Thread.sleep(15 * 1000);
                        }
                    }
                }

            } catch (Exception e){
                info("线程启动失败: " + e.getMessage());
            }
        }
    }

    private String getCartMd5(Document document) {
        for (Element element : document.getElementsByTag("input")) {
            if (element.attr("name").equals("cart_md5")) {
                return element.attr("value");
            }
        }
        throw new RuntimeException("cart_md5获取失败");
    }

    private synchronized void updateRsBody(String body) {
        if (rsbody == null) {
            rsbody = body;
            countDownLatch.countDown();
            initCodeFlag.set(false);
        }
    }

    private String initBody(Map<String, String> cookies) {
        boolean flag = true;
        while (flag) {
            try {
//                Thread.sleep(SystemConstant.THREAD_WAIT_TIME);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
                        try {
                            Connection.Response response = Jsoup.connect(goodsUrl).method(Connection.Method.GET)
                                    .proxy(proxyUtil.getProxy())
                                    .userAgent(UserAgentUtil.get())
                                    .timeout(SystemConstant.TIME_OUT).cookies(cookies).followRedirects(true).execute();
                            String body = response.body();
                            if (body.contains("库存不足,当前最多可售数量")) {
                                info("库存不足 - " + new Date().toLocaleString());
                                Thread.sleep(5500);
                            } else if (body.contains("返回商品详情") || body.contains("cart_md5")) {
                                flag = false;
                                updateRsBody(body);
                            }
                        } catch (Exception e) {
                            if(e.getMessage().contains("HTTP error fetching URL") || e.getMessage().contains("Read timed out")
                                || e.getMessage().contains("Connection reset")
                                || e.getMessage().contains("Connection refused")
                                || e.getMessage().contains("Connection refused: connect")
                                || e.getMessage().contains("Connection timed out: connect")) {

                                if(new Date().getMinutes() > 8 && new Date().getMinutes() <= 58) {
                                    info("ip被封禁或过期，换ip中----" + e.getMessage());
                                    proxyUtil.initIps();
                                    flag = false;
                                    start();
                                }
                            }
                            info("初始化>>>body失败--" + e.getMessage());
                        }
//                    }
//                }).start();
            } catch (Exception e) {
                info("初始化body失败--" + e.getMessage());
            }
        }
        return rsbody;
    }

    private void syncCode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (initCodeFlag.get()) {
                    try {
                        if (new Date().getMinutes() == 59 && new Date().getSeconds() >= updateCodeSecond
                                || (vcCodeJson != null && new Date().getTime() >= DateUtils.addSeconds(initCodeDate, 45).getTime())) {
                            info("开始提前验证码");
                            initCodeDate = new Date();
                            vcCodeJson = FeiFeiUtil.validate(Jsoup.connect(vcCodeUrl)
                                    .ignoreContentType(true)
                                    .cookies(cookies)
                                    .proxy(proxyUtil.getProxy())
                                    .userAgent(UserAgentUtil.get())
                                    .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes());

                            info("提前验证码成功：" + vcCodeJson);
                            /** 防止使用途中被二次更新 */
                            Thread.sleep(44 * 1000);
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        info("验证码失败---" + e.getMessage());
                        vcCodeJson = null;
                    }
                }

            }
        }).start();
    }


    /**
     * 初始化数据 + 检查是否有默认地址
     * @param cookies
     * @return
     */
    private boolean initData() {
        try {
            Thread.sleep(7 * 1000);
            vcCodeUrl = "https://mall.phicomm.com/vcode-index-passport" + cookies.get("MEMBER_IDENT") + ".html";
            Document document = Jsoup.connect("https://mall.phicomm.com/my-receiver.html").method(Connection.Method.GET).cookies(cookies)
                    .timeout(SystemConstant.TIME_OUT).userAgent(UserAgentUtil.get())
                    .proxy(proxyUtil.getProxy())
                    .execute().parse();
            Elements dds = document.select("dd.clearfix.editing");

            if (dds == null || dds.size() == 0) {
                info("无收货地址，请设置！！");
                return false;
            }
            String[] split = dds.get(0).getElementsByTag("a").get(0).attr("href").split("-");
            addrId = split[split.length - 1].split("\\.")[0];

            Elements dts = document.getElementsByTag("dt");
            String address = "";
            for (Element dt : dts) {
                if (dt.text().contains("默认")) {
                    address = dt.getElementsByTag("span").get(0).text() + document.getElementsByTag("dd").get(0).text();
                    continue;
                }
            }

            log.info(account.getPhone() + "----" + account.getPassword() + "----addrId: {}, address: {}, vcCodeUrl: {}", addrId, address, vcCodeUrl);
            syncCode();
            return true;
        } catch (Exception e) {
            info("--初始化地址失败" + e.getMessage());
            if(e.getMessage().contains("HTTP error fetching URL") || e.getMessage().contains("Read timed out") || e.getMessage().contains("Connection refused: connect")) {
                info("ip被封禁或过期，换ip中----" + e.getMessage());
                proxyUtil.initIps();
                start();
            }
            return false;
        }
    }

    /**
     * 查询vc是否够用
     * @param cookies
     * @return
     */
    private boolean vcIsEnough() {
        String vc = getVc(cookies);
        if (Integer.parseInt(vc) < Integer.parseInt(vc)) {
           log.info("phone: {}, password: {}, 抢购[{}], 需要vc{}, 可用vc{}", account.getPhone(), account.getPassword(), goods, vc, vc);
            return false;
        }
        return true;
    }

    private String getVc(Map<String, String> cookies) {
        try {
            Thread.sleep(7 * 1000);
            Document parse = Jsoup.connect("https://mall.phicomm.com/my-vclist.html")
                    .cookies(cookies)
                    .userAgent(UserAgentUtil.get())
                    .timeout(SystemConstant.TIME_OUT)
                    .proxy(proxyUtil.getProxy())
                    .execute().parse();
            String vc = parse.body().text().split("可用维C ")[1].split(" 冻结维C")[0];
            return vc;
        } catch (Exception e) {
            return getVc(cookies);
        }
    }

    /**
     * 查询本周是否抢购过
     *
     * @param cookies
     * @return
     */
    private boolean isNotOrdered() {
        try {
            Thread.sleep(7 * 1000);
            Connection.Response response = Jsoup.connect("https://mall.phicomm.com/index.php/my-orders.html")
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .timeout(10000)
                    .userAgent(UserAgentUtil.get())
                    .proxy(proxyUtil.getProxy())
                    .cookies(cookies)
                    .execute();
            if (response.body().contains("暂无")) {
                info("----无订单");
                return true;
            } else {
                Document document = Jsoup.parse(response.body());
                Element table1 = document.getElementsByTag("table").get(1);
                Date date = DateUtils.parseDateStrictly(table1.getElementsByTag("li").get(1).text().trim(), "yyyy-MM-dd HH:mm");
                if (table1.text().contains(goods) && (!table1.text().contains("已取消")) && date.getTime() > DateUtils.addDays(new Date(), -7).getTime()) {
                    info("----已抢购---" + goods);
                    return false;
                }
                return true;

            }
        } catch (Exception e) {
            info("查单失败----" + e.getMessage());
            return isNotOrdered();
        }

    }



    public void initCookies() {
        try {
            Connection.Response response = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .header("Host", "mall.phicomm.com")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Referer", "https://mall.phicomm.com/passport-login.html")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .timeout(SystemConstant.TIME_OUT)
                    .proxy(proxyUtil.getProxy())
                    .ignoreHttpErrors(true)
                    .execute();

            Map<String, String> cookies = response.cookies();
            String body = response.body();
            checkStatus(response);
            if(response.statusCode() == 403) {
                info("ip被封禁，换ip中");
                proxyUtil.initIps();
            } else {
                cookies.put("__jsl_clearance", getck(body).split("=")[1]);
                this.cookies = cookies;
            }
            toLoginPage();
        } catch (Exception e) {
            info("获取 __jsl_clearance 失败，" + e.getMessage());
            if(e.getMessage().contains("HTTP error fetching URL") || e.getMessage().contains("Read timed out")
                    || e.getMessage().contains("Connection refused") || e.getMessage().contains("Connection reset")
                    || e.getMessage().contains("Connection timed out: connect")) {
                info("ip被封禁或过期，换ip中----" + e.getMessage());
                proxyUtil.initIps();
            }
            initCookies();
        }
    }

    private static String replaceAll(String htmlString,
                                     String start,
                                     String end,
                                     String newString,
                                     boolean logError,
                                     boolean reportError)
    {
        StringBuffer modString = new StringBuffer(htmlString.length());
        int i = 0, j = 0, j2=0;
        int tagFound = 0;
        while(true) {
            // first check if there are any matching start & end
            i = htmlString.indexOf(start, j2);
            if( i != -1 ) {
                j = htmlString.indexOf(end, i);
            } else {
                j = htmlString.indexOf(end, j2);
            }
            if ((i != -1) && (j != -1)) {
                tagFound++;
                modString.append( htmlString.substring(j2, i)).append( newString );
                j2 = j + end.length();// 此处不可以改为
// j2 = modString.length();因为进行查找相同的操作时是在htmlString上// 操作的，htmlString是没有变过的。进行拼接操作的是modString.
            }
            else {
                modString.append( htmlString.substring(j2));
                if((i != -1) && (j == -1) || (i == -1) && (j != -1)) {
                    //hack, to report same error message as if no tags found at all.
                    //if later determined to report a different error message if we do
                    //find tags but the last tag is not matched, we just need
                    //to put the logic here.
                    tagFound = 0;
                }
                break;
            }
        }
        if( tagFound == 0 ) {
            if (logError) {
                // write the stack trace to the log file
                String msg = "No matching tag for " + start + " or " + end;
                log.error(msg);
            }

            if (reportError) {
                return "no matching tag found in replaceAll=" + start;
            }
            else {
                return htmlString;
            }
        }
        return modString.toString();
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

    public void toLoginPage() {
        try {
            Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .timeout(100000)
                    .header("Host", "mall.phicomm.com")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Referer", "https://mall.phicomm.com/passport-login.html")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .proxy(proxyUtil.getProxy())
                    .cookies(this.cookies)
                    .ignoreHttpErrors(true)
                    .execute();
            if(execute.statusCode() != 200) {
                log.error("登录界面ck返回异常， code: [{}]", execute.statusCode());
                if(execute.statusCode() == 521) {
                    info("重新登录中");
                    start();
                } else {
                    toLoginPage();
                }
            } else {
                cookies.putAll(execute.cookies());
                doLogin(0);
            }
        } catch (Exception e) {
            log.error("获取 登录界面 ck 失败，{}", e.getMessage());
        }
    }

    public void doLogin(int tryCount) {
        try {
            Connection.Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html")
                    .method(Connection.Method.POST)
                    .timeout(SystemConstant.TIME_OUT)
                    .cookies(this.cookies)
                    .proxy(proxyUtil.getProxy())
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
                    .data("uname", account.getPhone())
                    .data("password", account.getPassword())
                    .execute();
            info(JSON.parseObject(loginResponse.body()).toString());
            if (loginResponse.body().contains("error")) {
                throw new RuntimeException("账号或密码错误");
            }
            isLogin = true;
            cookies.putAll(loginResponse.cookies());
        } catch (Exception e) {
            if(e.getMessage().equals("HTTP error fetching URL") || e.getMessage().equals("Read timed out") || e.getMessage().equals("Connection refused: connect")) {
                info("ip被封禁或过期，换ip中----" + e.getMessage());
                proxyUtil.initIps();
                start();
            } else {}
            info("登录失败，" + e.getMessage());
            if (tryCount < 5) {
                info("第" + (++tryCount) + "次登录重试");
                doLogin(tryCount);
            } else {
                info("超过最大登录次数");
            }
        }
    }

    private void info(String msg) {
        log.info(account.getPhone() + "----" + account.getPassword() + "----" + msg);
    }

}
