package com.mak.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by yanghailong on 2018/1/9.
 */
@Data
public class ShareDayRxtDetail extends Pojo {
    
    private Integer id;
    private Date date;  //股票日期
    private String code;  //股票代码
    private String name;  //股票中文名
    private Date tradeTime;  //成交时间
    private Double price;  //成交价
    private Double p1Change;  //涨跌幅
    private Double priceChange;  //价格变动
    private Integer num;  //成交量(手)
    private Double money;  //成交额(元)

}
