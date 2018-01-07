package com.mak.dao;

import com.mak.dto.ShareSingeDayRight;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.SQL;

import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2018/1/6.
 */
@DB(table = "share_singe_day_right")
public interface ShareSingeDayRightDao {

    @SQL("insert into #table(id,code,name,date,open,high,close,low,volume,totalPrice) " +
            "values(:1.id,:1.code,:1.name,:1.date,:1.open,:1.high,:1.close,:1.low,:1.volume,:1.totalPrice)")
    int insert(ShareSingeDayRight shareSingeDayRight);

    @SQL("insert into #table(id,code,name,date,open,high,close,low,volume,totalPrice) " +
            "values(:1.id,:1.code,:1.name,:1.date,:1.open,:1.high,:1.close,:1.low,:1.volume,:1.totalPrice)")
    int insert(List<ShareSingeDayRight> shareSingeDayRights);

    @SQL("select * from #table")
    List<ShareSingeDayRight> findAll();

    @SQL("select * from #table where code=:1")
    List<ShareSingeDayRight> find(String code);

    @SQL("select * from #table where code=:1 and date=:2")
    ShareSingeDayRight find(String code, Date date);

    @SQL("select * from #table where code=:1 and date>=:2 and date<=:3")
    List<ShareSingeDayRight> find(String code, Date start, Date end);

}
