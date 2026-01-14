package controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import data.StationRepository;
import model.StationRecord;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Testes referentes às operações do StationService (controller).
 * A abordagem baseia-se em carregar pequenos CSVs temporários e validar o comportamento.
 */
public class StationControllerTest {

    /**
     * Verifica se o carregamento via controller devolve o número correto de entradas.
     */
    @Test
    public void testLoadCsv() throws Exception {
        System.out.println("== ControllerTest: loadStations ==");

        String csvContent =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisboa,38.71387,-9.122271,False,False,False\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Santa Apolonia,38.71387,-9.122271,True,False,False\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Oriente,38.71387,-9.122271,True,False,False\n" +
                "ES,\"('Europe/Madrid',)\",CET,Madrid Atocha,40.405963,-3.689757,False,True,False\n";

        Path tmpCsv = Files.createTempFile("ctrl_load_", ".csv");
        tmpCsv.toFile().deleteOnExit();

        try (BufferedWriter bw = Files.newBufferedWriter(tmpCsv, StandardCharsets.UTF_8)) {
            bw.write(csvContent);
        }

        StationRepository repo = new StationRepository();
        StationService service = new StationService(repo);

        int result = service.loadStations(tmpCsv.toString(), true);

        System.out.println("Total carregado: " + result);
        Assertions.assertEquals(4, result);
    }

    /**
     * Verifica se a pesquisa por latitude devolve as duas estações esperadas e em ordem correcta.
     */
    @Test
    public void testSearchByLatitude() throws Exception {
        System.out.println("== ControllerTest: searchByLatitude ==");

        String csv =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Santa Apolonia,38.71387,-9.122271,False,False,False\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Oriente,38.71387,-9.122271,True,False,False\n";

        Path temp = Files.createTempFile("ctrl_lat_", ".csv");
        temp.toFile().deleteOnExit();

        try (BufferedWriter bw = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            bw.write(csv);
        }

        StationRepository repository = new StationRepository();
        StationService service = new StationService(repository);
        service.loadStations(temp.toString(), true);

        List<StationRecord> result = service.getStationsByLatitude(38.71387);

        System.out.println("Encontradas: " + result.size());
        result.forEach(r -> System.out.println("  -> " + r));

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Lisbon Oriente", result.get(0).getName());
        Assertions.assertEquals("Lisbon Santa Apolonia", result.get(1).getName());
    }

    /**
     * Testa pesquisa por longitude comum.
     */
    @Test
    public void testSearchByLongitude() throws Exception {
        System.out.println("== ControllerTest: searchByLongitude ==");

        String csv =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "IT,\"('Europe/Rome',)\",CET,Montallegro,37.3911879,13.3503598,True,False,False\n" +
                "IT,\"('Europe/Rome',)\",CET,SPalma di Montechiaro,37.192768,13.3503598,False,False,False\n";

        Path csvTmp = Files.createTempFile("ctrl_lon_", ".csv");
        csvTmp.toFile().deleteOnExit();

        try (BufferedWriter bw = Files.newBufferedWriter(csvTmp, StandardCharsets.UTF_8)) {
            bw.write(csv);
        }

        StationRepository repo = new StationRepository();
        StationService service = new StationService(repo);
        service.loadStations(csvTmp.toString(), true);

        List<StationRecord> found = service.getStationsByLongitude(13.3503598);

        System.out.println("Total: " + found.size());
        found.forEach(s -> System.out.println(" * " + s));

        Assertions.assertEquals(2, found.size());
        Assertions.assertTrue(found.stream().anyMatch(s -> s.getName().equals("Montallegro")));
        Assertions.assertTrue(found.stream().anyMatch(s -> s.getName().equals("SPalma di Montechiaro")));
    }

