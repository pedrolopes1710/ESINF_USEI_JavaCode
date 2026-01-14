package data;

import model.StationRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    /**
     * Lê o ficheiro CSV e devolve apenas as estações consideradas válidas.
     * @param filePath      caminho para o ficheiro CSV
     * @param showRejected  se true, escreve no stdout as linhas rejeitadas
     */
    public static List<StationRecord> loadStationsFromFile(String filePath, boolean showRejected) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return loadFromReader(reader, showRejected);
        }
    }

    /**
     * Lê todas as linhas (ignorando o header) a partir de um reader.
     */
    private static List<StationRecord> loadFromReader(BufferedReader reader, boolean showRejected) throws IOException {
        // descartar header
        String header = reader.readLine();

        List<StationRecord> validStations = new ArrayList<>();
        String line;
        int lineNumber = 1; // já lemos o header

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            if (line.trim().isEmpty()) {
                continue;
            }

            // Usamos o splitCsvLine que lida com aspas e vírgulas internas
            String[] columns = parseCsvLine(line, 9);
            
            if (columns == null || columns.length != 9) {
                if (showRejected) {
                    System.out.println("[trains_stations_europe.csv] - Número de colunas inválido. Linha -> " + lineNumber);
                }
                continue;
            }

            try {
                StationRecord station = buildStation(columns, lineNumber, showRejected);
                if (station != null) {
                    validStations.add(station);
                }
            } catch (Exception ex) {
                if (showRejected) {
                    System.out.println("[trains_stations_europe.csv] - Erro. Linha -> " + lineNumber + " -> " + ex.getMessage());
                }
            }
        }

        return validStations;
    }

    /**
     * Constrói um StationRecord a partir de uma linha já separada em colunas.
     */
    private static StationRecord buildStation(String[] cols, int lineNumber, boolean showRejected) {
        String countryName     = unquote(cols[0]);
        String timeZone        = unquote(cols[1]);
        String timeZoneGroup   = unquote(cols[2]);
        String stationName     = unquote(cols[3]);
        double latitude        = Double.parseDouble(cols[4]);
        double longitude       = Double.parseDouble(cols[5]);
        boolean cityFlag       = Boolean.parseBoolean(cols[6]);
        boolean mainFlag       = Boolean.parseBoolean(cols[7]);
        boolean airportFlag    = Boolean.parseBoolean(cols[8]);

        // Validações semânticas
        if (isBlank(stationName)) {
            if (showRejected) System.out.println("[trains_stations_europe.csv] - Station vazia. Linha -> " + lineNumber);
            return null;
        }

        if (isBlank(countryName)) {
            if (showRejected) System.out.println("[trains_stations_europe.csv] - Country vazio. Linha -> " + lineNumber);
            return null;
        }

        if (isBlank(timeZoneGroup)) {
            if (showRejected) System.out.println("[trains_stations_europe.csv] - time_zone_group vazio. Linha -> " + lineNumber);
            return null;
        }

        if (Double.isNaN(latitude) || latitude < -90.0 || latitude > 90.0) {
            if (showRejected) System.out.println("[trains_stations_europe.csv] - Latitude inválida. Linha -> " + lineNumber);
            return null;
        }

        if (Double.isNaN(longitude) || longitude < -180.0 || longitude > 180.0) {
            if (showRejected) System.out.println("[trains_stations_europe.csv] - Longitude inválida. Linha -> " + lineNumber);
            return null;
        }

        return new StationRecord(
                countryName,
                timeZone,
                timeZoneGroup,
                stationName,
                latitude,
                longitude,
                cityFlag,
                mainFlag,
                airportFlag
        );
    }

    private static String[] parseCsvLine(String line, int expectedCols) {
        List<String> cols = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder cur = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                cur.append(c);
            } else if (c == ',' && !inQuotes) {
                cols.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        cols.add(cur.toString());

        if (cols.size() != expectedCols) return null;
        return cols.toArray(new String[0]);
    }

    private static String unquote(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}