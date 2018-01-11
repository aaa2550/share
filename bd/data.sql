DROP TABLE IF EXISTS `share_list`;
CREATE TABLE `share_list` (
  `id` int(11) primary key not null auto_increment,
  `code` varchar(10) NOT NULL COMMENT '股票代码',
  `name` varchar(20) NOT NULL COMMENT '股票中文名',
  `industry` varchar(20) NOT NULL COMMENT '所属行业',
  `area` varchar(20) NOT NULL COMMENT '地区',
  `pe` double(10,2) NOT NULL COMMENT '市盈率',
  `outstanding` double(20,2) NOT NULL COMMENT '流通股本',
  `totals` double(20,2) NOT NULL COMMENT '总股本',
  `totalAssets` double(20,2) NOT NULL COMMENT '总资产',
  `liquidAssets` double(20,2) NOT NULL COMMENT '流动资产',
  `fixedAssets` double(20,2) NOT NULL COMMENT '固定资产',
  `esp` double(11,2) NOT NULL COMMENT '每股收益',
  `bvps` double(11,2) NOT NULL COMMENT '每股净资',
  `pb` double(11,2) NOT NULL COMMENT '市净率',
  `undp` double(11,2) NOT NULL COMMENT '未分利润',
  `holders` double(11,2) NOT NULL COMMENT '股东人数',
  `timeToMarket` timestamp NOT NULL COMMENT '上市时间',
  KEY `index_code` USING BTREE (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `share_singe_day`;
CREATE TABLE `share_singe_day` (
  `id` int(11) primary key not null auto_increment,
  `date` TIMESTAMP NOT NULL COMMENT '股票日期',
  `code` varchar(20) NOT NULL COMMENT '股票代码',
  `name` varchar(20) NOT NULL COMMENT '股票中文名',
  `open` double(11,2) NOT NULL COMMENT '开盘价',
  `high` double(11,2) NOT NULL COMMENT '最高价',
  `close` double(11,2) NOT NULL COMMENT '收盘价',
  `low` double(11,2) NOT NULL COMMENT '最低价',
  `volume` double(11,2) NOT NULL COMMENT '成交量',
  `priceChange` double(11,2) NOT NULL COMMENT '价格变动',
  `p1Change` double(11,2) NOT NULL COMMENT '涨跌幅',
  `ma5` double(11,2) NOT NULL COMMENT '5日均价',
  `ma10` double(11,2) NOT NULL COMMENT '10日均价',
  `ma20` double(11,2) NOT NULL COMMENT '20日均价',
  `v1Ma5` double(11,2) NOT NULL COMMENT '5日均量',
  `v1Ma10` double(11,2) NOT NULL COMMENT '10日均量',
  `v1Ma20` double(11,2) NOT NULL COMMENT '20日均量',
  `turnover` double(11,2) DEFAULT NULL COMMENT '换手率',
  KEY `index_code` USING BTREE (`code`),
  KEY `index_code_date` USING BTREE (`code`,`date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `share_singe_day_right`;
CREATE TABLE `share_singe_day_right` (
  `id` int(11) primary key not null auto_increment,
  `date` TIMESTAMP NOT NULL COMMENT '股票日期',
  `code` varchar(20) NOT NULL COMMENT '股票代码',
  `name` varchar(20) NOT NULL COMMENT '股票中文名',
  `open` double(11,2) NOT NULL COMMENT '开盘价',
  `high` double(11,2) NOT NULL COMMENT '最高价',
  `close` double(11,2) NOT NULL COMMENT '收盘价',
  `low` double(11,2) NOT NULL COMMENT '最低价',
  `volume` double(11,2) NOT NULL COMMENT '成交量',
  `totalPrice` double(11,2) NOT NULL COMMENT '成交金额',
  KEY `index_code` USING BTREE (`code`),
  KEY `index_code_date` USING BTREE (`code`,`date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `share_singe_day_detail`;
CREATE TABLE `share_singe_day_detail` (
  `id` int(11) primary key not null auto_increment,
  `date` TIMESTAMP NOT NULL COMMENT '股票日期',
  `code` varchar(20) NOT NULL COMMENT '股票代码',
  `name` varchar(20) NOT NULL COMMENT '股票中文名',
  `tradeTime` TIMESTAMP NOT NULL COMMENT '成交时间',
  `price` double(11,2) NOT NULL COMMENT '成交价',
  `priceChange` double(11,2) NOT NULL COMMENT '价格变动',
  `num` int(11) NOT NULL COMMENT '成交量(手)',
  `money` double(11,2) NOT NULL COMMENT '成交额(元)',
  `nature` varchar(20) NOT NULL COMMENT '性质',
  KEY `index_code` USING BTREE (`code`),
  KEY `index_code_date` USING BTREE (`code`,`date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `proxy_info`;
CREATE TABLE `proxy_info` (
  `id` int(11) primary key not null auto_increment,
  `ip` varchar(200) NOT NULL COMMENT 'IP',
  `port` varchar(200) NOT NULL COMMENT '端口'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
