package ar.com.old.ms_stock.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @OneToMany(mappedBy = "location",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<LocationStock> locationStockList;

    @Column(nullable = false)
    private boolean active = true;

    public Location(Long id, String name, Long warehouseId) {
        this.locationStockList = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.warehouseId = warehouseId;
    }

    public Location() {
    }

    public void addStock(LocationStock stock) {
        locationStockList.add(stock);
    }

    public void removeStock(LocationStock stock) {
        locationStockList.remove(stock);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public List<LocationStock> getLocationStockList() {
        return locationStockList;
    }

    public void setLocationStockList(List<LocationStock> locationStockList) {
        this.locationStockList = locationStockList;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
