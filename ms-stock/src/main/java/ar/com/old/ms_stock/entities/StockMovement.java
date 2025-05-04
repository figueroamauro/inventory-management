package ar.com.old.ms_stock.entities;

import ar.com.old.ms_stock.enums.MovementType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movement")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movement_type", nullable = false)
    private MovementType type;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "note")
    private String note;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "stock_entry_id")
    private Long stockEntryId;

    public StockMovement(Long id, MovementType type, Integer quantity, String note, Long locationId, Long stockEntryId) {
        this.id = id;
        this.type = type;
        this.quantity = quantity;
        this.timestamp = LocalDateTime.now();
        this.note = note;
        this.locationId = locationId;
        this.stockEntryId = stockEntryId;
    }

    public StockMovement() {
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MovementType getType() {
        return type;
    }

    public void setType(MovementType type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getStockEntryId() {
        return stockEntryId;
    }

    public void setStockEntryId(Long stockEntryId) {
        this.stockEntryId = stockEntryId;
    }
}
