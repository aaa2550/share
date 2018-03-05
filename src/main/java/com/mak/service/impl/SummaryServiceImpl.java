package com.mak.service.impl;

import com.mak.dao.ShareDao;
import com.mak.dao.ShareDayDao;
import com.mak.dto.Share;
import com.mak.dto.ShareDay;
import com.mak.service.SummaryService;
import com.mak.util.CandleModelUtil;
import com.mak.util.DateUtil;
import com.mak.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * Created by yanghailong on 2018/1/19.
 */
@Service
public class SummaryServiceImpl implements SummaryService {

    private static final Logger logger = LoggerFactory.getLogger(SummaryServiceImpl.class);

    @Resource
    private ShareDayDao shareDayDao;

    @Resource
    private ShareDao shareDao;

    @Override
    public void zhangTingGailv(Date timeToMarket) {
        Date startTime = DateUtil.parse("2017-01-01");

        List<Share> shares = shareDao.findAll();
        List<Double> doubles = new ArrayList<>();
        shares.forEach(s->{
            List<ShareDay> shareDays = shareDayDao.find(s.getCode()).stream().sorted(Comparator.comparingLong(s2 -> s2.getDate().getTime()))
                    .collect(toList());
            ShareDay temp = null;
            ShareDay colse = null;
            double zhangTingCount = 0;
            double lianxuZhangTingCount = 0;
            for (ShareDay shareDay : shareDays) {
                if (temp != null && isLianxuZhangTing(shareDay, colse)) {
                    lianxuZhangTingCount++;
                }
                if (isZhangTing(shareDay, colse)) {
                    zhangTingCount++;
                    temp = shareDay;
                } else {
                    temp = null;
                }
                colse = shareDay;
            }
            double banfenbi = lianxuZhangTingCount / zhangTingCount;
            if (!(zhangTingCount == 0.0 && lianxuZhangTingCount == 0.0)) {
                doubles.add(banfenbi);
            }
            System.out.println("涨停数量:" + zhangTingCount + "\t连续涨停:" + lianxuZhangTingCount + "\t概率:" + banfenbi);
        });
        Double avarage = doubles.stream().mapToDouble(i->i).average().getAsDouble();
        System.out.println(avarage);
    }

    private boolean isZhangTing(ShareDay shareDay, ShareDay colseShare) {
        double open = shareDay.getOpen();
        if (colseShare != null) {
            open = colseShare.getClose();
        }
        return (shareDay.getClose() - shareDay.getOpen()) > shareDay.getOpen() * 0.05 && shareDay.getClose() > open * 0.96;
    }

    private boolean isLianxuZhangTing(ShareDay shareDay, ShareDay colseShare) {
        double open = shareDay.getOpen();
        if (colseShare != null) {
            open = colseShare.getClose();
        }
        return open < shareDay.getClose();
    }

    @Override
    public void summaryByYesterdayUpStopOrTurnover(Date timeToMarket) {
        Date startTime = DateUtil.parse("2017-01-01");

        Set<String> markets = new HashSet<>();
        if (timeToMarket != null) {
            markets = shareDayDao.find(timeToMarket).parallelStream()
                    .map(ShareDay::getCode)
                    .collect(toSet());
        }

        Date today = new Date();
        Date yesterday = startTime;
        Date currentDay;
        List<ShareDay> currentDays;
        List<ShareDay> yesterdays = shareDayDao.find(yesterday);
        while (yesterday.before(today)) {
            currentDay = DateUtil.nextDay(yesterday);
            currentDays = shareDayDao.find(currentDay);
            yesterday = currentDay;
            if (yesterdays.isEmpty() || currentDays.isEmpty()) {
                if (!currentDays.isEmpty()) {
                    yesterdays = currentDays;
                }
                continue;
            }
            print(currentDay, markets, yesterdays, currentDays);
            yesterdays = currentDays;
        }

    }

    @Override
    public void dayFaucet() {
        Date currentDay = DateUtil.parse("2017-01-01");
        Date today = new Date();
        List<ShareDay> currentDays;

        while (currentDay.before(today)) {
            currentDays = shareDayDao.find(currentDay);
            print(currentDay, currentDays);
            currentDay = DateUtil.nextDay(currentDay);
        }
    }

