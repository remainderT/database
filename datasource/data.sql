DROP DATABASE IF EXISTS lr_database;
CREATE DATABASE lr_database  DEFAULT CHARACTER SET utf8mb4;

USE `lr_database`;

DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱',
    `is_vip`        tinyint(1) DEFAULT NULL COMMENT 'VIP标识 0：普通用户 1：VIP用户',
    `deletion_time` bigint(20) DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1716344307570487299 DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `t_group`;
CREATE TABLE `t_group`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3) DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY           `idx_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `t_link`;
CREATE TABLE `t_link`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11) DEFAULT '0' COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT 'default' COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1) DEFAULT 0 COMMENT '启用标识 0：启用 1：未启用',
    `created_type`    tinyint(1) DEFAULT NULL COMMENT '创建类型 0：接口创建 1：控制台创建',
    `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `is_pic`          tinyint(1) DEFAULT 0 COMMENT '是否图片 0：否 1：是',
    `total_pv`        int(11) DEFAULT 0 COMMENT '历史PV',
    `total_uv`        int(11) DEFAULT 0 COMMENT '历史UV',
    `total_uip`       int(11) DEFAULT 0 COMMENT '历史UIP',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_time`        bigint(20) DEFAULT '0' COMMENT '删除时间戳',
    `del_flag`        tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full-short-url` (`full_short_url`,`del_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `t_link_access_logs`;
CREATE TABLE `t_link_access_logs`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `user`           varchar(64)  DEFAULT NULL COMMENT '用户信息',
    `ip`             varchar(64)  DEFAULT NULL COMMENT 'IP',
    `browser`        varchar(64)  DEFAULT NULL COMMENT '浏览器',
    `os`             varchar(64)  DEFAULT NULL COMMENT '操作系统',
    `network`        varchar(64)  DEFAULT NULL COMMENT '访问网络',
    `device`         varchar(64)  DEFAULT NULL COMMENT '访问设备',
    `locale`         varchar(256) DEFAULT NULL COMMENT '地区',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY              `idx_full_short_url` (`full_short_url`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `t_link_access_status`;
CREATE TABLE `t_link_access_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `pv`             int(11) DEFAULT NULL COMMENT '访问量',
    `uv`             int(11) DEFAULT NULL COMMENT '独立访客数',
    `uip`            int(11) DEFAULT NULL COMMENT '独立IP数',
    `hour`           int(3) DEFAULT NULL COMMENT '小时',
    `weekday`        int(3) DEFAULT NULL COMMENT '星期',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_access_stats` (`full_short_url`,`date`,`hour`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `t_link_browser_stats`;
CREATE TABLE `t_link_browser_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `cnt`            int(11) DEFAULT NULL COMMENT '访问量',
    `browser`        varchar(64)  DEFAULT NULL COMMENT '浏览器',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_browser_stats` (`full_short_url`,`date`,`browser`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `t_link_device_stats`;
CREATE TABLE `t_link_device_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `cnt`            int(11) DEFAULT NULL COMMENT '访问量',
    `device`         varchar(64)  DEFAULT NULL COMMENT '访问设备',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_browser_stats` (`full_short_url`,`date`,`device`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `t_link_locale_stats`;
CREATE TABLE `t_link_locale_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `cnt`            int(11) DEFAULT NULL COMMENT '访问量',
    `province`       varchar(64)  DEFAULT NULL COMMENT '省份名称',
    `city`           varchar(64)  DEFAULT NULL COMMENT '市名称',
    `adcode`         varchar(64)  DEFAULT NULL COMMENT '城市编码',
    `country`        varchar(64)  DEFAULT NULL COMMENT '国家标识',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_locale_stats` (`full_short_url`,`date`,`adcode`,`province`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS  `t_link_network_stats`;
CREATE TABLE `t_link_network_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `cnt`            int(11) DEFAULT NULL COMMENT '访问量',
    `network`        varchar(64)  DEFAULT NULL COMMENT '访问网络',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_browser_stats` (`full_short_url`,`date`,`network`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS  `t_link_os_stats`;
CREATE TABLE `t_link_os_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `cnt`            int(11) DEFAULT NULL COMMENT '访问量',
    `os`             varchar(64)  DEFAULT NULL COMMENT '操作系统',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_os_stats` (`full_short_url`,`date`,`os`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `t_link_stats_today`;
CREATE TABLE `t_link_stats_today`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '短链接',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `today_pv`       int(11) DEFAULT '0' COMMENT '今日PV',
    `today_uv`       int(11) DEFAULT '0' COMMENT '今日UV',
    `today_uip`      int(11) DEFAULT '0' COMMENT '今日IP数',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_today_stats` (`full_short_url`,`date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `t_link_uv_stats`;
CREATE TABLE `t_link_uv_stats` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `uuid` varchar(64) NOT NULL COMMENT 'uv唯一标识',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_uv_stats` (`full_short_url`,`uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `t_link_uip_stats`;
CREATE TABLE `t_link_uip_stats` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
   `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
   `ip` varchar(64) NOT NULL COMMENT 'ip地址',
   `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
   `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
   `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
   PRIMARY KEY (`id`),
   UNIQUE KEY `idx_unique_uip_stats` (`full_short_url`,`ip`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
     `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
     `goods_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
     `amount` decimal(10, 2) DEFAULT NULL COMMENT '价格',
     `pay_no` varchar(255)  DEFAULT NULL COMMENT '支付编号',
     `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
     `status` varchar(255)  DEFAULT NULL COMMENT '订单状态',
     `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
     `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
     `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;