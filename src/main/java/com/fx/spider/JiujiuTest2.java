package com.fx.spider;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * Created by lt on 2018/8/28 0028.
 */
public class JiujiuTest2 {

    public static void main(String[] args) throws Exception {

     /*   String s = "abcdefg";
        String start = "b";
        String end = "e";
        String str = "###";
        StringBuilder sb = new StringBuilder();
        String s1 = sb.append(s.split(start)[0])
                .append(str)
                .append(s.split(start)[1].split(end)[1]).toString();
        System.out.println(s1);*/


        while(true) {
          try {
              Connection.Response execute = Jsoup.connect("http://99shou.cn/charge/phone/receive/info")
                      .method(Connection.Method.POST)
                      .header("Cookie", "SESSION=3473ede2-d632-4b1f-abbe-e178d9e8149d; UM_distinctid=167026fa570310-007fb7e139d929-514d2f1f-384000-167026fa5721180; CNZZDATA1265035294=1302909670-1541930659-null%7C1541930659; uname=leemain")
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


    }

}
