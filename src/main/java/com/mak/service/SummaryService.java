package com.mak.service;

import java.util.Date;

/**
 * Created by yanghailong on 2018/1/19.
 */
public interface SummaryService {

    /**
     * 根据昨日涨停或转换率查看今日的股票走势情况
     * timeToMarket 上市时间在timeToMarket以后的不计算
     */
    void summaryByYesterdayUpStopOrTurnover(Date timeToMarket);

}
