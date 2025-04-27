package ar.com.old.ms_products.clients;

import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.exceptions.ConnectionFeignException;
import feign.FeignException;
import org.springframework.stereotype.Service;

@Service
public class UserClientService {

    private final  UserClient userClient;

    public UserClientService(UserClient userClient) {
        this.userClient = userClient;
    }

    public UserDTO getUser() {
        try {
            return userClient.getCurrentUser();
        } catch (FeignException e) {
            throw new ConnectionFeignException("Can not connect to another service, verify you current token");
        }
    }
}
