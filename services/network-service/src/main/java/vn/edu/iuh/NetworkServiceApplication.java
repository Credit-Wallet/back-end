package vn.edu.iuh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NetworkServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetworkServiceApplication.class, args);
	}

}