    @Override
    public void liuAnHuaMing(String time) {
        Date startDay = DateUtil.parse(time);
        List<Share> shares = shareDao.findAll();
        int[] i = new int[1];
        shares.parallelStream()
                .forEach(s->{
                    if (singeLiuAnHuaMing(shareDayDao.startFind(s.getCode(), startDay))) {
                        i[0]++;
                    }
                });
        System.out.println("共计" + i[0] + "条数据");
    }

    private void print(Date currentDay, Set<String> markets, List<ShareDay> yesterdays, List<ShareDay> currentDays) {
        Set<ShareDay> upStop = yesterdays.parallelStream()
                .filter(d->d.getP1Change()>=9.97)
                .filter(d->markets.isEmpty()||markets.contains(d.getCode()))
                .sorted(Comparator.comparingDouble(ShareDay::getP1Change))
                .collect(toSet());
        Set<ShareDay> up = currentDays.parallelStream()
                .filter(d->d.getP1Change()>=0.0)
                .filter(d->!d.getOpen().equals(d.getClose()))
                .filter(upStop::contains)
                .collect(toSet());
        Set<ShareDay> turnoverTop10 = yesterdays.parallelStream()
                .filter(s->s.getTurnover()!=null)
                .sorted((s1, s2)->s2.getTurnover() - s1.getTurnover() == 0.0 ? 0 : s2.getTurnover() - s1.getTurnover() > 0 ? 1 : -1)
                .filter(d->markets.isEmpty()||markets.contains(d.getCode()))
                .limit(10)
                .collect(toSet());
        Set<ShareDay> turnover = currentDays.parallelStream()
                .filter(d->d.getP1Change()>=0.0)
                .filter(turnoverTop10::contains)
                .collect(toSet());

        try {
            logger.info(String.join("\t", new String[]{
                    DateUtil.format(currentDay),
                    upStop.size() + "", up.size() + "",
                    turnoverTop10.size() + "",
                    turnover.size() + "",
                    NumberUtil.percent(upStop.size(), up.size()) + "",
                    NumberUtil.percent(turnoverTop10.size(), turnover.size()) + ""}));
        } catch (Throwable e) {
            System.out.println(upStop.size() + "-" + up.size() + "-" + turnoverTop10.size() + "-" + turnover.size());
        }

    }

    private void print(Date currentDay, List<ShareDay> yesterdays) {
        List<String> upStop = yesterdays.parallelStream()
                .filter(d->d.getP1Change()>=9.97)
                .sorted(Comparator.comparingDouble(ShareDay::getP1Change).reversed())
                .map(s->s.getCode() + ":" + s.getP1Change())
                .collect(toList());
        logger.info(DateUtil.format(currentDay) + "-" + upStop.toString());

    }

    private boolean singeLiuAnHuaMing(List<ShareDay> shareDays) {
        boolean success = false;
        ShareDay[] ds = new ShareDay[7];
        for (int i = 0; i < shareDays.size(); i++) {
            ShareDay shareDay = shareDays.get(i);
            singeLiuAnHuaMingChange(ds);
            ds[6] = shareDay;
            if (i > 6 && isLiuAnHuaMing(ds)) {
                System.out.println(shareDay.getCode() + ":" + shareDay.getName() + "---" + DateUtil.format(shareDay.getDate()));
                success = true;
            }
        }
        return success;
    }

    private void singeLiuAnHuaMingChange(ShareDay[] ds) {
        for (int i = 1; i < ds.length; i++) {
            ds[i-1] = ds[i];
        }
    }

    private boolean isLiuAnHuaMing(ShareDay[] ds) {
        int liuAnHuaMing = 0;
        if (CandleModelUtil.isMinYang(ds[0])) {
            liuAnHuaMing++;
        }
        if (CandleModelUtil.isMinYang(ds[1])) {
            liuAnHuaMing++;
        }
        if (CandleModelUtil.isMinYang(ds[2])) {
            liuAnHuaMing++;
        }
        if (CandleModelUtil.isMinYang(ds[3])) {
            liuAnHuaMing++;
        }
        if (CandleModelUtil.isMinYang(ds[4])) {
            liuAnHuaMing++;
        }
        boolean you = false;
        if (CandleModelUtil.isMinYin(ds[5]) || CandleModelUtil.isMinYin(ds[4])) {
            you = true;
        }
        boolean cun = false;
        if (CandleModelUtil.xiaYingXian(ds[6])) {
            cun = true;
        }
        return liuAnHuaMing >= 4 && you && cun;
    }

}
