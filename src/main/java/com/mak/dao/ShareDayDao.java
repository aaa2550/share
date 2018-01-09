package com.mak.dao;

import com.mak.dto.ShareDay;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.SQL;

import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2018/1/6.
 */
@DB(table = "share_singe_day")
public interface ShareDayDao {

    @SQL("insert into #table(id,code,name,date,open,high,close,low,volume,priceChange,p1Change,ma5,ma10,ma20,v1Ma5,v1Ma10,v1Ma20,turnover) " +
            "values(:1.id,:1.code,:1.name,:1.date,:1.open,:1.high,:1.close,:1.low,:1.volume,:1.priceChange,:1.p1Change,:1.ma5,:1.ma10,:1.ma20,:1.v1Ma5,:1.v1Ma10,:1.v1Ma20,:1.turnover)")
    int insert(ShareDay shareDay);

    @SQL("insert into #table(id,code,name,date,open,high,close,low,volume,priceChange,p1Change,ma5,ma10,ma20,v1Ma5,v1Ma10,v1Ma20,turnover) " +
            "values(:1.id,:1.code,:1.name,:1.date,:1.open,:1.high,:1.close,:1.low,:1.volume,:1.priceChange,:1.p1Change,:1.ma5,:1.ma10,:1.ma20,:1.v1Ma5,:1.v1Ma10,:1.v1Ma20,:1.turnover)")
    int insert(List<ShareDay> shareDays);

    @SQL("select * from #table")
    List<ShareDay> findAll();

    @SQL("select * from #table where code=:1")
    List<ShareDay> find(String code);

    @SQL("select date from #table where code=:1")
    List<ShareDay> findDate(String code);

    @SQL("select * from #table where code=:1 and date=:2")
    ShareDay find(String code, Date date);

    @SQL("select * from #table where code=:1 and date>=:2 and date<=:3")
    List<ShareDay> find(String code, Date start, Date end);

}
