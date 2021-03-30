package com.supplyr.supplyr.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrganisationalUnitAssetId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "organisational_unit_id")
    private Long organisationalUnitId;

    @Column(name = "asset_id")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganisationalUnitAssetId that = (OrganisationalUnitAssetId) o;
        return Objects.equals(organisationalUnitId, that.organisationalUnitId) && Objects.equals(assetId, that.assetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organisationalUnitId, assetId);
    }
}
