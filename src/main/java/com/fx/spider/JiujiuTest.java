package com.fx.spider;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by lt on 2018/8/28 0028.
 */
public class JiujiuTest {

    public static String replaceByPrefix(String str, String start, String end, String replace) {
        System.out.println("start> " + start + "  end > " + end);
        if(!str.contains(start)) {
            System.out.println("start : " + start + " is not exist");
            return str;
        }
        if(!str.contains(end)) {
            System.out.println("end : " + end + " is not exist");
            return str;
        }
        StringBuilder sb = new StringBuilder();
        System.err.println(str.split(start)[0]);
        String s1 = sb.append(str.split(start)[0])
                .append(replace)
                .append(str.split(start)[1].split(end)[1]).toString();
        return s1;
    }

    public static void main(String[] args) throws Exception {

        String str = "var _1c = function() {\n" +
                "\t\t\n" +
                "\t\t\tvar cookie = '__jsl_clearance=1541911057.905|0|' + (function() {\n" +
                "\t\t\t\tvar _59 = [function(_1c) {\n" +
                "\t\t\t\t\treturn eval('String.fromCharCode(' + _1c + ')')\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t(function() {\n" +
                "\t\t\t\t\tvar _1c = document.createElement('div');\n" +
                "\t\t\t\t\t_1c.innerHTML = '<a href=\\'/\\'>_16</a>';\n" +
                "\t\t\t\t\t_1c = _1c.firstChild.href;\n" +
                "\t\t\t\t\tvar _59 = _1c.match(/https?:\\/\\//)[0];\n" +
                "\t\t\t\t\t_1c = _1c.substr(_59.length).toLowerCase();\n" +
                "\t\t\t\t\treturn function(_59) {\n" +
                "\t\t\t\t\t\tfor (var _16 = 0; _16 < _59.length; _16++) {\n" +
                "\t\t\t\t\t\t\t_59[_16] = _1c.charAt(_59[_16])\n" +
                "\t\t\t\t\t\t};\n" +
                "\t\t\t\t\t\treturn _59.join('')\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t})()],\n" +
                "\t\t\t\t_16 = [[(7 + [] + [[]][0])], [[6] + (5 + []), (7 + [] + [[]][0]) + [6], ( - ~~~ {} + [[]][0]) + [2] + (( + []) + [] + [[]][0]), ((2 ^ -~~~ {}) + [] + []) + (7 + [] + [[]][0]), (5 + []) + (( + []) + [] + [[]][0]), [6] + [6], (7 + [] + [[]][0]) + ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + [])], [(5 + [])], [( - ~~~ {} + [[]][0]) + ( - ~~~ {} + [[]][0]) + ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []), ( - ~~~ {} + [[]][0]) + ( - ~~~ {} + [[]][0]) + (( + []) + [] + [[]][0])], [[3 + ( - ~ - ~ [] ^ -~~~ {}) + ( - ~ - ~ [] ^ -~~~ {})]], [( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []) + ( - ~~~ {} + [[]][0]), [3 + ( - ~ - ~ [] ^ -~~~ {}) + ( - ~ - ~ [] ^ -~~~ {})] + ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + [])], [(7 + [] + [[]][0])], [( - ~~~ {} + [[]][0]) + (( + []) + [] + [[]][0]) + ((2 ^ -~~~ {}) + [] + []), [3 + ( - ~ - ~ [] ^ -~~~ {}) + ( - ~ - ~ [] ^ -~~~ {})] + (( + []) + [] + [[]][0]), (5 + []) + ( - ~~~ {} + [[]][0]), ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []) + (5 + []), ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []) + (4 + [] + [[]][0]), ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []) + (4 + [] + [[]][0])], [[3 + ( - ~ - ~ [] ^ -~~~ {}) + ( - ~ - ~ [] ^ -~~~ {})]], [[6] + ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []), ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []) + [2], [3 + ( - ~ - ~ [] ^ -~~~ {}) + ( - ~ - ~ [] ^ -~~~ {})] + (( + []) + [] + [[]][0]), ( - ~~~ {} + [[]][0]) + ( - ~~~ {} + [[]][0]) + [6], [6] + ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + []), (7 + [] + [[]][0]) + ((2 ^ -~~~ {}) + [] + []), (7 + [] + [[]][0]) + ((2 ^ -~~~ {}) + [] + []), ((2 ^ -~~~ {}) + [] + []) + (7 + [] + [[]][0]), (5 + []) + ( - ~~~ {} + [[]][0]), [6] + ( - ~ ! {} + ( - ~ ! {} + [( - ~~~ {} << -~~~ {})] >> ( - ~~~ {} << -~~~ {})) + ( - ~ ! {} << ( - ~~~ {} << -~~~ {})) + [])]];\n" +
                "\t\t\t\tfor (var _1c = 0; _1c < _16.length; _1c++) {\n" +
                "\t\t\t\t\t_16[_1c] = _59.reverse()[(( + []) + [] + [[]][0])](_16[_1c])\n" +
                "\t\t\t\t};\n" +
                "\t\t\t\treturn _16.join('')\n" +
                "\t\t\t})() + ';Expires=Sun, 11-Nov-18 05:37:37 GMT;Path=/;'\n" +
                "\t\t\treturn cookie;\n" +
                "\t\t};";

        StringBuilder sb = new StringBuilder();
//        System.out.println(str.split("document.createElement")[0] + "#####");
        System.out.println("document.createElement('div');");

        sb.append(str.split("document.createElement")[0])
        .append("\"https://mall.phicomm.com/\"")
                .append(str.split("firstChild\\.href")[1]);

        System.out.println(sb.toString());

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Invocable invocable2 = (Invocable) engine;
        String over = (String) invocable2.invokeFunction("getClearance2");

//        System.out.println(str.split("document.createElement\\('div'\\)")[0]);
//        System.out.println(str.split("document").length);

//        System.out.println(replaceByPrefix(str, "document.createElement('div');", "href;", "###"));

     /*   String s = "abcdefg";
        String start = "b";
        String end = "e";
        String str = "###";
        StringBuilder sb = new StringBuilder();
        String s1 = sb.append(s.split(start)[0])
                .append(str)
                .append(s.split(start)[1].split(end)[1]).toString();
        System.out.println(s1);*/


       /* while(true) {
          try {
              Connection.Response execute = Jsoup.connect("http://99shou.cn/charge/phone/receive/info")
                      .method(Connection.Method.POST)
                      .header("Cookie", "UM_distinctid=16701275e2b4e4-06afd1a5eac402-514d2f1f-384000-16701275e2dde0; uname=%E5%85%A8%E6%9D%91%E5%B8%8C%E6%9C%9B; SESSION=d338b212-623e-4acb-9733-cecbc316cf67; CNZZDATA1265035294=10172223-1541909050-null%7C1541930659")
                      .data("facevalue", "100")
                      .ignoreContentType(true)
                      .data("receiveNum", "1")
                      .data("channel[0]", "1")
                      .data("channel[1]", "2")
                      .data("channel[2]", "3")
                      .timeout(5555)
                      .execute();
              System.out.println(execute.body());
              Thread.sleep(2800);

          } catch (Exception e) {}
       }
*/

    }

}
