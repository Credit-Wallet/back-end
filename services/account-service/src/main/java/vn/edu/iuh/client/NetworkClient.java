package vn.edu.iuh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.NetworkResponse;

@FeignClient(name = "NETWORK-SERVICE")
public interface NetworkClient {
    //get network by id
    @GetMapping("/networks/{id}")
    ApiResponse<NetworkResponse> getNetwork(@PathVariable("id") Long id);
}
