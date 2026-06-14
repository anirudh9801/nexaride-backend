package com.nexaride.booking_service;

import com.nexaride.booking_service.security.SSLUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BookingServiceApplication {

	public static void main(String[] args) throws Exception {
        SSLUtil.disableSSL();
		SpringApplication.run(BookingServiceApplication.class, args);
	}

}
