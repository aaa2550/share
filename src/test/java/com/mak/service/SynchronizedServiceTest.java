package com.mak.service;

import com.mak.SharesApplication;
import com.mak.dao.ShareDayDetailDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

/**
 * Created by lenovo on 2018/1/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SharesApplication.class)
@WebAppConfiguration
public class SynchronizedServiceTest {

    @Resource
    private SynchronizedService synchronizedService;

    @Resource
    private ShareDayDetailDao shareSingeDayDetailDao;

    @Test
    public void testSynchronizedProxys() {
        synchronizedService.synchronizedProxys();
    }

    @Test
    public void testSynchronizedShares() {
        synchronizedService.synchronizedShares();
    }

    @Test
    public void testSynchronizedHistory() {
        synchronizedService.synchronizedHistory();
    }

    @Test
    public void synchronizedDayDetail() {
        testSynchronizedProxys();
        synchronizedService.synchronizedDayDetail();
    }

}
