package com.mak.service.impl;

import com.mak.api.ApiClient;
import com.mak.common.Constant;
import com.mak.dao.ShareDao;
import com.mak.dao.ShareSingeDayDao;
import com.mak.dto.Share;
import com.mak.dto.ShareSingeDay;
import com.mak.service.SynchronizedService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;

/**
 * Created by lenovo on 2018/1/6.
 */
@Service
public class SynchronizedServiceImpl implements SynchronizedService {

    @Resource
    private ShareDao shareDao;

    @Resource
    private ShareSingeDayDao shareSingeDayDao;

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
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\lenovo\\Desktop\\sql.sql")));
            shares.forEach(s->{
                System.out.println(++index[0]);
                synchronizedHistory(s.getCode(), s.getName(), bufferedWriter);
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void synchronizedHistory(String code, String name, BufferedWriter bufferedWriter) {
        String codeParam = code.startsWith("6")? "sh" + code : "sz" + code;
        List<ShareSingeDay> shareSingeDays = ApiClient.history(Constant.API_HISTORY + "?code="+codeParam+"&type=last", code, name);
        StringBuilder stringBuilder = new StringBuilder();
        shareSingeDays.forEach(s->stringBuilder.append(shareSingeDayToString(s)));
        try {
            bufferedWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //shareSingeDayDao.insert(shareSingeDays);
    }

    private String shareSingeDayToString(ShareSingeDay shareSingeDay) {
        return ("insert into share_singe_day(code,name,date,open,high,close," +
                "low,volume,priceChange,p1Change,ma5,ma10,ma20,v1Ma5,v1Ma10,v1Ma20,turnover) " +
                "values(") +
                shareSingeDay.getCode() + "," +
                shareSingeDay.getName() + "," +
                shareSingeDay.getDate() + "," +
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
