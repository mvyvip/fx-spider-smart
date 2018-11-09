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

        for (int i = 0; i < 30; i++) {
//        Thread.sleep(1000);
//           new Thread(new Runnable() {
//               @Override
//               public void run() {
                   try {
                       String host = getHosts();
//                       String url = "http://" + getHosts() + "/cart-fastbuy-13-1.html";
                       String url = "http://" + host + "/cart-fastbuy-13-1.html";
                       OkHttpClient okHttpClient = new OkHttpClient();
                       final Request request = new Builder()
                           .url(url)
                           .header("Cookie", "__jsluid=54d5a8a8c765c62f3287be364a258866; _VMC_UID=b74619d868761593c39e79325ad9205e; _SID=e46b208b9496d73ef77d344cd06c6764; Hm_lvt_d7682ab43891c68a00de46e9ce5b76aa=1541559313; c_dizhi=null; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1541640839,1541646842,1541646846,1541733148; __jsl_clearance=1541743894.16|0|BtOxaJsU6MyOtLG6xN432MnmkGI%3D; UNAME=13648045607; MEMBER_IDENT=6272948; MEMBER_LEVEL_ID=1; CACHE_VARY=f374b22ce753507a6e9cce128dc6be4b-0f063e018c840f8a56946ecec41a6c18; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541743876")
                           .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                           .header("Host", "mall.phicomm.com")
                           .build();
                       final Call call = okHttpClient.newCall(request);
                       System.out.println(url);
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
