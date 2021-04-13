package com.supplyr.supplyr.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "offer")
public class Offer implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "organisational_unit_id")
    private OrganisationalUnit organisationalUnit;

    @ManyToOne()
    @JoinColumn(name = "asset_id")
    private Asset asset;

    private double quantity;

    @Enumerated(EnumType.STRING)
    private OfferType type;

    private double price;

    private LocalDateTime timestamp;

    private boolean fulfilled;

    public Offer() {
    }

    public Offer(Long id,
                 OrganisationalUnit organisationalUnit,
                 double quantity,
                 OfferType type,
                 double price,
                 LocalDateTime timestamp) {
        this.id = id;
        this.organisationalUnit = organisationalUnit;
        this.quantity = quantity;
        this.type = type;
        this.price = price;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public OfferType getType() {
        return type;
    }

    public void setType(OfferType type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFulfilled() {
        return fulfilled;
    }

    public void setFulfilled(boolean fulfilled) {
        this.fulfilled = fulfilled;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", organisationalUnit=" + organisationalUnit +
                ", asset=" + asset +
                ", quantity=" + quantity +
                ", type=" + type +
                ", price=" + price +
                ", timestamp=" + timestamp +
                ", fulfilled=" + fulfilled +
                '}';
    }
}
