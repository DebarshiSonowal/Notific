package com.deb.notific.helper;

public class polylocation {
    private  double Latitude;
    private  double Longitude;

    public polylocation(double latitude, double longitude) {
        Latitude = latitude;
        Longitude = longitude;
    }

    public polylocation() {

    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
