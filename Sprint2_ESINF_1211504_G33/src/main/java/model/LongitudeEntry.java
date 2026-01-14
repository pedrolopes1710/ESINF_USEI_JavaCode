package model;

public class LongitudeEntry implements Comparable<LongitudeEntry> {
    private final double lon;
    private final StationRecord station;

    public LongitudeEntry(double lon, StationRecord station) {
        this.lon = lon;
        this.station = station;
    }

    public double getLon() {
        return lon;
    }

    public StationRecord getStation() {
        return station;
    }

    private String stationNameSafe() {

        if (station == null) return "";

        String n = station.getName();

        return n == null ? "" : n;
    }

    @Override
    public int compareTo(LongitudeEntry o) {

        int c = Double.compare(this.lon, o.lon);

        if (c != 0) return c;

        return stationNameSafe().compareTo(o.station == null ? "" : (o.station.getName() == null ? "" : o.station.getName()));
    }

    @Override
    public String toString() {
        String name = (station == null) ? "null" : station.getName();

        return String.format("LongitudeEntry[lon=%.6f, station=%s]", lon, name);
    }
}
