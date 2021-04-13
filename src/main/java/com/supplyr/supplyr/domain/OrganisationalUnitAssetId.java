package com.supplyr.supplyr.domain;

import com.sun.istack.NotNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class OrganisationalUnitAssetId implements Serializable {

    @Column(name = "organisational_unit_id")
    @NotNull
    private Long organisationalUnitId;

    @Column(name = "asset_id")
    @NotNull
    private Long assetId;

    public OrganisationalUnitAssetId() {
    }

    public OrganisationalUnitAssetId(Long organisationalUnitId, Long assetId) {
        this.organisationalUnitId = organisationalUnitId;
        this.assetId = assetId;
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

}
