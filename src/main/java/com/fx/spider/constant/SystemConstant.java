package com.fx.spider.constant;

public interface SystemConstant {

    String GOODS_URL = "https://mall.phicomm.com/cart-fastbuy-{0}-1.html";

    String APP_GOODS_URL = "https://mall.phicomm.com/index.php/m/cart-fastbuy-{0}-1.html";

    Integer UPDATE_CODE_SECOND = 44;

    int THREAD_WAIT_TIME = 350;

    int TIME_OUT = 45 * 1000;

    int TASK_COUNT = 2;

    String IP_URL = "http://h.wandouip.com/get/ip-list?pack=0&num=" + 10 + "&xy=2&type=2&lb=\\r\\n&mr=2&app_key=";

}
