package com.mak.dto;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public class Pojo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
