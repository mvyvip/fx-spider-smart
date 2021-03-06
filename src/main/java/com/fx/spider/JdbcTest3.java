package com.fx.spider;

import com.alibaba.fastjson.JSON;
import com.fx.spider.constant.SystemConstant;
import com.fx.spider.util.UserAgentUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
@Slf4j
public class JdbcTest3 { //定义一个类

    private Map<String, String> cookies = new HashMap<>();

    private String phone;

    private String password;

    public JdbcTest3() {}

    public JdbcTest3(String phone, String password) {
        this.password = password;
        this.phone = phone;
    }

    private static List<Account> accounts = new ArrayList<>();

    public static void main(String[] args) throws Exception { //主方法
        try {
            String driver = "com.mysql.jdbc.Driver";              //1.定义驱动程序名为driver内容为com.mysql.jdbc.Driver
            String url = "jdbc:mysql://118.24.153.209:3306/fx"; //防止乱码；useUnicode=true表示使用Unicode字符集；characterEncoding=UTF8表示使用UTF-8来编辑的。
            String user = "root";                                   //3.定义用户名，写你想要连接到的用户。
            String pass = "admin";                                  //4.用户密码。
            String querySql = "select phone, password from fx_order_account where id > 3859 and password=666666 ORDER BY id";          //5.你想要查找的表名。
            Class.forName(driver);                              //6.注册驱动程序，用java.lang包下面的class类里面的Class.froName();方法 此处的driver就是1里面定义的driver，也可以  Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);//7.获取数据库连接,使用java.sql里面的DriverManager的getConnectin(String url , String username ,String password )来完成
            Statement stmt = conn.createStatement();   //8.构造一个statement对象来执行sql语句：主要有Statement，PreparedStatement，CallableStatement三种实例来实现
            ResultSet rs = stmt.executeQuery(querySql);//9.执行sql并返还结束 ；ResultSet executeQuery(String sqlString)：用于返还一个结果集（ResultSet）对象。
            while (rs.next()) {
                 accounts.add(new Account(rs.getString("phone"), rs.getString("password")));
            }
            System.out.println(accounts.size());


            for (Account account : accounts) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JdbcTest3 jdbcTest = new JdbcTest3(account.getPhone(), account.getPassword());
                            jdbcTest.initCookies();
                            Map<String, String> cookies = jdbcTest.cookies;
                                    Response execute = Jsoup.connect("https://mall.phicomm.com/my-receiver-save.html").method(org.jsoup.Connection.Method.POST).cookies(cookies)
                               .timeout(SystemConstant.TIME_OUT)
                               .ignoreContentType(true)
                                .userAgent(UserAgentUtil.get())
                               //.header("X-Requested-With", "XMLHttpRequest")
                               //.header("Content-Type", "application/x-www-form-urlencoded")
                               //.header("Upgrade-Insecure-Requests", "1")
//                               .data("maddr[name]", "马洪文")
//                               .data("maddr[mobile]", "18254459299")
//                               .data("maddr[area]", "mainland:山东省/枣庄市/山亭区:1542")
//                               .data("maddr[addr]", "山东枣庄山亭区紫禁庄园 马洪文 18254459299-诗人")
//                               .data("maddr[is_default]", "true")
//                                            .data("maddr[name]", "刘正周")
//                                            .data("maddr[mobile]", "13863749494")
//                                            .data("maddr[area]", "mainland:山东省/济宁市/微山县:1583")
//                                            .data("maddr[addr]", "山东省济宁市微山县苏园一村32号楼1单元888号--诗人")
//                                            .data("maddr[is_default]", "true")

                                            .data("maddr[name]", "妍妍")
                                            .data("maddr[mobile]", "13473376134")
                                            .data("maddr[area]", "mainland:河北省/石家庄市/新华区:49")
                                            .data("maddr[addr]", "河北省石家庄市新华区北二环西路89号星河御城9号楼 妍妍-13473376134收")
                                            .data("maddr[is_default]", "true")
                               .execute();
                           System.out.println(account.getPhone() + "----" + account.getPassword() + "---ok");


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                Thread.sleep(17 * 1000);
            }



            /*if (rs != null) {//11.关闭记录集
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {//12.关闭声明的对象
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {//13.关闭连接 （记住一定要先关闭前面的11.12.然后在关闭连接，就像关门一样，先关里面的，最后关最外面的）
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> getCookies(String username, String password) throws Exception {
        try {
            Response pageResponse = Jsoup.connect("https://mall.phicomm.com/passport-login.html").method(org.jsoup.Connection.Method.GET).timeout(SystemConstant.TIME_OUT).execute();
            Map<String, String> pageCookies = pageResponse.cookies();

            Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html").method(org.jsoup.Connection.Method.POST)
                    .cookies(pageCookies)
                    .timeout(SystemConstant.TIME_OUT)
                    .ignoreContentType(true)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("forward", "")
                    .data("uname", username)
                    .data("password", password)
                    .execute();


            if(loginResponse.body().contains("error")) {
                throw new RuntimeException("帐号或密码不正确");
            }

            Map<String, String> cks = new HashMap<>();
            cks.putAll(pageCookies);
            cks.putAll(loginResponse.cookies());
            return cks;
        }catch (Exception e){
            return getCookies(username, password);
        }
    }

    public void initCookies() {
        try {
            Response response = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .header("Host", "mall.phicomm.com")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Referer", "https://mall.phicomm.com/passport-login.html")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .timeout(SystemConstant.TIME_OUT)
                    .ignoreHttpErrors(true)
                    .execute();

            Map<String, String> cookies = response.cookies();
            String body = response.body();
            if(response.statusCode() == 403) {
                System.out.println(("ip被封禁，换ip中"));
            } else {
                cookies.put("__jsl_clearance", getck(body).split("=")[1]);
                this.cookies = cookies;
            }
            toLoginPage();
        } catch (Exception e) {
            System.out.println("获取 __jsl_clearance 失败，" + e.getMessage());
            if(e.getMessage().contains("HTTP error fetching URL") || e.getMessage().contains("Read timed out")
                    || e.getMessage().contains("Connection refused: connect")
                    || e.getMessage().contains("Connection timed out: connect")) {
                System.out.println("ip被封禁或过期，换ip中----" + e.getMessage());
            }
            initCookies();
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

    public void toLoginPage() {
        try {
//            Thread.sleep(5000);
            Response execute = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .timeout(100000)
                    .header("Host", "mall.phicomm.com")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Referer", "https://mall.phicomm.com/passport-login.html")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .cookies(this.cookies)
                    .ignoreHttpErrors(true)
                    .execute();
            if(execute.statusCode() != 200) {
                log.error("登录界面ck返回异常， code: [{}]", execute.statusCode());
                toLoginPage();
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
//            Thread.sleep(5000);
            Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html")
                    .method(org.jsoup.Connection.Method.POST)
                    .timeout(SystemConstant.TIME_OUT)
                    .cookies(this.cookies)
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
                    .data("uname", this.phone)
                    .data("password", this.password)
                    .execute();
            log.info(JSON.parseObject(loginResponse.body()).toString());

            if (JSON.parseObject(loginResponse.body()).toString().contains("错误")) {
                System.err.println(phone + " -- " + password + "----" + JSON.parseObject(loginResponse.body()).toString());
            }
            if (loginResponse.body().contains("error")) {
                throw new RuntimeException("账号或密码错误");
            }
            cookies.putAll(loginResponse.cookies());
        } catch (Exception e) {
            if(e.getMessage().equals("HTTP error fetching URL") || e.getMessage().equals("Read timed out") || e.getMessage().equals("Connection refused: connect")) {
                log.info("ip被封禁或过期，换ip中----" + e.getMessage());
            } else {}
            log.info("登录失败，" + e.getMessage());
            if (tryCount < 5) {
                doLogin(tryCount);
            } else {
                log.info("超过最大登录次数");
            }
        }
    }

}