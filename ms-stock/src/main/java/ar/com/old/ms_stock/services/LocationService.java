package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.entities.Location;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface LocationService {

    Location create(Location location);

    Page<Location> findAll();

    Location findOne(Long id);

    Location update(Location location);

    void delete(Long id);
}
