package data;

import model.AVL;
import model.LatitudeEntry;
import model.LongitudeEntry;
import model.StationRecord;
import model.TimeZoneCountryKey;
import model.TimeZoneEntry;

import java.util.ArrayList;
import java.util.List;

public class StationRepository {

    private final AVL<LatitudeEntry> avlLat;
    private final AVL<LongitudeEntry> avlLon;
    private final AVL<TimeZoneEntry> avlTzCountry;
    private List<StationRecord> stations;

    public StationRepository() {
        this.avlLat = new AVL<>();
        this.avlLon = new AVL<>();
        this.avlTzCountry = new AVL<>();
        this.stations = new ArrayList<>();
    }

    /**
     * Carrega todas as estações a partir de um CSV e insere-as nas estruturas AVL.
     * @return número de estações carregadas
     */
    public int loadStationsFromCsv(String csvPath, boolean showRejected) throws Exception {
        List<StationRecord> loadedStations = CsvLoader.loadStationsFromFile(csvPath, showRejected);

        // manter uma cópia simples em memória
        this.stations = new ArrayList<>(loadedStations);

        for (StationRecord station : loadedStations) {
            addStation(station);
        }

        return loadedStations.size();
    }

    /**
     * Adiciona uma única estação ao repositório e às estruturas auxiliares.
     */
    public void addStation(StationRecord station) {
        if (station == null) {
            return;
        }

        avlLat.insert(new LatitudeEntry(station.getLatitude(), station));
        avlLon.insert(new LongitudeEntry(station.getLongitude(), station));

        TimeZoneCountryKey key = new TimeZoneCountryKey(station.getTimeZoneGroup(), station.getCountry());
        avlTzCountry.insert(new TimeZoneEntry(key, station));
    }

    /**
     * Procura todas as estações numa latitude exata.
     * As estações vêm ordenadas de acordo com a ordenação definida em LatitudeEntry
     * (para latitudes iguais, tipicamente por nome da estação).
     */
    public List<StationRecord> searchByLatitude(double latitude) {
        LatitudeEntry lowKey = new LatitudeEntry(latitude, null);
        // usamos um StationRecord com nome "\uFFFF" para garantir que é o maior
        StationRecord maxStation = new StationRecord("", "", "", "\uFFFF", latitude, 0.0, false, false, false);
        LatitudeEntry highKey = new LatitudeEntry(latitude, maxStation);

        List<LatitudeEntry> entries = avlLat.rangeQuery(lowKey, highKey);
        List<StationRecord> result = new ArrayList<>();

        for (LatitudeEntry entry : entries) {
            result.add(entry.getStation());
        }

        return result;
    }

    /**
     * Procura todas as estações numa longitude exata.
     */
    public List<StationRecord> searchByLongitude(double longitude) {
        LongitudeEntry lowKey = new LongitudeEntry(longitude, null);
        StationRecord maxStation = new StationRecord("", "", "", "\uFFFF", 0.0, longitude, false, false, false);
        LongitudeEntry highKey = new LongitudeEntry(longitude, maxStation);

        List<LongitudeEntry> entries = avlLon.rangeQuery(lowKey, highKey);
        List<StationRecord> result = new ArrayList<>();

        for (LongitudeEntry entry : entries) {
            result.add(entry.getStation());
        }

        return result;
    }

    /**
     * Procura todas as estações de um determinado time_zone_group,
     * ordenadas pela chave de TimeZoneEntry (time_zone_group + country).
     */
    public List<StationRecord> searchByTimeZoneGroup(String tzGroupName) {
        TimeZoneEntry lowKey = new TimeZoneEntry(new TimeZoneCountryKey(tzGroupName, ""), null);
        TimeZoneEntry highKey = new TimeZoneEntry(new TimeZoneCountryKey(tzGroupName, "\uFFFF"), null);

        List<TimeZoneEntry> entries = avlTzCountry.rangeQuery(lowKey, highKey);
        List<StationRecord> result = new ArrayList<>();

        for (TimeZoneEntry entry : entries) {
            result.add(entry.getStation());
        }

        return result;
    }

    /**
     * Devolve as estações cujo time_zone_group esteja dentro da janela [tzLower, tzUpper].
     */
    public List<StationRecord> searchByTimeZoneWindow(String tzLower, String tzUpper) {
        TimeZoneEntry lowKey = new TimeZoneEntry(new TimeZoneCountryKey(tzLower, ""), null);
        TimeZoneEntry highKey = new TimeZoneEntry(new TimeZoneCountryKey(tzUpper, "\uFFFF"), null);

        List<TimeZoneEntry> entries = avlTzCountry.rangeQuery(lowKey, highKey);
        List<StationRecord> result = new ArrayList<>();

        for (TimeZoneEntry entry : entries) {
            result.add(entry.getStation());
        }

        return result;
    }

    /**
     * Devolve uma cópia da lista interna de estações.
     */
    public List<StationRecord> getStations() {
        return new ArrayList<>(this.stations);
    }
}
