package com.example.msloan.client;

import com.example.msloan.client.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-user",
        url = "${client.ms-user.url}",
        configuration = UserErrorDecoder.class
)
public interface UserClient {

    @GetMapping("/v1/users/{id}")
    UserResponseDto getUserById(@PathVariable("id") Integer id);
}
