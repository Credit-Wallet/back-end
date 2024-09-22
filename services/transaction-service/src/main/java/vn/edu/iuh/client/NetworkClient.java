package vn.edu.iuh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.edu.iuh.response.ApiResponse;

@FeignClient(name = "NETWORK-SERVICE")
public interface NetworkClient {
    @GetMapping("/networks/{id}")
    ApiResponse<?> getNetworkById(@PathVariable("id") Long id);
}
