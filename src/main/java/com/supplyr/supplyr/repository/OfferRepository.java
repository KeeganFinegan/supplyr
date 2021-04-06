package com.supplyr.supplyr.repository;

import com.supplyr.supplyr.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer,Long> {
}
