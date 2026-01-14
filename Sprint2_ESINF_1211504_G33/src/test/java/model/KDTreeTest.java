package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class KDTreeTest {

    /**
     * Objetivos:
     *  - Garantir que duplicados (mesmas coordenadas) ficam no mesmo bucket ordenados por nome
     *  - Verificar range query que inclui pontos próximos de Lisboa
     *  - Verificar kNN devolve as estações mais próximas
     */
    @Test
    public void testBucketsRangeAndKNN() {
        System.out.println("**** KDTreeUnitTest: duplicados, range e kNN ****");

        List<StationRecord> sts = new ArrayList<>();

        //Duas estações com exactas mesmas coordenadas
        sts.add(new StationRecord("PT", "('Europe/Lisbon',)", "WET/GMT", "AAA-Lisbon-SameCoord", 38.7139, -9.1252, false, false, false));
        sts.add(new StationRecord("PT", "('Europe/Lisbon',)", "WET/GMT", "Lisbon Santa Apolonia", 38.7139, -9.1252, true, false, false));

        //Estação perto (Lisbon Oriente)
        sts.add(new StationRecord("PT", "('Europe/Lisbon',)", "WET/GMT", "Lisbon Oriente", 38.7578, -9.0977, true, false, false));

        //Estação Espanha (Madrid Atocha)
        sts.add(new StationRecord("ES", "('Europe/Madrid',)", "CET", "Madrid Atocha", 40.4069, -3.6902, true, true, false));

        KDTree tree = KDTree.buildFromStations(sts);

        tree.printStats();

        Assertions.assertEquals(4, tree.size(), "Total de estações deve ser 4.");
        Assertions.assertEquals(3,tree.nodeCount(),"Número de nós deve ser 3.");


        boolean hasBucket2 = tree.bucketHistogram().getOrDefault(2, 0) > 0;

        Assertions.assertTrue(hasBucket2, "Deve existir pelo menos um bucket com 2 estações (duplicados).");

        // Range query: Inclui as estações de Lisboa
        List<StationRecord> inRange = tree.rangeQuery(38.70, 38.76, -9.13, -9.09);
        System.out.println("RangeQuery(38.70..38.76, -9.13..-9.09) -> count: " + inRange.size());

        for (StationRecord s : inRange){
            System.out.println("  " + s);
        }

        Assertions.assertEquals(3, inRange.size(),"Devem ser 3 estações na range (AA A + Santa Apolonia + Oriente)");
        Assertions.assertTrue(inRange.stream().anyMatch(s -> s.getName().equals("AAA-Lisbon-SameCoord")));
        Assertions.assertTrue(inRange.stream().anyMatch(s -> s.getName().equals("Lisbon Santa Apolonia")));
        Assertions.assertTrue(inRange.stream().anyMatch(s -> s.getName().equals("Lisbon Oriente")));

        //kNN: procurar 2 nearest a ponto muito perto de AAA-Lisbon-SameCoord
        List<StationRecord> knn = tree.kNearest(38.7140, -9.1250, 2);
        System.out.println("kNN(38.7140,-9.1250) k=2 ->");

        for (StationRecord s : knn){
            System.out.println("  " + s);
        }

        Assertions.assertEquals(2, knn.size(),"kNN deve devolver 2 estações");

        Assertions.assertTrue(knn.stream().anyMatch(s -> s.getName().equals("AAA-Lisbon-SameCoord")));
        Assertions.assertTrue(knn.stream().anyMatch(s -> s.getName().equals("Lisbon Santa Apolonia")));
    }

}
