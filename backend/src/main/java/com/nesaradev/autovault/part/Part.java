package com.nesaradev.autovault.part;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "parts")
public class Part {
    @Id
    private String id;
    @Indexed(unique = true)
    private String partNumber;
    private String name;
    private String description;
    private String category;
    private String brand;
    private PartCondition condition;
    private int quantity;
    private int minimumStockLevel;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private String storageLocation;
    private boolean active;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    @Version
    private Long version;

    public Part() {
        active = true;
    }

    public String getId() {
        return id;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public PartCondition getCondition() {
        return condition;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCondition(PartCondition condition) {
        this.condition = condition;
    }

    public void setMinimumStockLevel(int minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public void addStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }
        quantity += amount;
    }

    public void removeStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }
        if (amount > quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        quantity -= amount;
    }

    public StockStatus getStockStatus() {
        if (quantity == 0) {
            return StockStatus.OUT_OF_STOCK;
        }
        if (quantity <= minimumStockLevel) {
            return StockStatus.LOW_STOCK;
        }
        return StockStatus.IN_STOCK;
    }
}
