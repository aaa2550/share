package com.mak.service;

import java.io.BufferedWriter;

/**
 * Created by lenovo on 2018/1/6.
 */
public interface SynchronizedService {

    void synchronizedShares();

    void synchronizedHistory();

    void synchronizedHistory(String code, String name, BufferedWriter bufferedWriter);
}
