package com.supplyr.supplyr.domain;

import javax.persistence.*;

@Entity
@Table(name = "organisational_unit_assets")
public class OrganisationalUnitAsset {

    @EmbeddedId
    private OrganisationalUnitAssetId id;

    @ManyToOne()
    @JoinColumn(name = "organisational_unit_id", insertable = false, updatable = false)
    private OrganisationalUnit organisationalUnit;


    @ManyToOne()
    @JoinColumn(name = "asset_id", insertable = false, updatable = false)
    private Asset asset;

    private double quantity;

    public OrganisationalUnitAsset() {
    }

    public OrganisationalUnitAsset(OrganisationalUnitAssetId id,
                                   OrganisationalUnit organisationalUnit,
                                   Asset asset,
                                   double quantity) {
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", organisationalUnit=" + organisationalUnit +
                ", asset=" + asset+
                ", quantity=" + quantity +
                '}';
    }
}
