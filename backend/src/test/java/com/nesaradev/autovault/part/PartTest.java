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
}
