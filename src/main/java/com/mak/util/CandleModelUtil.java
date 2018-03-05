package com.mak.util;

import com.mak.dto.ShareDay;

/**
 * Created by yanghailong on 2018/3/5.
 */
public class CandleModelUtil {

    public static boolean isMinYang(ShareDay shareDay) {
        return shareDay.getP1Change() >= 0 && shareDay.getP1Change() < 5;
    }

    public static boolean isMinYin(ShareDay shareDay) {
        return shareDay.getP1Change() <= 0 && shareDay.getP1Change() > -5;
    }

    public static boolean xiaYingXian(ShareDay shareDay) {
        return (shareDay.getOpen().equals(shareDay.getHigh()) || shareDay.getClose().equals(shareDay.getHigh()))
        && shareDay.getOpen() > shareDay.getLow() && shareDay.getClose() > shareDay.getLow();
    }

}
