package com.supplyr.supplyr.model;

public class OrganisationalUnitAssetDto {

    private Long organisationalUnitId;

    private Long assetId;

    private double quantity;

    public OrganisationalUnitAssetDto() {
    }

    public Long getOrganisationalUnitId() {
        return organisationalUnitId;
    }

    public void setOrganisationalUnitId(Long organisationalUnitId) {
        this.organisationalUnitId = organisationalUnitId;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
