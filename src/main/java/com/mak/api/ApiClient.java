package com.mak.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mak.config.HttpUtil;
import com.mak.dto.Share;
import com.mak.dto.ShareDay;
import com.mak.dto.ShareDayDetail;
import com.mak.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lenovo on 2018/1/6.
 */
public class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    public static List<Share> sharesList(String urlStr) {
        try {
            URL url = new URL(urlStr);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "GBK"));
            return bufferedReader.lines().skip(1).map(l->toShare(l.split(","))).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<ShareDayDetail> shareDayDetail(String urlStr) {
        try {
            URL url = new URL(urlStr);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "GBK"));
            return bufferedReader.lines().skip(1).map(l->toShareSingeDayDetail(l.split(","))).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static ShareDayDetail toShareSingeDayDetail(String[] shareSingeDayDetailParam) {
        ShareDayDetail shareSingeDayDetail = new ShareDayDetail();
        shareSingeDayDetail.setTradeTime(DateUtil.parse(shareSingeDayDetailParam[0], DateUtil.DEFAULT_TIME));
        shareSingeDayDetail.setPrice(Double.valueOf(shareSingeDayDetailParam[1]));
        shareSingeDayDetail.setPriceChange(Double.valueOf(shareSingeDayDetailParam[2]));
        shareSingeDayDetail.setNum(Integer.valueOf(shareSingeDayDetailParam[3]));
        shareSingeDayDetail.setMoney(Double.valueOf(shareSingeDayDetailParam[4]));
        shareSingeDayDetail.setNature(shareSingeDayDetailParam[5]);
        return shareSingeDayDetail;
    }
    
    private static Share toShare(String[] shareParam) {
        Share share = new Share();
        share.setCode(shareParam[0]);
        share.setName(shareParam[1]);
        share.setIndustry(shareParam[2]);
        share.setArea(shareParam[3]);
        share.setPe(Double.valueOf(shareParam[4]));
        share.setOutstanding(Double.valueOf(shareParam[5]));
        share.setTotals(Double.valueOf(shareParam[6]));
        share.setTotalAssets(Double.valueOf(shareParam[7]));
        share.setLiquidAssets(Double.valueOf(shareParam[8]));
        share.setFixedAssets(Double.valueOf(shareParam[9]));
        share.setEsp(Double.valueOf(shareParam[12]));
        share.setBvps(Double.valueOf(shareParam[13]));
        share.setPb(Double.valueOf(shareParam[14]));
        share.setTimeToMarket(DateUtil.parse(shareParam[15]));
        share.setUndp(Double.valueOf(shareParam[16]));
        share.setHolders(Double.valueOf(shareParam[22]));
        return share;
    }

    public static List<ShareDay> history(String url, String code, String name) {
        try {
            String result = HttpUtil.getIntance().get(url);
            Map<String, JSONArray> map = JSON.parseObject(result, Map.class);
            List<ShareDay> shareSingeDays = new ArrayList<>();
            JSONArray jsonArray = map.get("record");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray jsonArray1 = (JSONArray)jsonArray.get(i);
                ShareDay shareSingeDay = new ShareDay();
                shareSingeDay.setCode(code);
                shareSingeDay.setName(name);
                shareSingeDay.setDate(DateUtil.parse(jsonArray1.get(0).toString()));
                shareSingeDay.setOpen(Double.valueOf(jsonArray1.get(1).toString()));
                shareSingeDay.setHigh(Double.valueOf(jsonArray1.get(2).toString()));
                shareSingeDay.setClose(Double.valueOf(jsonArray1.get(3).toString()));
                shareSingeDay.setLow(Double.valueOf(jsonArray1.get(4).toString()));
                shareSingeDay.setVolume(Double.valueOf(jsonArray1.get(5).toString()));
                shareSingeDay.setPriceChange(Double.valueOf(jsonArray1.get(6).toString()));
                shareSingeDay.setP1Change(Double.valueOf(jsonArray1.get(7).toString()));
                shareSingeDay.setMa5(Double.valueOf(jsonArray1.get(8).toString()));
                shareSingeDay.setMa10(Double.valueOf(jsonArray1.get(9).toString()));
                shareSingeDay.setMa20(Double.valueOf(jsonArray1.get(10).toString()));
                shareSingeDay.setV1Ma5(Double.valueOf(jsonArray1.get(11).toString().replaceAll(",", "")));
                shareSingeDay.setV1Ma10(Double.valueOf(jsonArray1.get(12).toString().replaceAll(",", "")));
                shareSingeDay.setV1Ma20(Double.valueOf(jsonArray1.get(13).toString().replaceAll(",", "")));
                if (jsonArray1.size() >= 15) {
                    shareSingeDay.setTurnover(Double.valueOf(jsonArray1.get(14).toString()));
                }
                shareSingeDays.add(shareSingeDay);
            }
            return shareSingeDays;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("history error.url=" + url, e);
            System.out.println("history error.url=" + url);
            return Collections.emptyList();
        }
    }

}
