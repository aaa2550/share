package com.mak;

import com.mak.common.JdbcSql;
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
	public JdbcSql getJdbcSql(DriverManagerDataSource driverManagerDataSource) {
		try {
			Connection connection = driverManagerDataSource.getConnection();
			return new JdbcSql(connection);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("table create error.", e);
			return null;
		}
	}

	@Bean
	public DriverManagerDataSource getDriverManagerDataSource(
			@Value("${mango.driverClassName}") String driverClassName,
			@Value("${mango.url}") String url,
			@Value("${mango.username}") String username,
			@Value("${mango.password}") String password) {
		return new DriverManagerDataSource(driverClassName, url, username, password);
	}

	@Bean
	public Mango getMango(DriverManagerDataSource driverManagerDataSource) {
		Mango mango = Mango.newInstance(driverManagerDataSource);
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
