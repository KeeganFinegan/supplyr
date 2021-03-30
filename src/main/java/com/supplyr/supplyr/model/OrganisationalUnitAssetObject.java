package com.supplyr.supplyr.model;

public class OrganisationalUnitAssetObject {

    private Long organisationalUnitId;

    private Long assetId;

    private Integer quantity;

    public OrganisationalUnitAssetObject() {
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
