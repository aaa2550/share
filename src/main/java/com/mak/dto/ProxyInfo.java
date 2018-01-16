package com.mak.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by lenovo on 2018/1/9.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProxyInfo extends Pojo {

    private String ip;
    private String port;

}
