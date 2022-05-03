/**
 * ==========================================================================
 * Filename: DataSourceConfiguration.java
 * =============================================================================
 * <p>
 * =============================================================================
 * <p>
 * NOTICE
 * Confidential, unpublished property of United Parcel Service.
 * Use and distribution limited solely to authorized personnel.
 * <p>
 * The use, disclosure, reproduction, modification, transfer, or
 * transmittal of this work for any purpose in any form or by
 * any means without the written permission of United Parcel
 * Service is strictly prohibited.
 * <p>
 * Copyright (c) 2016-2017, United Parcel Service of America, Inc.
 * All Rights Reserved.
 * <p>
 * =============================================================================
 * Author/Architect - OMS@ups.com
 * Date Create – 09-10-2017
 * =============================================================================
 */

package com.ups.ops.oms.batch.mdc.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@EnableTransactionManagement
@Configuration
public class DataSourceConfiguration {

    // HikariCP

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.pool-name}")
    private String poolName;

    @Value("${spring.datasource.connection-timeout}")
    private int connectionTimeout;

    @Value("${spring.datasource.max-lifetime}")
    private int maxLifetime;

    @Value("${spring.datasource.maximum-pool-size}")
    private int maximumPoolSize;

    @Value("${spring.datasource.minimum-idle}")
    private int minimumIdle;

    @Value("${spring.datasource.idle-timeout}")
    private int idleTimeout;

    @Value("${spring.datasource.validation-query}")
    private String validationQuery;

    @Value("${spring.datasource.pool-prepared-statements}")
    private String cachePrepStmts;

    @Value("${spring.datasource.pool-prepared-statements-cache-size}")
    private String prepStmtCacheSize;

    @Value("${spring.datasource.pool-prepared-statements-cache-sql-limit}")
    private String prepStmtCacheSqlLimit;

    @Value("${spring.datasource.user-server-prepared-statements}")
    private String useServerPrepStmts;

    @Bean
    public HikariDataSource primaryDataSource() {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(url); 
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        hikariConfig.setMinimumIdle(minimumIdle);
        hikariConfig.setConnectionTimeout(connectionTimeout);
        hikariConfig.setIdleTimeout(idleTimeout);
        hikariConfig.setConnectionTestQuery(validationQuery);
        hikariConfig.setPoolName("springHikariCP");

        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", cachePrepStmts);
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", prepStmtCacheSize);
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);
        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", useServerPrepStmts);

        return new HikariDataSource(hikariConfig);
    }
    
}
