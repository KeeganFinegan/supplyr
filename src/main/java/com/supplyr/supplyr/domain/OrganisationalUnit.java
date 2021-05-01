package com.supplyr.supplyr.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;

@Table(name = "organisational_unit")
@Entity
public class OrganisationalUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    private double credits;

    @OneToMany(mappedBy = "organisationalUnit", targetEntity = User.class,
            cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("organisationalUnit")
    private List<User> users;

    @OneToMany(mappedBy = "organisationalUnit", targetEntity = OrganisationalUnitAsset.class)
    @JsonIgnoreProperties("organisationalUnit")
    private List<OrganisationalUnitAsset> organisationalUnitAssets;

    @OneToMany(mappedBy = "organisationalUnit", targetEntity = Offer.class)
    @JsonIgnoreProperties("organisationalUnit")
    private List<Offer> offers;

    public OrganisationalUnit() {

    }

    public OrganisationalUnit(String name, double credits) {
        this.name = name;
        this.credits = credits;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<OrganisationalUnitAsset> getOrganisationalUnitAssets() {
        return organisationalUnitAssets;
    }

    public void setOrganisationalUnitAssets(List<OrganisationalUnitAsset> organisationalUnitAssets) {
        this.organisationalUnitAssets = organisationalUnitAssets;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
}
