package com.fx.spider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class ProxyTest {

    public static List<String> hosts = new ArrayList<>();

    public static AtomicInteger atomicInteger = new AtomicInteger(0);

    static {
        hosts.add("111.13.147.234");
        hosts.add("112.90.216.82");
        hosts.add("111.47.226.161");
        hosts.add("183.222.96.234");
        hosts.add("1.31.128.245");
        hosts.add("1.31.128.203");
        hosts.add("111.13.147.215");
        hosts.add("1.31.128.217");
        hosts.add("183.222.96.250");
        hosts.add("1.31.128.139");
        hosts.add("1.31.128.213");
        hosts.add("113.207.76.18");
        hosts.add("1.31.128.231");
        hosts.add("1.31.128.216");
        hosts.add("111.47.226.25");
        hosts.add("113.107.238.206");
        hosts.add("117.21.219.111");
        hosts.add("117.21.219.76");
        hosts.add("117.21.219.99");
        hosts.add("117.21.219.73");
        hosts.add("113.107.238.133");
        hosts.add("122.228.238.92");
        hosts.add("58.58.81.142");
        hosts.add("112.90.216.63");
        hosts.add("113.107.238.193");
        hosts.add("106.42.25.225");
        hosts.add("117.21.219.112");
        hosts.add("112.90.216.104");
        hosts.add("1.31.128.230");
        hosts.add("1.31.128.140");
        hosts.add("111.202.98.6");
        hosts.add("1.31.128.153");
        hosts.add("111.13.147.233");
    }

    public static String getHosts() {
        if(atomicInteger.get() == hosts.size()) {
            atomicInteger.set(0);
        }
        return hosts.get(atomicInteger.getAndIncrement());
    }

    public static void main(String[] args)  throws Exception {

    /*    for (int i = 0; i < 100; i++) {
            System.out.println(getHosts());
        }
        Thread.sleep(111111);*/

//        Connection.Response execute = Jsoup.connect("http://test.abuyun.com/proxy.php")
//            .timeout(3000)
//            .header("Host", "mall.phicomm.com")
//            .header("xx", "123")
//            .userAgent(UserAgentUtil.get())
////            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("36.6.144.29", 23564)))
//            .execute();
//
//        System.out.println(execute.body());

     /*   Connection.Response response = Jsoup.connect(goodsUrl).method(Connection.Method.GET)
            .proxy(proxyUtil.getProxy())
            .userAgent(UserAgentUtil.get())
            .timeout(SystemConstant.TIME_OUT).cookies(cookies).followRedirects(true).execute();
        String body = response.body();
        if (body.contains("库存不足,当前最多可售数量")) {
            info("库存不足 - " + new Date().toLocaleString());
            Thread.sleep(6000);
        } else if (body.contains("返回商品详情") || body.contains("cart_md5")) {
            flag = false;
            updateRsBody(body);
        }
*/

        for (int i = 0; i < 130; i++) {
//        Thread.sleep(1000);
//           new Thread(new Runnable() {
//               @Override
//               public void run() {
                   try {
                       String host = getHosts();
//                       String url = "http://" + getHosts() + "/cart-fastbuy-13-1.html";
                       String url = "http://" + getHosts() + "/m/checkout-fastbuy.html";
                       System.out.println(url);
                       OkHttpClient okHttpClient = new OkHttpClient();
                       final Request request = new Builder()
                           .url(url)
                           .header("Cookie", "Hm_lvt_806df8bf4f865af1db1c724887359a8c=1542220570,1542220586,1542222848,1542223013; _SID=A88ADD098413F964442923EDFB930D3D; Hm_lvt_d7682ab43891c68a00de46e9ce5b76aa=1542218943,1542220360,1542220361,1542220629; __jsl_clearance=1542222290.156|0|LLn9SvaAIKrr83sy7WOhmjWeDo0%3D; CACHE_VARY=d8a3ac5eaa02cc9b268b60d696ef578f-9244d31d67c9dff15c6a27bbbb77f6ac; MEMBER_IDENT=6272948; MEMBER_LEVEL_ID=1; UNAME=13648045607; _VMC_UID=ecee97ca878c2b4140b242f453d1e8e4; __jsluid=88002394aaf25ec77a536d724290d8e9")
                           .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E302 VMCHybirdAPP-iOS/2.2.4/")
                           .header("Host", "mall.phicomm.com")
                           .build();
                       final Call call = okHttpClient.newCall(request);
                       Response response = call.execute();
                       if(response.body().string().contains("库存不足,当前最多可售数量")) {
                           System.out.println(("库存不足 - " + new Date().toLocaleString()));
                           Thread.sleep(1000);
                       } else {
                           System.err.println(response.code() + "封ip----" + new Date().toLocaleString());
                       }
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
//               }
//           }).start();
        }



    }

}
