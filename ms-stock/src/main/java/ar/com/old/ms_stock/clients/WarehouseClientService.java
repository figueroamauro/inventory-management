package ar.com.old.ms_stock.clients;

import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.exceptions.ConnectionFeignException;
import feign.FeignException;
import org.springframework.stereotype.Service;

@Service
public class WarehouseClientService {

    private final WarehouseClient warehouseClient;

    public WarehouseClientService(WarehouseClient warehouseClient) {
        this.warehouseClient = warehouseClient;
    }

    WarehouseDTO getWarehouse() {
        try {
            return warehouseClient.findCurrent();
        } catch (FeignException e) {
            throw new ConnectionFeignException("Can not connect to another service, verify you current token");
        }
    }
}
