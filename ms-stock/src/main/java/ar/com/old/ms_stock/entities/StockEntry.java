package ar.com.old.ms_stock.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_entry")
public class StockEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    public StockEntry() {
        this.updateAt = LocalDateTime.now();
    }

    public StockEntry(Long id, Integer quantity, Long productId) {
        this.id = id;
        this.quantity = quantity;
        this.productId = productId;
        this.updateAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
