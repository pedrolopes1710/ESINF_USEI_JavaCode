package data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import model.StationRecord;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class RepositoryTest {

    /**
     * Objetivo: testar carregar ficheiro e inserir nas AVLs.
     */
    @Test
    public void testLoadFromCsv() throws Exception {
        System.out.println("**** Teste: loadFromCsv ****");

        String csv =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Santa Apolonia,38.71387,-9.122271,True,False,False\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Oriente,38.71387,-9.122271,True,False,False\n" +
                "ES,\"('Europe/Madrid',)\",CET,Madrid Atocha,40.4069,-3.6902,True,True,False\n" +
                "FR,\"('Europe/Paris',)\",CET,Chateau-Arnoux-St-Auban,44.08179,6.001625,True,False,False\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon,38.71387,-9.122271,False,False,False\n";

        Path tmp = Files.createTempFile("stations_repo_test_", ".csv");
        tmp.toFile().deleteOnExit();

        try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            writer.write(csv);
        }

        StationRepository repo = new StationRepository();
        int loaded = repo.loadStationsFromCsv(tmp.toString(), true);

        System.out.println("Carregado: " + loaded);
        Assertions.assertEquals(5, loaded);
    }

    /**
     * Objetivo: testar estações com mesma latitude.
     * Resultado: duas estações ordenadas por nome, a primeira "Lisbon Oriente".
     */
    @Test
    public void testFindByLatitude() throws Exception {
        System.out.println("**** Teste: findByLatitude ****");

        String csv =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Santa Apolonia,38.71387,-9.122271,False,False,False\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Oriente,38.71387,-9.122271,True,False,False\n";

        Path tmp = Files.createTempFile("repo_findbylat_", ".csv");
        tmp.toFile().deleteOnExit();

        try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            writer.write(csv);
        }

        StationRepository repo = new StationRepository();
        repo.loadStationsFromCsv(tmp.toString(), true);

        List<StationRecord> latMatches = repo.searchByLatitude(38.71387);

        System.out.println("Resultado: " + latMatches.size());
        for (StationRecord s : latMatches) {
            System.out.println("  " + s);
        }

        Assertions.assertEquals(2, latMatches.size());
        Assertions.assertEquals("Lisbon Oriente", latMatches.get(0).getName());
    }

    /**
     * Objetivo: testar estações com mesma longitude.
     */
    @Test
    public void testFindByLongitude() throws Exception {
        System.out.println("**** Teste: findByLongitude ****");

        String csv =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "IT,\"('Europe/Rome',)\",CET,Montallegro,37.3911879,13.3503598,True,False,False\n" +
                "IT,\"('Europe/Rome',)\",CET,SPalma di Montechiaro,37.192768,13.3503598,False,False,False\n";

        Path tmp = Files.createTempFile("repo_findbylon_", ".csv");
        tmp.toFile().deleteOnExit();

        try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            writer.write(csv);
        }

        StationRepository repo = new StationRepository();
        repo.loadStationsFromCsv(tmp.toString(), true);

        List<StationRecord> lonMatches = repo.searchByLongitude(13.3503598);

        System.out.println("Resultado: " + lonMatches.size());
        for (StationRecord s : lonMatches) {
            System.out.println("  " + s);
        }

        Assertions.assertEquals(2, lonMatches.size());
    }

    /**
     * Objetivo: testar operações na janela [CET, WET/GMT].
     */
    @Test
    public void testFindByTimeZoneWindow() throws Exception {
        System.out.println("**** Teste: findByTimeZoneWindow ****");

        String csv =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "ES,\"('Europe/Madrid',)\",CET,Madrid Atocha,40.4069,-3.6902,True,True,False\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Santa Apolonia,38.71387,-9.122271,False,False,False\n";

        Path tmp = Files.createTempFile("repo_tzwindow_", ".csv");
        tmp.toFile().deleteOnExit();

        try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            writer.write(csv);
        }

        StationRepository repo = new StationRepository();
        repo.loadStationsFromCsv(tmp.toString(), true);

        List<StationRecord> window = repo.searchByTimeZoneWindow("CET", "WET/GMT");

        System.out.println("Resultado: " + window.size());
        for (StationRecord s : window) {
            System.out.println("  " + s);
        }

        Assertions.assertEquals(2, window.size());
    }

    /**
     * Objetivo: testar todas as estações de um grupo, ordenadas por country asc.
     */
    @Test
    public void testFindByTimeZoneGroup() throws Exception {
        System.out.println("**** Teste: findByTimeZoneGroup ****");

        String csv =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "BE,\"('Europe/Brussels',)\",CET,Soignies,50.573177,4.335695,True,False,False\n" +
                "DE,\"('Europe/Berlin',)\",CET,Bad Driburg (Westf),51.730322,9.030806,True,False,False\n" +
                "FR,\"('Europe/Paris',)\",CET,Decazeville,44.55,2.25,True,False,False\n";

        Path tmp = Files.createTempFile("repo_tzgroup_", ".csv");
        tmp.toFile().deleteOnExit();

        try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            writer.write(csv);
        }

        StationRepository repo = new StationRepository();
        repo.loadStationsFromCsv(tmp.toString(), true);

        List<StationRecord> tz = repo.searchByTimeZoneGroup("CET");

        System.out.println("Resultado: " + tz.size());
        for (StationRecord s : tz) {
            System.out.println("  " + s);
        }

        Assertions.assertEquals(3, tz.size());
        Assertions.assertEquals("BE", tz.get(0).getCountry());
        Assertions.assertEquals("DE", tz.get(1).getCountry());
        Assertions.assertEquals("FR", tz.get(2).getCountry());
    }
}
