package com.mak.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yanghailong on 2018/1/9.
 */
public class JdbcSql {

    private static final Logger logger = LoggerFactory.getLogger(JdbcSql.class);

    public final static String SHARE_SINGE_DAY_DETAIL_CREATE_SQL = "CREATE TABLE `#table` (\n" +
            "  `id` int(11) primary key not null auto_increment,\n" +
            "  `date` TIMESTAMP NOT NULL COMMENT '股票日期',\n" +
            "  `code` varchar(20) NOT NULL COMMENT '股票代码',\n" +
            "  `name` varchar(20) NOT NULL COMMENT '股票中文名',\n" +
            "  `tradeTime` TIMESTAMP NOT NULL COMMENT '成交时间',\n" +
            "  `price` double(11,2) NOT NULL COMMENT '成交价',\n" +
            "  `priceChange` double(11,2) NOT NULL COMMENT '价格变动',\n" +
            "  `num` int(11) NOT NULL COMMENT '成交量(手)',\n" +
            "  `money` double(11,2) NOT NULL COMMENT '成交额(元)',\n" +
            "  `nature` varchar(20) NOT NULL COMMENT '性质',\n" +
            "  KEY `index_code` USING BTREE (`code`),\n" +
            "  KEY `index_code_date` USING BTREE (`code`,`date`)\n" +
            ") ENGINE=MyISAM DEFAULT CHARSET=utf8;";

    private Connection connection;
    final static ConcurrentHashMap<String, String> tables = new ConcurrentHashMap<>();

    public static JdbcSql singeJdbcSql;

    public JdbcSql(Connection connection) {
        this.connection = connection;
        singeJdbcSql = this;
    }

    public static JdbcSql getSingeJdbcSql() {
        return singeJdbcSql;
    }

    public boolean execute(String sql) {
        Statement statement;
        try {
            statement = connection.createStatement();
            return statement.executeLargeUpdate(sql) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("JDBC sql execute error.", e);
            return false;
        }
    }

}
