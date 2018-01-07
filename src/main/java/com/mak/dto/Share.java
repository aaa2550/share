package com.mak.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by lenovo on 2018/1/6.
 */
@Data
public class Share extends Pojo {

    private String code;
    private String name;
    private String industry;
    private String area;
    private Double pe;
    private Double outstanding;
    private Double totals;
    private Double totalAssets;
    private Double liquidAssets;
    private Double fixedAssets;
    private Double reserved;
    private Double esp;
    private Double bvps;
    private Double pb;
    private Date timeToMarket;
    private Double undp;
    private Double holders;
            
}
