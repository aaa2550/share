package com.mak.service;

import com.mak.SharesApplication;
import com.mak.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

/**
 * Created by yanghailong on 2018/1/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SharesApplication.class)
@WebAppConfiguration
public class SummaryServiceTest {

    @Resource
    private SummaryService summaryService;

    /**
     * 根据昨日涨停或转换率查看今日的股票走势情况
     */
    @Test
    public void testZhangTingGailv() {
        summaryService.zhangTingGailv(DateUtil.parse("2017-10-09"));
    }

    /**
     * 根据昨日涨停或转换率查看今日的股票走势情况
     */
    @Test
    public void testSummaryByYesterdayUpStopOrTurnover() {
        summaryService.summaryByYesterdayUpStopOrTurnover(DateUtil.parse("2017-10-09"));
    }

    @Test
    public void testLiuAnHuaMing() {
        summaryService.liuAnHuaMing("2018-01-15");
    }

    @Test
    public void testDayFaucet() {
        summaryService.dayFaucet();
    }
}
