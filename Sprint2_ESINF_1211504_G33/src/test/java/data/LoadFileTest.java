package data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import model.StationRecord;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LoadFileTest {

    /**
     * Objetivo: validar que apenas as linhas corretas são carregadas.
     * Linha 2 tem time_zone_group vazio, linha 3 tem station vazia.
     * Apenas a primeira deve ser aceite.
     */
    @Test
    public void testLoadFile() throws Exception {
        System.out.println("***** Teste: Validar linhas *****");

        String csvContent =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Vila Franca das Naves,40.724541,-7.258357,False,False,False\n" +
                "FR,\"('Europe',)\",,La Crau,0.0,0.0,False,False,False\n" +
                "GB,\"('Europe/London',)\",WET/GMT,,51.5,-0.1,True,True,False\n";

        Path tmp = Files.createTempFile("stations_test_", ".csv");
        tmp.toFile().deleteOnExit();

        try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            writer.write(csvContent);
        }

        List<StationRecord> stations = CsvLoader.loadStationsFromFile(tmp.toString(), true);

        System.out.println("Estações válidas carregadas: " + stations.size());
        for (StationRecord s : stations) {
            System.out.println("  " + s);
        }

        Assertions.assertEquals(1, stations.size());
        Assertions.assertEquals("Vila Franca das Naves", stations.get(0).getName());
    }
}
