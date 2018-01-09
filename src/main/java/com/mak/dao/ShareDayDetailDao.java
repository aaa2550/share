package com.mak.dao;

import com.mak.common.TableShardingStrategyDayDetailByCode;
import com.mak.dto.ShareDayDetail;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.SQL;
import org.jfaster.mango.annotation.Sharding;
import org.jfaster.mango.annotation.ShardingBy;

import java.util.Date;
import java.util.List;

/**
 * Created by yanghailong on 2018/1/9.
 */
@DB(table = "share_singe_day_detail")
@Sharding(tableShardingStrategy = TableShardingStrategyDayDetailByCode.class)
public interface ShareDayDetailDao {

    @SQL("insert into #table(date,code,name,tradeTime,price,priceChange,num,money,nature) " +
            "values(:1.date,:1.code,:1.name,:1.tradeTime,:1.price,:1.priceChange,:1.num,:1.money,:1.nature)")
    int insert(@ShardingBy("code") ShareDayDetail shareDayDetail);

    @SQL("insert into #table(date,code,name,tradeTime,price,priceChange,num,money,nature) " +
            "values(:1.date,:1.code,:1.name,:1.tradeTime,:1.price,:1.priceChange,:1.num,:1.money,:1.nature)")
    int insert(@ShardingBy("code")List<ShareDayDetail> shareDayDetails);

    @SQL("select tradeTime,price,priceChange,num,money,nature from #table where code=:1 and date=:2")
    List<ShareDayDetail> find(@ShardingBy String code, Date date);

}
