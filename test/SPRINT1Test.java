import org.junit.jupiter.api.*;
import java.util.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;


public class SPRINT1Test {

    @BeforeAll
    static void setup() {
        // Garantir que os dados base são carregados antes de todos os testes
        SPRINT1.loadItems("train_station_dataset/items.csv");
        SPRINT1.loadBays("train_station_dataset/bays.csv");
        SPRINT1.loadBoxesFromWagons("train_station_dataset/wagons.csv");
    }

    @Test
    @DisplayName("Carregamento de Items")
    void testLoadItems() {
        assertFalse(SPRINT1.items.isEmpty(), "Os items não foram carregados corretamente.");
    }

    @Test
    @DisplayName("Carregamento de Bays")
    void testLoadBays() {
        assertFalse(SPRINT1.bays.isEmpty(), "As bays não foram carregadas corretamente.");
        assertTrue(SPRINT1.bays.get(0).capacityBoxes > 0, "As bays devem ter capacidade positiva.");
    }

    @Test
    @DisplayName("Carregamento de Boxes")
    void testLoadBoxesFromWagons() {
        assertFalse(SPRINT1.boxes.isEmpty(), "As boxes não foram carregadas corretamente.");
        assertNotNull(SPRINT1.boxes.get(0).sku, "Cada box deve ter um SKU associado.");
    }

    @Test
    @DisplayName("Despacho de SKU (FEFO)")
    void testDespacharSKU() {
        String sku = SPRINT1.boxes.get(0).sku;
        int before = SPRINT1.boxes.stream().filter(b -> b.sku.equals(sku)).mapToInt(b -> b.qty).sum();
        SPRINT1.despacharSKU(sku, 5);
        int after = SPRINT1.boxes.stream().filter(b -> b.sku.equals(sku)).mapToInt(b -> b.qty).sum();
        assertTrue(before >= after, "Após despacho, a quantidade total deve ser menor ou igual.");
    }

    @Test
    @DisplayName("Carregamento de Orders e Order Lines (USE02)")
    void testLoadOrdersAndOrderLines() {
        SPRINT1.loadOrders("train_station_dataset/orders.csv");
        SPRINT1.loadOrderLines("train_station_dataset/order_lines.csv");

        assertFalse(SPRINT1.orders.isEmpty(), "As orders não foram carregadas.");
        assertFalse(SPRINT1.orderLines.isEmpty(), "As order lines não foram carregadas.");
    }

    @Test
    @DisplayName("Processamento de Orders (USE02)")
    void testProcessOrders() {
        // Garante que orders e order lines foram carregadas
        if (SPRINT1.orders.isEmpty() || SPRINT1.orderLines.isEmpty()) {
            SPRINT1.loadOrders("train_station_dataset/orders.csv");
            SPRINT1.loadOrderLines("train_station_dataset/order_lines.csv");
        }

        assertDoesNotThrow(SPRINT1::processOrders, 
            "O processamento de encomendas não deve gerar exceções.");
    }

    @AfterAll
    static void tearDown() {
        // Limpar as estruturas de dados para evitar interferência entre testes
        SPRINT1.items.clear();
        SPRINT1.bays.clear();
        SPRINT1.boxes.clear();
        SPRINT1.orders.clear();
        SPRINT1.orderLines.clear();
    }
}