    /**
     * Verifica se o agrupamento por timeZoneGroup funciona.
     */
    @Test
    public void testSearchByTimeZoneGroup() throws Exception {
        System.out.println("== ControllerTest: searchByTZGroup ==");

        String csv =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon,38.71387,-9.122271,False,False,False\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Santa Apolonia,38.71387,-9.122271,True,False,False\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon Oriente,38.71387,-9.122271,True,False,False\n";

        Path tmp = Files.createTempFile("ctrl_tzg_", ".csv");
        tmp.toFile().deleteOnExit();

        try (BufferedWriter bw = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            bw.write(csv);
        }

        StationRepository repo = new StationRepository();
        StationService service = new StationService(repo);
        service.loadStations(tmp.toString(), true);

        List<StationRecord> list = service.getStationsByTimeZoneGroup("WET/GMT");

        System.out.println("WET/GMT encontrados: " + list.size());
        list.forEach(s -> System.out.println(" - " + s));

        Assertions.assertEquals(3, list.size());
        Assertions.assertTrue(list.stream().allMatch(s -> "WET/GMT".equals(s.getTimeZoneGroup())));
    }

    /**
     * Testa pesquisa na janela CET..WET/GMT.
     */
    @Test
    public void testSearchByTimeZoneWindow() throws Exception {
        System.out.println("== ControllerTest: searchByTZWindow ==");

        String csv =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "ES,\"('Europe/Madrid',)\",CET,Madrid Atocha,40.4069,-3.6902,True,True,False\n" +
                "FR,\"('Europe/Paris',)\",CET,Chateau-Arnoux-St-Auban,44.08179,6.001625,True,False,False\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,AAA-Lisbon,38.7139,-9.1252,False,False,False\n";

        Path temp = Files.createTempFile("ctrl_window_", ".csv");
        temp.toFile().deleteOnExit();

        try (BufferedWriter bw = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            bw.write(csv);
        }

        StationRepository repo = new StationRepository();
        StationService service = new StationService(repo);
        service.loadStations(temp.toString(), true);

        List<StationRecord> window = service.getStationsByTimeZoneWindow("CET", "WET/GMT");

        System.out.println("Janela: " + window.size());
        window.forEach(s -> System.out.println(" • " + s));

        Assertions.assertEquals(3, window.size());
        Assertions.assertTrue(window.stream().anyMatch(s -> "CET".equals(s.getTimeZoneGroup())));
        Assertions.assertTrue(window.stream().anyMatch(s -> "WET/GMT".equals(s.getTimeZoneGroup())));
    }

    /**
     * Testa retorno vazio quando o grupo não existe.
     */
    @Test
    public void testSearchByTimeZoneGroupNoExist() throws Exception {
        System.out.println("== ControllerTest: searchByTZGroup inexistente ==");

        String csv =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,AAA,38.7139,-9.1252,False,False,False\n";

        Path temp = Files.createTempFile("ctrl_tzg_none_", ".csv");
        temp.toFile().deleteOnExit();

        try (BufferedWriter bw = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            bw.write(csv);
        }

        StationRepository repo = new StationRepository();
        StationService service = new StationService(repo);
        service.loadStations(temp.toString(), true);

        List<StationRecord> empty = service.getStationsByTimeZoneGroup("Não existe");

        System.out.println("Resultado: " + empty.size());
        Assertions.assertTrue(empty.isEmpty());
    }

    /**
     * Garante que a latitude 0 não coincide com nenhuma entrada.
     */
    @Test
    public void testSearchByLatitudeZero() throws Exception {
        System.out.println("== ControllerTest: searchByLatitude(0.0) ==");

        String csv =
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "PT,\"('Europe/Lisbon',)\",WET/GMT,Lisbon,38.7139,-9.1252,False,False,False\n";

        Path temp = Files.createTempFile("ctrl_lat0_", ".csv");
        temp.toFile().deleteOnExit();

        try (BufferedWriter bw = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            bw.write(csv);
        }

        StationRepository repo = new StationRepository();
        StationService service = new StationService(repo);
        service.loadStations(temp.toString(), true);

        List<StationRecord> none = service.getStationsByLatitude(0.0);

        System.out.println("Resultado: " + none.size());
        Assertions.assertTrue(none.isEmpty());
    }
}
