package vn.edu.iuh.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import vn.edu.iuh.client.AccountClient;

@Configuration
public class WebClientConfig {
    @Bean
    WebClient webClient(){
        return WebClient.builder().baseUrl("http://localhost:8001/auth").build();
    }
    @Bean
    AccountClient accountClient(WebClient webClient){
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)).build();
        return httpServiceProxyFactory.createClient(AccountClient.class);
    }
}
