package com.mak.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by lenovo on 2018/1/6.
 */
@Data
public class ShareSingeDayRight extends Pojo {

    private Integer id;
    private String code;
    private String name;
    private Date date;
    private Double open;
    private Double high;
    private Double close;
    private Double low;
    private Double volume;
    private Double totalPrice;

}
