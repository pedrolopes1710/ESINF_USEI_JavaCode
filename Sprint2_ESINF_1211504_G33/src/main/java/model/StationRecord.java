package model;

/**
 * Estação ferroviária (renomeada)
 */
public class StationRecord {

    private final String country;
    private final String timeZone;
    private final String timeZoneGroup;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final boolean isCity;
    private final boolean isMainStation;
    private final boolean isAirport;

    public StationRecord(String country, String timeZone, String timeZoneGroup, String name, double latitude, double longitude, boolean isCity, boolean isMainStation, boolean isAirport) {
        this.country = country;
        this.timeZone = timeZone;
        this.timeZoneGroup = timeZoneGroup;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isCity = isCity;
        this.isMainStation = isMainStation;
        this.isAirport = isAirport;
    }

    public String getCountry() { return country; }
    public String getTimeZone() { return timeZone; }
    public String getTimeZoneGroup() { return timeZoneGroup; }
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isCity() { return isCity; }
    public boolean isMainStation() { return isMainStation; }
    public boolean isAirport() { return isAirport; }

    @Override
    public String toString() {
        return String.format("StationRecord[name=%s, country=%s, tzGroup=%s, lat=%.6f, lon=%.6f]",
                name, country, timeZoneGroup, latitude, longitude);
    }
}
