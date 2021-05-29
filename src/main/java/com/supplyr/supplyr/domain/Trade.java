package com.supplyr.supplyr.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organisational_unit_id")
    private OrganisationalUnit organisationalUnit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    private double quantity;

    @Enumerated(EnumType.STRING)
    private OfferType type;

    private double price;

    private LocalDateTime timestamp;

    private boolean fulfilled;

    public Trade() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Override
    public String toString() {
        return "Trade{" +
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
