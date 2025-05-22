package ar.com.old.ms_stock.clients;

import ar.com.old.ms_stock.clients.dto.ProductDTO;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.exceptions.ConnectionFeignException;
import ar.com.old.ms_stock.exceptions.ResourceNotFoundException;
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
            if (e.status() == 404) {
                throw new ResourceNotFoundException("Warehouse not found in remote service");
            }
            throw new ConnectionFeignException("Error connecting to warehouse service: " + e.getMessage());
        }
    }

    public ProductDTO getProduct(Long id) {
        try {
            return warehouseClient.findOneProduct(id);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ResourceNotFoundException("Product with id " + id + " not found in warehouse service");
            }
            throw new ConnectionFeignException("Error connecting to warehouse service: " + e.getMessage());
        }
    }

}
