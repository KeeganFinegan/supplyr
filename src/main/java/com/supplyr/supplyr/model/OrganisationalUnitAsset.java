package com.supplyr.supplyr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;


@Table(name = "organisational_unit_assets")
@Entity
public class OrganisationalUnitAsset {

    @EmbeddedId
    @JsonIgnore
    private OrganisationalUnitAssetId id;

    @ManyToOne()
    @JoinColumn(name = "organisational_unit_id", insertable = false, updatable = false)
    private OrganisationalUnit organisationalUnit;


    @ManyToOne()
    @JoinColumn(name = "asset_id", insertable = false, updatable = false)
    private Asset asset;

    private Integer quantity;

    public OrganisationalUnitAsset() {
    }

    public OrganisationalUnitAsset(OrganisationalUnitAssetId id, OrganisationalUnit organisationalUnit, Asset asset, Integer quantity) {
        this.id = id;
        this.organisationalUnit = organisationalUnit;
        this.asset = asset;
        this.quantity = quantity;
    }

    public OrganisationalUnitAssetId getId() {
        return id;
    }

    public void setId(OrganisationalUnitAssetId id) {
        this.id = id;
    }

    public OrganisationalUnit getOrganisationalUnit() {
        return organisationalUnit;
    }

    public void setOrganisationalUnit(OrganisationalUnit organisationalUnit) {
        this.organisationalUnit = organisationalUnit;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
