package model;

public class TimeZoneCountryKey implements Comparable<TimeZoneCountryKey> {
    private final String timeZoneGroup;
    private final String country;

    public TimeZoneCountryKey(String timeZoneGroup, String country) {
        this.timeZoneGroup = timeZoneGroup == null ? "" : timeZoneGroup;
        this.country = country == null ? "" : country;
    }

    public String getTimeZoneGroup() {
        return timeZoneGroup;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public int compareTo(TimeZoneCountryKey o) {

        int c = this.timeZoneGroup.compareTo(o.timeZoneGroup);
        if (c != 0) return c;

        return this.country.compareTo(o.country);
    }

    @Override
    public String toString() {
        return timeZoneGroup + "|" + country;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof TimeZoneCountryKey)) return false;

        TimeZoneCountryKey other = (TimeZoneCountryKey)o;

        return this.timeZoneGroup.equals(other.timeZoneGroup) && this.country.equals(other.country);
    }

    @Override
    public int hashCode() {
        return timeZoneGroup.hashCode()*31 + country.hashCode();
    }
}
