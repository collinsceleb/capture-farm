package com.bdn.collinsceleb.capturefarm.models;

public class AddressInfo {

    private String addressLine;
    private double latitude;
    private double longitude;
    public AddressInfo() {

    }

    public AddressInfo(String addressLine, double latitude, double longitude) {
        this.addressLine = addressLine;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "AddressInfo{" +
                "addressLine='" + addressLine + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

}
