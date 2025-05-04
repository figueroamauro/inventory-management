package ar.com.old.ms_stock.clients;

import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "ms-products", url = "http://localhost:8001/api/warehouses",configuration = FeignConfig.class)
@Component
public interface WarehouseClient {

    @GetMapping("/current")
    WarehouseDTO findCurrent();
}
