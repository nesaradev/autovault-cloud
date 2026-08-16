package com.nesaradev.autovault.part;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
