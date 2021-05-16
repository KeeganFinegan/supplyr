package com.supplyr.supplyr.domain;

public class OrganisationalUnitAssetDto {

    private Long organisationalUnitId;

    private Long assetId;

    private String organisationalUnitName;

    private String assetName;

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

    public String getOrganisationalUnitName() {
        return organisationalUnitName;
    }

    public void setOrganisationalUnitName(String organisationalUnitId) {
        this.organisationalUnitName = organisationalUnitId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetId) {
        this.assetName = assetId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
