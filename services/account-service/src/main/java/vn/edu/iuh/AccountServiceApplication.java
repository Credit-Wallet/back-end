package vn.edu.iuh;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import vn.edu.iuh.component.FirebaseKey;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@SpringBootApplication
@EnableFeignClients
@PropertySource("classpath:firebase.properties")
public class AccountServiceApplication {
    @Autowired
    private FirebaseKey firebaseKey;

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
                new ByteArrayInputStream(firebaseKey.toString().getBytes())
        );

        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions);
        return FirebaseMessaging.getInstance(app);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
	}
}
