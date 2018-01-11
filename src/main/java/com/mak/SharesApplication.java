package com.mak;

import com.mak.common.JdbcSql;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import org.jfaster.mango.datasource.DriverManagerDataSource;
import org.jfaster.mango.operator.Mango;
import org.jfaster.mango.plugin.page.MySQLPageInterceptor;
import org.jfaster.mango.plugin.spring.MangoDaoScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Setter
@Getter
@SpringBootApplication
public class SharesApplication {

	private static final Logger logger = LoggerFactory.getLogger(SharesApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SharesApplication.class, args);
	}

	@Bean
	public JdbcSql getJdbcSql(HikariDataSource hikariDataSource) {
		try {
			Connection connection = hikariDataSource.getConnection();
			return new JdbcSql(connection);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("table create error.", e);
			return null;
		}
	}

	@Bean
	public HikariDataSource getHikariDataSource(
			@Value("${mango.maximumPoolSize}") Integer maximumPoolSize,
			@Value("${mango.MinimumIdle}") Integer minimumIdle,
			@Value("${mango.connectionTestQuery}") String connectionTestQuery,
			@Value("${mango.maxLifetime}") Long maxLifetime,
			@Value("${mango.idleTimeout}") Long idleTimeout,
			@Value("${mango.driverClassName}") String driverClassName,
			@Value("${mango.url}") String url,
			@Value("${mango.username}") String username,
			@Value("${mango.password}") String password) {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setAutoCommit(true);
		hikariConfig.setMaximumPoolSize(maximumPoolSize);
		hikariConfig.setMinimumIdle(minimumIdle);
		hikariConfig.setConnectionTestQuery(connectionTestQuery);
		hikariConfig.setMaxLifetime(maxLifetime);
		hikariConfig.setIdleTimeout(idleTimeout);
		hikariConfig.setDriverClassName(driverClassName);
		hikariConfig.setJdbcUrl(url);
		hikariConfig.setUsername(username);
		hikariConfig.setPassword(password);
		return new HikariDataSource(hikariConfig);
	}

	@Bean
	public Mango getMango(HikariDataSource hikariDataSource) {
		Mango mango = Mango.newInstance(hikariDataSource);
		mango.addInterceptor(new MySQLPageInterceptor());
		return mango;
	}

	@Bean
	public MangoDaoScanner getMangoDaoScanner() {
		MangoDaoScanner mangoDaoScanner = new MangoDaoScanner();
		List<String> list = new ArrayList<>();
		list.add("com.mak.dao");
		mangoDaoScanner.setPackages(list);
		return mangoDaoScanner;
	}
}
