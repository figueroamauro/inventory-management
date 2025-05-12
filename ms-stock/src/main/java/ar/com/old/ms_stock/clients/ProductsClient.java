package ar.com.old.ms_stock.clients;

import ar.com.old.ms_stock.clients.dto.ProductDTO;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-products", url = "http://localhost:8001/api",configuration = FeignConfig.class)
@Component
public interface ProductsClient {

    @GetMapping("/warehouses/current")
    WarehouseDTO findCurrentWarehouse();

    @GetMapping("/products/{id}")
    ProductDTO findOneProduct(@PathVariable Long id);
}
