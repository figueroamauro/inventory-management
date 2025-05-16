package ar.com.old.ms_stock.entities;

import ar.com.old.ms_stock.enums.MovementType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movement_type", nullable = false, columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private MovementType type;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "before_stock", nullable = false)
    private Integer beforeStock;

    @Column(name = "after_stock", nullable = false)
    private Integer afterStock;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "stock_entry_id", referencedColumnName = "id")
    private StockEntry stockEntry;

    public StockMovement(Long id, MovementType type, Integer quantity,Integer beforeStock, Integer afterStock, String note, Location location, StockEntry stockEntry) {
        this.id = id;
        this.type = type;
        this.quantity = quantity;
        this.beforeStock = beforeStock;
        this.afterStock = afterStock;
        this.createAt = LocalDateTime.now();
        this.note = note;
        this.location = location;
            this.stockEntry = stockEntry;
    }

    public StockMovement() {
        this.createAt = LocalDateTime.now();
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

    public Integer getBeforeStock() {
        return beforeStock;
    }

    public void setBeforeStock(Integer beforeStock) {
        this.beforeStock = beforeStock;
    }

    public Integer getAfterStock() {
        return afterStock;
    }

    public void setAfterStock(Integer afterStock) {
        this.afterStock = afterStock;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public StockEntry getStockEntry() {
        return stockEntry;
    }

    public void setStockEntry(StockEntry stockEntry) {
        this.stockEntry = stockEntry;
    }
}
