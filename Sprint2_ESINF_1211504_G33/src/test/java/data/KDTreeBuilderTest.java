package data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import model.KDTree;
import model.StationRecord;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class KDTreeBuilderTest {

    /**
     * Objetivo: garantir que o KDTree contém todas as estações
     * e que a pesquisa exata por coordenadas encontra as estações corretas.
     */
    @Test
    public void testBuildFromRepoAndQuery() throws Exception {
        System.out.println("***** Teste: KDTreeBuilder.buildFromRepo *****");

        String csvContent =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Santa Apolonia,38.7139,-9.1252,True,False,False\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Oriente,38.7139,-9.1252,True,False,False\n" +
                "ES,\"('Europe/Madrid',)\",CET,Madrid Atocha,40.4069,-3.6902,True,True,False\n";

        Path tmp = Files.createTempFile("kdtree_builder_test_", ".csv");
        tmp.toFile().deleteOnExit();

        try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            writer.write(csvContent);
        }

        StationRepository repository = new StationRepository();
        int loaded = repository.loadStationsFromCsv(tmp.toString(), true);

        KDTree tree = KDTreeBuilder.buildFromRepo(repository);

        System.out.println("Estações carregadas: " + loaded);
        Assertions.assertEquals(loaded, tree.size(),"KDTree.size() deve igualar o número de estações carregadas.");

        List<StationRecord> exactMatches = tree.rangeQuery(38.7139, 38.7139, -9.1252, -9.1252);

        System.out.println("Resultado da query exata: " + exactMatches.size());
        for (StationRecord s : exactMatches) {
            System.out.println("  " + s);
        }

        Assertions.assertEquals(2, exactMatches.size(),"Devem existir 2 estações exatamente nessa coordenada");
    }
}
