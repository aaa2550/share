package com.mak.dao;

import com.mak.dto.ProxyInfo;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.SQL;

import java.util.List;

/**
 * Created by lenovo on 2018/1/9.
 */
@DB(table = "proxy_info")
public interface ProxyInfoDao {

    @SQL("insert into #table(ip,port) values(:1.ip,:1.port)")
    int insert(ProxyInfo proxyInfo);

    @SQL("insert into #table(ip,port) values(:1.ip,:1.port)")
    int insert(List<ProxyInfo> proxyInfos);

    @SQL("select * from #table")
    List<ProxyInfo> findAll();

    @SQL("delete from #table")
    int deleteAll();
}
