package com.supplyr.supplyr.domain;

import javax.persistence.*;
import java.util.List;

@Table(name = "asset")
@Entity
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assetId;

    private String name;

    @OneToMany(mappedBy = "asset", targetEntity = Offer.class)
    private List<Offer> offers;

    public Asset() {
    }


    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" +
                "assetId=" + assetId +
                ", name='" + name + '\'' +
                ", offers=" + offers.toString() +
                '}';
    }
}
