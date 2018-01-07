package com.mak;

import lombok.Getter;
import lombok.Setter;
import org.jfaster.mango.datasource.DriverManagerDataSource;
import org.jfaster.mango.operator.Mango;
import org.jfaster.mango.plugin.page.MySQLPageInterceptor;
import org.jfaster.mango.plugin.spring.MangoDaoScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Setter
@Getter
@SpringBootApplication
public class SharesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SharesApplication.class, args);
	}

	@Bean
	public Mango getMango(
			@Value("${mango.driverClassName}") String driverClassName,
			@Value("${mango.url}") String url,
			@Value("${mango.username}") String username,
			@Value("${mango.password}") String password) {
		Mango mango = Mango.newInstance(new DriverManagerDataSource(driverClassName, url, username, password));
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
