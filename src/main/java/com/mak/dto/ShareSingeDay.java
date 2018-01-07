package com.mak.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by lenovo on 2018/1/6.
 */
@Data
public class ShareSingeDay extends Pojo {

    private Integer id;
    private String code;
    private String name;
    private Date date;
    private Double open;
    private Double high;
    private Double close;
    private Double low;
    private Double volume;
    private Double priceChange;
    private Double p1Change;
    private Double ma5;
    private Double ma10;
    private Double ma20;
    private Double v1Ma5;
    private Double v1Ma10;
    private Double v1Ma20;
    private Double turnover;


}
