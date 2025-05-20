package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.dto.LocationDTO;
import ar.com.old.ms_stock.entities.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface LocationService {

    Location create(LocationDTO location);

    Page<Location> findAll(Pageable pageable);

    Location findOne(Long id);

    Location update(LocationDTO location);

    void delete(Long id);
}
