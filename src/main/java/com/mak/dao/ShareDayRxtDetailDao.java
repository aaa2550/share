package com.mak.dao;

import com.mak.common.TableShardingStrategyDayDetailByCode;
import com.mak.common.TableShardingStrategyDayRxtDetailByCode;
import com.mak.dto.ShareDayDetail;
import com.mak.dto.ShareDayRxtDetail;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.SQL;
import org.jfaster.mango.annotation.Sharding;
import org.jfaster.mango.annotation.ShardingBy;

import java.util.Date;
import java.util.List;

/**
 * Created by yanghailong on 2018/1/9.
 */
@DB(table = "share_rxt_singe_day_detail")
@Sharding(tableShardingStrategy = TableShardingStrategyDayRxtDetailByCode.class)
public interface ShareDayRxtDetailDao {

    @SQL("insert into #table(date,code,name,tradeTime,price,p1Change,priceChange,num,money) " +
            "values(:1.date,:1.code,:1.name,:1.tradeTime,:1.price,:1.p1Change,:1.priceChange,:1.num,:1.money)")
    int insert(@ShardingBy("date") ShareDayRxtDetail shareDayRxtDetail);

    @SQL("insert into #table(date,code,name,tradeTime,price,p1Change,priceChange,num,money) " +
            "values(:1.date,:1.code,:1.name,:1.tradeTime,:1.price,:1.p1Change,:1.priceChange,:1.num,:1.money)")
    int insert(@ShardingBy("date") List<ShareDayRxtDetail> shareDayRxtDetails);

    @SQL("select tradeTime,price,p1Change,priceChange,num,money from #table where code=:1 and date=:2")
    List<ShareDayRxtDetail> find(String code, @ShardingBy Date date);

}
