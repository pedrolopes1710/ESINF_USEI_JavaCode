package model;

public class LatitudeEntry implements Comparable<LatitudeEntry> {
    private final double lat;
    private final StationRecord station;

    public LatitudeEntry(double lat, StationRecord station) {
        this.lat = lat;
        this.station = station;
    }

    public double getLat() {
        return lat;
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
    public int compareTo(LatitudeEntry o) {

        int c = Double.compare(this.lat, o.lat);

        if (c != 0) return c;

        return stationNameSafe().compareTo(o.station == null ? "" : (o.station.getName() == null ? "" : o.station.getName()));
    }

    @Override
    public String toString() {
        String name = (station == null) ? "null" : station.getName();

        return String.format("LatitudeEntry[lat=%.6f, station=%s]", lat, name);
    }
}
