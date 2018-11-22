package com.fx.spider.model;

import java.net.Proxy;
import java.util.Date;
import lombok.Data;

@Data
public class OrderAccount {

    public OrderAccount() {}

    public OrderAccount(String phone, String password, Proxy proxy) {
        this.phone = phone;
        this.password = password;
        this.proxy = proxy;
    }

    public OrderAccount(String phone, String password) {
        this.phone = phone;
        this.password = password;
        this.proxy = proxy;
    }

    private Integer id;

    private String phone;

    private String password;

    private String username;

    private String goodsName;


    private String vcCard;

    private String address;

    private String defaultAddress;

    private String vc;
    private String vc2;

    private String renzheng;

    private boolean passwordFaild;

    /** 物流号 */
    private String logisticsNum;

    private String logisticsInfo;

    private String orderNo;

    private String status;

    private String status2;

    private Date createDate;

    private String orderCreateDate;

    private String remark;

    private String cookie;

    private String payBase64;

    private Proxy proxy;

}
