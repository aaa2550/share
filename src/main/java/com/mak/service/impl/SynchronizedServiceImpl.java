package com.mak.service.impl;

import com.mak.api.ApiClient;
import com.mak.common.Constant;
import com.mak.dao.ShareDao;
import com.mak.dao.ShareDayDao;
import com.mak.dao.ShareDayDetailDao;
import com.mak.dto.Share;
import com.mak.dto.ShareDay;
import com.mak.dto.ShareDayDetail;
import com.mak.http.HttpUtil;
import com.mak.service.SynchronizedService;
import com.mak.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.Date;
import java.util.List;
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

    @Override
    public void synchronizedProxys() {
        Stream.iterate(1, i->i+1).limit(10).map(i->ApiClient.proxyList(Constant.API_PROXY + i)).collect(Collectors.toList());
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
                List<ShareDay> shareDays = shareSingeDayDao.findDate(share.getCode());
                shareDays.parallelStream().forEach(s -> {
                    System.out.println("execute share=" + share.getCode() + ",date=" + DateUtil.format(s.getDate()));
                    synchronizedDayDetail(share.getCode(), share.getName(), s.getDate(), null);
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
