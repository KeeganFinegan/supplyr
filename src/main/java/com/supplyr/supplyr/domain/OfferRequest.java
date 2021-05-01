package com.supplyr.supplyr.domain;

public class OfferRequest {
    private String asset;
    private String organisationalUnit;
    private double quantity;
    private double price;

    public OfferRequest() {
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getOrganisationalUnit() {
        return organisationalUnit;
    }

    public void setOrganisationalUnit(String organisationalUnit) {
        this.organisationalUnit = organisationalUnit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
