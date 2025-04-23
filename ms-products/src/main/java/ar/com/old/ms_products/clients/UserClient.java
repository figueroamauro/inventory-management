package ar.com.old.ms_products.clients;

import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "ms-users", url = "http://localhost:8000/api/users",configuration = FeignConfig.class)
@Component
public interface UserClient {

    @GetMapping("/current")
    UserDTO getCurrentUser();
}
