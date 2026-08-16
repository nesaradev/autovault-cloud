package com.nesaradev.autovault.part;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PartTest {
    @Test
    void newPartStartsWithCorrectDefaults() {
        Part part = new Part();
        assertEquals(0, part.getQuantity());
        assertTrue(part.isActive());
        assertEquals(StockStatus.OUT_OF_STOCK, part.getStockStatus());
    }

    @Test
    void addStockIncreasesQuantity() {
        Part part = new Part();
        part.addStock(5);
        assertEquals(5, part.getQuantity());
    }

    @Test
    void addStockRejectsZeroAmount() {
        Part part = new Part();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> part.addStock(0));
        assertEquals("amount must be greater than 0", exception.getMessage());
        assertEquals(0, part.getQuantity());
    }

    @Test
    void addStockRejectsNegativeAmount() {
        Part part = new Part();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> part.addStock(-1));
        assertEquals("amount must be greater than 0", exception.getMessage());
        assertEquals(0, part.getQuantity());
    }

    @Test
    void removeStockDecreasesQuantity() {
        Part part = new Part();
        part.addStock(10);
        part.removeStock(4);
        assertEquals(6, part.getQuantity());
    }

    @Test
    void removeStockRejectsZeroAmount() {
        Part part = new Part();
        part.addStock(10);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> part.removeStock(0));
        assertEquals("amount must be greater than 0", exception.getMessage());
        assertEquals(10, part.getQuantity());
    }

    @Test
    void removeStockRejectsNegativeAmount() {
        Part part = new Part();
        part.addStock(10);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> part.removeStock(-1));
        assertEquals("amount must be greater than 0", exception.getMessage());
        assertEquals(10, part.getQuantity());
    }

    @Test
    void removeStockRejectsAmountGreaterThanQuantity() {
        Part part = new Part();
        part.addStock(5);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> part.removeStock(6));
        assertEquals("Insufficient stock", exception.getMessage());
        assertEquals(5, part.getQuantity());
    }

    @Test
    void stockStatusIsLowWhenQuantityEqualsMinimumStockLevel() {
        Part part = new Part();
        part.setMinimumStockLevel(5);
        part.addStock(5);
        assertEquals(StockStatus.LOW_STOCK, part.getStockStatus());
    }

    @Test
    void stockStatusIsLowWhenQuantityIsBelowMinimumStockLevel() {
        Part part = new Part();
        part.setMinimumStockLevel(5);
        part.addStock(3);
        assertEquals(StockStatus.LOW_STOCK, part.getStockStatus());
    }

    @Test
    void stockStatusIsInStockWhenQuantityIsAboveMinimumStockLevel() {
        Part part = new Part();
        part.setMinimumStockLevel(5);
        part.addStock(6);
        assertEquals(StockStatus.IN_STOCK, part.getStockStatus());
    }
}
