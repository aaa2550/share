package com.mak.service;

import java.io.BufferedWriter;
import java.util.Date;

/**
 * Created by lenovo on 2018/1/6.
 */
public interface SynchronizedService {

    void synchronizedProxys();

    void synchronizedShares();

    void synchronizedHistory();

    void synchronizedDayDetail();

    void synchronizedHistoryRight();

    void synchronizedHistoryRight(String code, String name, BufferedWriter bufferedWriter);

    void synchronizedDayDetail(String code, String name, Date date, BufferedWriter bufferedWriter);

    void synchronizedHistory(String code, String name, BufferedWriter bufferedWriter);

}
