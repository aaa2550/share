package com.mak.service.impl;

import com.mak.dao.ShareDao;
import com.mak.dao.ShareDayDao;
import com.mak.dto.Share;
import com.mak.dto.ShareDay;
import com.mak.service.SummaryService;
import com.mak.util.DateUtil;
import com.mak.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public void summaryByYesterdayUpStopOrTurnover(Date timeToMarket) {
        Date startTime = DateUtil.parse("2017-01-01");

        Set<String> markets = new HashSet<>();
        if (timeToMarket != null) {
            markets = shareDayDao.find(timeToMarket).parallelStream()
                    .map(ShareDay::getCode)
                    .collect(Collectors.toSet());
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

    private void print(Date currentDay, Set<String> markets, List<ShareDay> yesterdays, List<ShareDay> currentDays) {
        Set<ShareDay> upStop = yesterdays.parallelStream()
                .filter(d->d.getP1Change()>=9.97)
                .filter(d->markets.isEmpty()||markets.contains(d.getCode()))
                .sorted(Comparator.comparingDouble(ShareDay::getP1Change))
                .collect(Collectors.toSet());
        Set<ShareDay> up = currentDays.parallelStream()
                .filter(d->d.getP1Change()>=0.0)
                .filter(d->!d.getOpen().equals(d.getClose()))
                .filter(upStop::contains)
                .collect(Collectors.toSet());
        Set<ShareDay> turnoverTop10 = yesterdays.parallelStream()
                        .filter(s->s.getTurnover()!=null)
                        .sorted((s1, s2)->s2.getTurnover() - s1.getTurnover() == 0.0 ? 0 : s2.getTurnover() - s1.getTurnover() > 0 ? 1 : -1)
                        .filter(d->markets.isEmpty()||markets.contains(d.getCode()))
                        .limit(10)
                        .collect(Collectors.toSet());
        Set<ShareDay> turnover = currentDays.parallelStream()
                .filter(d->d.getP1Change()>=0.0)
                .filter(turnoverTop10::contains)
                .collect(Collectors.toSet());

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
}
