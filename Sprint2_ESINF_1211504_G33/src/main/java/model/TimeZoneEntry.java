package model;

public class TimeZoneEntry implements Comparable<TimeZoneEntry> {
    private final TimeZoneCountryKey key;
    private final StationRecord station;

    public TimeZoneEntry(TimeZoneCountryKey key, StationRecord station) {
        this.key = key;
        this.station = station;
    }

    public TimeZoneCountryKey getKey() {
        return key;
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
    public int compareTo(TimeZoneEntry o) {

        int c = this.key.compareTo(o.key);

        if (c != 0) return c;

        return stationNameSafe().compareTo(o.station == null ? "" : (o.station.getName() == null ? "" : o.station.getName()));
    }

    @Override
    public String toString() {
        String name = (station == null) ? "null" : station.getName();

        return String.format("TimeZoneEntry[key=%s, station=%s]", key.toString(), name);
    }
}
