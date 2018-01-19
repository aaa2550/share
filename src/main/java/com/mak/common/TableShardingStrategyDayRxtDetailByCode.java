package com.mak.common;

import com.mak.util.DateUtil;
import org.jfaster.mango.sharding.TableShardingStrategy;

import java.util.Date;

/**
 * Created by yanghailong on 2018/1/9.
 */
public class TableShardingStrategyDayRxtDetailByCode implements TableShardingStrategy<Date> {
    @Override
    public String getTargetTable(String table, Date shardingParameter) {
        String tableName = table + "_" + DateUtil.format(shardingParameter, "yyyyMMdd");
        if (!JdbcSql.tables.contains(tableName)) {
            synchronized (JdbcSql.tables) {
                if (!JdbcSql.tables.contains(tableName)) {
                    JdbcSql.getSingeJdbcSql().execute(JdbcSql.SHARE_SINGE_DAY_RXT_DETAIL_CREATE_SQL.replace("#table", tableName));
                    JdbcSql.tables.add(tableName);
                }
            }
        }
        return tableName;
    }
}
