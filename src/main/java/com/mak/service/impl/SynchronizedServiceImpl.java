package com.mak.service.impl;

import com.alibaba.fastjson.util.IOUtils;
import com.mak.api.ApiClient;
import com.mak.common.Constant;
import com.mak.dao.*;
import com.mak.dto.*;
import com.mak.http.ProxyPool;
import com.mak.service.SynchronizedService;
import com.mak.util.DateUtil;
import com.mak.util.NumberUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.web.ProxyingHandlerMethodArgumentResolver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lenovo on 2018/1/6.
 */
@Service
public class SynchronizedServiceImpl implements SynchronizedService {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizedServiceImpl.class);

    @Resource
    private ShareDao shareDao;

    @Resource
    private ShareDayDao shareSingeDayDao;

    @Resource
    private ShareDayDetailDao shareDayDetailDao;

    @Resource
    private ShareDayRxtDetailDao shareDayRxtDetailDao;

    @Resource
    private ProxyInfoDao proxyInfoDao;

    @Resource
    private ShareDayDao shareDayDao;

    @Override
    public void synchronizedProxys() {
        //proxyInfoDao.deleteAll();
        Stream.iterate(1, i->i+1)
                .limit(30)
                .map(i->ApiClient.proxyListAli(Constant.API_PROXY + Base64.getUrlEncoder().encodeToString("6Zi/6YeM5LqR".getBytes())  + "&page=" + i))
                .flatMap(Collection::stream)
                .forEach(proxyInfoDao::insert);
        ProxyPool.getProxyPool().reSetProxies(proxyInfoDao.findAll());
    }

    @Override
    public void synchronizedShares() {
        List<Share> shareList = ApiClient.sharesList(Constant.API_SHARES);
        shareDao.insert(shareList);
    }

    @Override
    public void synchronizedHistory() {
        List<Share> shares = shareDao.findAll();
        try {
            int[] index = new int[1];
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Constant.FILE_PATH_SYNCHRONIZED_HISTORY)));
            shares.forEach(s -> {
                System.out.println(++index[0]);
                synchronizedHistory(s.getCode(), s.getName(), bufferedWriter);
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void synchronizedDayDetail() {
        List<Share> shares = shareDao.findAll();
        int i = 0;
        try {
            for (Share share : shares) {
                //BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Constant.FILE_PATH_SYNCHRONIZED_DAY_DETAIL)));
                List<ShareDay> shareDays = shareSingeDayDao.findDate(share.getCode(), DateUtil.parse("2017-01-01"), DateUtil.parse("2018-01-11"));
                shareDays.parallelStream().forEach(s -> {
                    synchronizedDayDetail(share.getCode(), share.getName(), s.getDate(), null);
                    System.out.println("execute share=" + share.getCode() + ",date=" + DateUtil.format(s.getDate()));
                });
                i++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("synchronizedDayDetail error.i=" + i);
            logger.error("synchronizedDayDetail error.i=" + i, e);
        }
    }

    @Override
    public void synchronizedDayRxtDetail() {
        Date currentDay = DateUtil.parse("2017-10-01");
        Date today = new Date();
        List<ShareDay> currentDays;
        ConcurrentHashMap<String, Double> yesterdayHashMap = new ConcurrentHashMap<>();

        while (currentDay.before(today)) {
            currentDays = shareDayDao.find(currentDay);
            currentDays.parallelStream()
                    .peek(d->yesterdayHashMap.put(d.getCode(), d.getClose()))
                    .forEach(d->{
                        try {
                            String fileName = d.getCode().startsWith("6") ? "SH#" + d.getCode() + ".txt" : "SZ#" + d.getCode() + ".txt";
                            Files.lines(Paths.get(Constant.FILE_PATH_RXT, fileName))
                                    .skip(2)
                                    .map(l->toShareDayRxtDetail(yesterdayHashMap.get(d.getCode()), d.getOpen(), d.getCode(), d.getName(), l))
                                    .forEach(shareDayRxtDetailDao::insert);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            currentDay = DateUtil.nextDay(currentDay);
        }
    }

    @Override
    public void synchronizedDayDetail(String code, String name, Date date, BufferedWriter bufferedWriter) {
        String codeParam = code.startsWith("6") ? "sh" + code : "sz" + code;
        List<ShareDayDetail> shareDayDetails = ApiClient.shareDayDetail(Constant.API_SHARES_DAY_DETAIL + "?date="+DateUtil.format(date)+"&symbol=" + codeParam, code, name, date);
        shareDayDetailDao.insert(shareDayDetails);
    }

    @Override
    public void synchronizedHistory(String code, String name, BufferedWriter bufferedWriter) {
        String codeParam = code.startsWith("6") ? "sh" + code : "sz" + code;
        List<ShareDay> shareSingeDays = ApiClient.history(Constant.API_HISTORY + "?code=" + codeParam + "&type=last", code, name);
        StringBuilder stringBuilder = new StringBuilder();
        shareSingeDays.forEach(s -> stringBuilder.append(shareSingeDayToString(s)));
        try {
            bufferedWriter.write(stringBuilder.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //shareSingeDayDao.insert(shareSingeDays);
    }

    @Override
    public void synchronizedHistoryRight() {
        List<Share> shares = shareDao.findAll();
        try {
            int[] index = new int[1];
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\lenovo\\Desktop\\synchronizedHistoryRight.sql")));
            shares.forEach(s->{
                System.out.println(++index[0]);
                synchronizedHistoryRight(s.getCode(), s.getName(), bufferedWriter);
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void synchronizedHistoryRight(String code, String name, BufferedWriter bufferedWriter) {
        List<ShareDayRight> shareSingeDayRights = new ArrayList<>();
        shareSingeDayRights .addAll(ApiClient.historyRight(Constant.API_HISTORY_RIGHT, code, name,"2017", "1"));
        shareSingeDayRights .addAll(ApiClient.historyRight(Constant.API_HISTORY_RIGHT, code, name,"2017", "2"));
        shareSingeDayRights .addAll(ApiClient.historyRight(Constant.API_HISTORY_RIGHT, code, name,"2017", "3"));
        shareSingeDayRights .addAll(ApiClient.historyRight(Constant.API_HISTORY_RIGHT, code, name,"2017", "4"));
        shareSingeDayRights .addAll(ApiClient.historyRight(Constant.API_HISTORY_RIGHT, code, name,"2018", "1"));
        StringBuilder stringBuilder = new StringBuilder();
        shareSingeDayRights.forEach(s->stringBuilder.append(shareDayRightToString(s)));
        try {
            bufferedWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String shareDayRightToString(ShareDayRight shareDayRight) {
        return ("insert into share_singe_day_right(code,name,date,open,high,close," +
                "low,volume,totalPrice) " +
                "values(") +
                shareDayRight.getCode() + "," +
                shareDayRight.getName() + "," +
                shareDayRight.getDate() + "," +
                shareDayRight.getOpen() + "," +
                shareDayRight.getHigh() + "," +
                shareDayRight.getClose() + "," +
                shareDayRight.getLow() + "," +
                shareDayRight.getVolume() + "," +
                shareDayRight.getTotalPrice() + ");\n";
    }

    private ShareDayRxtDetail toShareDayRxtDetail(Double afterDayClose, Double todayOpen, String code, String name, String str) {
        String[] params = str.split("\\r");
        ShareDayRxtDetail shareDayRxtDetail = new ShareDayRxtDetail();
        shareDayRxtDetail.setCode(code);
        shareDayRxtDetail.setName(name);
        shareDayRxtDetail.setDate(DateUtil.parse(params[0]));
        shareDayRxtDetail.setTradeTime(DateUtil.parse(params[1], "HHmm"));
        shareDayRxtDetail.setPrice(Double.valueOf(params[5]));
        shareDayRxtDetail.setPriceChange(afterDayClose == null ? null : afterDayClose - shareDayRxtDetail.getPrice());
        shareDayRxtDetail.setP1Change(afterDayClose == null ? null : NumberUtil.percent(afterDayClose, shareDayRxtDetail.getPrice()));
        shareDayRxtDetail.setNum(Integer.valueOf(params[6]));
        shareDayRxtDetail.setMoney(Double.valueOf(params[7]));
        return shareDayRxtDetail;
    }

    private String shareSingeDayToString(ShareDay shareSingeDay) {
        return ("insert into share_singe_day(code,name,date,open,high,close," +
                "low,volume,priceChange,p1Change,ma5,ma10,ma20,v1Ma5,v1Ma10,v1Ma20,turnover) " +
                "values('") +
                shareSingeDay.getCode() + "','" +
                shareSingeDay.getName() + "','" +
                DateUtil.format(shareSingeDay.getDate(), DateUtil.DEFAULT_TIME) + "'," +
                shareSingeDay.getOpen() + "," +
                shareSingeDay.getHigh() + "," +
                shareSingeDay.getClose() + "," +
                shareSingeDay.getLow() + "," +
                shareSingeDay.getVolume() + "," +
                shareSingeDay.getPriceChange() + "," +
                shareSingeDay.getP1Change() + "," +
                shareSingeDay.getMa5() + "," +
                shareSingeDay.getMa10() + "," +
                shareSingeDay.getMa20() + "," +
                shareSingeDay.getV1Ma5() + "," +
                shareSingeDay.getV1Ma10() + "," +
                shareSingeDay.getV1Ma20() + "," +
                shareSingeDay.getTurnover() + ");\n";
    }
}
