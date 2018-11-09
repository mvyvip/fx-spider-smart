package com.fx.spider.task;

import com.fx.spider.constant.SystemConstant;
import com.fx.spider.model.OrderAccount;
import com.fx.spider.service.AccountService;
import java.text.MessageFormat;
import java.util.List;
import javax.annotation.PostConstruct;

import com.fx.spider.task.runnable.SpliderRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpiderTask {

    @Value("${start}")
    private Integer start;

    @Value("${size}")
    private Integer size;

    @Value("${count}")
    private Integer count;

    @Value("${key}")
    private String key;

    @Autowired
    private AccountService accountService;

    @PostConstruct
    public void start() throws Exception {

        String[] values = accountService.findConfigByKey(SystemConstant.GOODS_URL).split("-");
        String goods = values[0];
        String goodsUrl = MessageFormat.format(SystemConstant.GOODS_URL, values[1]);
        String vc = values[2];

        log.info("===========================================================================");
        log.info("今日抢购:{}, vc:{}, url:{}", goods, vc, goodsUrl);
        log.info("单个号单次抢购总IP:{}", count);
        log.info("===========================================================================");

        List<OrderAccount> accounts = accountService.findByStatus();
        if ((start + size) > accounts.size()) {
            start = accounts.size() - size;
        }
        if (start >= accounts.size()) {
            log.info("开始下标：[{}],  总共数据：[{}],未启动成功", start, accounts.size());
            return;
        }
        log.info("参与总数：{}, 开始下标：{}, 结束下标：{}", accounts.size(), start, start + size);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = start; i < start + size; i++) {
//                    for (int i = start; i < 3; i++) {
                        int finalI = i;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    OrderAccount account = accounts.get(finalI);
                                    new Thread(new SpliderRunnable(account, goods, goodsUrl, vc, key)).start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        Thread.sleep(2500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
