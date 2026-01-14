package usei12;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;
import java.util.Map;

public class KruskalServiceTest {

    @Test
    void mstCostAndFiles() throws Exception {
        List<Edge> edges = List.of(
                new Edge("A", "B", 4),
                new Edge("A", "C", 3),
                new Edge("B", "C", 2),
                new Edge("B", "D", 5)
        );

        // Coordenadas fictícias para o mapa
        Map<String, double[]> coords = Map.of(
                "A", new double[]{0, 0},
                "B", new double[]{2, 4},
                "C", new double[]{4, 0},
                "D", new double[]{6, 4}
        );

        KruskalService ks = new KruskalService();
        List<Edge> mst = ks.minimumSpanningTree(edges);

        // 1. Testa o custo
        assertEquals(10, ks.totalCost(mst));

        // 2. Gera os ficheiros
        ks.generateFullReport(mst, coords, "backbone_test");

        // 3. Verifica se o DOT apareceu
        assertTrue(new File("backbone_test.dot").exists(), "O ficheiro DOT devia existir");
    }
}
