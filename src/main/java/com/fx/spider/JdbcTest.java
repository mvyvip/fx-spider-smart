package com.fx.spider;

import com.alibaba.fastjson.JSON;
import com.fx.spider.constant.SystemConstant;

import java.sql.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fx.spider.util.UserAgentUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@SuppressWarnings("all")
@Slf4j
public class JdbcTest { //定义一个类

    private Map<String, String> cookies = new HashMap<>();

    private String phone;

    private String password;

    public JdbcTest() {}

    public JdbcTest(String phone, String password) {
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
            String querySql = "select phone, password from fx_order_account where id > 4 and id < 100";          //5.你想要查找的表名。
            Class.forName(driver);                              //6.注册驱动程序，用java.lang包下面的class类里面的Class.froName();方法 此处的driver就是1里面定义的driver，也可以  Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);//7.获取数据库连接,使用java.sql里面的DriverManager的getConnectin(String url , String username ,String password )来完成
            Statement stmt = conn.createStatement();   //8.构造一个statement对象来执行sql语句：主要有Statement，PreparedStatement，CallableStatement三种实例来实现
            ResultSet rs = stmt.executeQuery(querySql);//9.执行sql并返还结束 ；ResultSet executeQuery(String sqlString)：用于返还一个结果集（ResultSet）对象。
            while (rs.next()) {
//                 accounts.add(new Account(rs.getString("phone"), rs.getString("password")));
            }

            String s = "" +
                    ""
                + "" +
                    "18585814760----666666----张鑫----330781199512020210\n" +
                    "18593814983----666666----王历祥----522123198409293030\n" +
                    "13116495284----666666----康厚华----520103196502082418\n" +
                    "18585419528----666666----高其后----522428199004284235\n" +
                    "18586943434----666666----严琨----520111199712262111\n" +
                    "15519067573----666666----林明----350128198308145811\n" +
                    "18585424740----666666----吴大猛----420983198412018114\n" +
                    "15585119804----666666----戈艳婷----130802198202211220\n" +
                    "13017485834----666666----田许丹----513821199102020440\n" +
                    "15599128190----666666----孟维超----230122199005013239\n" +
                    "15599159675----666666----岳伟----513701198304020419\n" +
                    "15692719541----666666----龙美----522425199509295723\n" +
                    "18585413569----666666----凌涛----341002199201300230\n" +
                    "15519029376----666666----汪诚----421102199108240451\n" +
                    "15628000779----666666----明平生----360423198408252952\n" +
                    "18593803656----666666----李兴顺----440506197901280038\n" +
                    "18585415797----666666----何文平----362528198201056515\n" +
                    "15519143236----666666----杨花----522731200003202904\n" +
                    "18585419103----666666----郭振升----140322199505044217\n" +
                    "18593828167----666666----江博伟----445221198910244975\n" +
                    "13158015402----666666----刘威----421121199310170011\n" +
                    "18593800650----666666----孟庆阳----371524199511010512\n" +
                    "15685169647----666666----陈焕超----321023197808066814\n" +
                    "15519546125----666666----黄军良----44142519790928293X\n" +
                    "18586814754----666666----马苗苗----612724198703052124\n" +
                    "18585435354----666666----康占权----152221197806153217\n" +
                    "15685312410----666666----王琳琳----120106198006155511\n" +
                    "18585412972----666666----马亮----610528198410062751\n" +
                    "13017436024----666666----莫金云----450981199107303012\n" +
                    "18585812249----666666----马跃----510922198705211718\n" +
                    "18593843570----666666----李春南----460028199610092030\n" +
                    "18585410751----666666----孙婷婷----230403198411300527\n" +
                    "18585025748----666666----罗永----511024199007203832\n" +
                    "18585429437----666666----刘翘铭----220104198404131815\n" +
                    "18593822091----666666----许晓岚----130133198904190010\n" +
                    "18593826239----666666----刘双喜----500235199011167376\n" +
                    "18593808246----666666----吴思豪----362229199401280014\n" +
                    "18586843145----666666----王杰----522328197909025313\n" +
                    "15519141302----666666----高壮壮----41061119911213701X\n" +
                    "18585834210----666666----车延辉----370687198804282074\n" +
                    "15519588862----666666----线慕容----620102198406213023\n" +
                    "18585439303----666666----欧阳毅昭----429005199503255654\n" +
                    "18585819145----666666----李燕----432822197810221645\n" +
                    "15519066362----666666----郑成浩----330382199005060538\n" +
                    "18586816146----666666----漆亮----510902198904158491\n" +
                    "15599152264----666666----国旭南----330226199412066711\n" +
                    "18585419451----666666----毛林伟----150302198204214018\n" +
                    "15597720471----666666----陈毅----429005199207094331\n" +
                    "18585027943----666666----高治良----62222619960611361X\n" +
                    "15519152129----666666----李强----362429198912052319\n" +
                    "18585811340----666666----庞久富----321281198302178092\n" +
                    "15599101961----666666----胡斌斌----320902198411251019\n" +
                    "15599124219----666666----梁龙----642221198807300216\n" +
                    "18585419547----666666----吴振伸----441223197910256217\n" +
                    "15585118153----666666----程兴东----370832199505030037\n" +
                    "18586904795----666666----代创创----342224198710200014\n" +
                    "15599109302----666666----罗明海----533421197808050214\n" +
                    "15519545042----666666----林国栋----441781198704192713\n" +
                    "15585179363----666666----罗璐----431229199102210021\n" +
                    "18593820849----666666----覃韦新----452124198910111518\n" +
                    "15519157970----666666----吕品----210804198209283027\n" +
                    "18593826841----666666----彭贺----130181198405214517\n" +
                    "18585041343----666666----赵合勇----37280119771205323X\n" +
                    "15597727421----666666----郑淑娟----370783198509230386\n" +
                    "15519028713----666666----潘俊宇----513701199412124412\n" +
                    "13037850379----666666----代世明----371102198110103254\n" +
                    "18593802085----666666----沙花花----320981198803017462\n" +
                    "13027843040----666666----金荣华----530126198503200856\n" +
                    "15519146441----666666----曹敏----321183198903041611\n" +
                    "18585621020----666666----黄真----510108198403051818\n" +
                    "18585814921----666666----张廷银----622201198601106638\n" +
                    "15519176059----666666----马苧----622922199009090547\n" +
                    "18585439646----666666----邓飞----340207199001070014\n" +
                    "15585118750----666666----刘玉兵----452330198110161910\n" +
                    "18585834193----666666----刘智----421182199302221010\n" +
                    "13098515487----666666----刘佳----230125199608080325\n" +
                    "15599120282----666666----齐国雷----320826199402010811\n" +
                    "18593824650----666666----涂小霞----420621198905156325\n" +
                    "18593818375----666666----王涛----420321199307211118\n" +
                    "15519144853----666666----谢冠鹏----430422198811270618\n" +
                    "15519015361----666666----罗琴----420822198809073981\n" +
                    "15585126641----666666----江中建----411330198710063419\n" +
                    "18586838962----666666----龙星天----440881199306225312\n" +
                    "15599100539----666666----李小弟----450421198802096010\n" +
                    "15685419806----666666----陈坤灵----421022198204292417\n" +
                    "18585415890----666666----刘强----131127198811183616\n" +
                    "15519029879----666666----吴广----62042219880414001X\n" +
                    "15519172595----666666----陈屿杭----500239199901255920\n" +
                    "18585041943----666666----王斌----612321198601032113\n" +
                    "18586831574----666666----罗宗毅----452630199204120010\n" +
                    "15519172183----666666----张英----340824199601122024\n" +
                    "15519143825----666666----黎亮----232325198710261817\n" +
                    "18586816449----666666----徐明磊----41102319920124251X\n" +
                    "18585421748----666666----李广----420984198709150016\n" +
                    "18585410064----666666----朱玉龙----371325199709077937\n" +
                    "18586932435----666666----李腾超----532901199612270012\n" +
                    "13078548481----666666----戚龙干----320911198905140921\n" +
                    "18585047814----666666----蒋波----430404198103240511\n" +
                    "18586928453----666666----胡海峰----652823199107280818\n" +
                    "15599137646----666666----荣为田----342401198203083614\n" +
                    "18585415674----666666----王有为----420104198802064053\n" +
                    "18593802451----666666----梅贵水----500102199607309192\n" +
                    "18585818453----666666----张艳艳----13262819810616414X\n" +
                    "15519164140----666666----李奔----500231199907102457\n" +
                    "15685198335----666666----季尔祥----341103198211264410\n" +
                    "15685177719----666666----王敏----370831199010053169\n" +
                    "18585424910----666666----黄芳----330522198608170410\n" +
                    "15685100492----666666----董同书----371327199501205410\n" +
                    "15519029176----666666----葛士洪----320582198112268814\n" +
                    "15519024429----666666----郭小宁----612401199004281976"
                    ;

            String remark = null;
            for (String s1 : s.split("\n")) {
                accounts.add(new Account(s1.split("----")[0], s1.split("----")[1]));
            }

            for (Account account : accounts) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sql = "INSERT INTO fx_order_account(phone,password,remark) VALUES(?,?,?)";
                            // 获取PrepareStatement对象
                            PreparedStatement preparedStatement = conn.prepareStatement(sql);
                            // 填充占位符
                            preparedStatement.setString(1, account.getPhone());
                            preparedStatement.setString(2, account.getPassword());
                            preparedStatement.setString(3, remark);
                            // 执行sql
                            int num = preparedStatement.executeUpdate();// 返回影响到的行数

                            System.out.println("一共影响到" + num + "行");
                            /*JdbcTest jdbcTest = new JdbcTest(account.getPhone(), account.getPassword());
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
                                            .data("maddr[name]", "钟尚华")
                                            .data("maddr[mobile]", "13540112643")
                                            .data("maddr[area]", "mainland:四川省/成都市/成华区:2535")
                                            .data("maddr[addr]", "东林小区--老板数签签（串串店）")
                                            .data("maddr[is_default]", "true")
                               .execute();
                           System.out.println(account.getPhone() + "----" + account.getPassword() + "---ok");*/
//                Thread.sleep(17 * 1000);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
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
            org.jsoup.Connection.Response response = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
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
            org.jsoup.Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
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
            org.jsoup.Connection.Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html")
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