package com.mak.common;

import org.jfaster.mango.sharding.TableShardingStrategy;

/**
 * Created by yanghailong on 2018/1/9.
 */
public class TableShardingStrategyDayDetailByCode implements TableShardingStrategy<String> {
    @Override
    public String getTargetTable(String table, String shardingParameter) {
        String tableName = table + "_" + shardingParameter;
        if (!JdbcSql.tables.contains(tableName)) {
            JdbcSql.getSingeJdbcSql().execute(JdbcSql.SHARE_SINGE_DAY_DETAIL_CREATE_SQL.replace("#table", tableName));
            JdbcSql.tables.add(tableName);
        }
        return tableName;
    }
}
