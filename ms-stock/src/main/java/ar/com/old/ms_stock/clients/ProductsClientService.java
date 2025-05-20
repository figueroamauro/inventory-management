package ar.com.old.ms_stock.clients;

import ar.com.old.ms_stock.clients.dto.ProductDTO;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.exceptions.ConnectionFeignException;
import feign.FeignException;
import org.springframework.stereotype.Service;

@Service
public class ProductsClientService {

    private final ProductsClient warehouseClient;

    public ProductsClientService(ProductsClient warehouseClient) {
        this.warehouseClient = warehouseClient;
    }

    public WarehouseDTO getWarehouse() {
        try {
            return warehouseClient.findCurrentWarehouse();
        } catch (FeignException e) {
            throw new ConnectionFeignException("Can not connect to another service, verify you current token");
        }
    }

    public ProductDTO getProduct(Long id) {
        try {
            return warehouseClient.findOneProduct(id);
        } catch (FeignException e) {
            throw new ConnectionFeignException("Can not connect to another service, verify you current token");
        }
    }
}
