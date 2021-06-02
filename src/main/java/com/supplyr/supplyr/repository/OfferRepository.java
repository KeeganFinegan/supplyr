package com.supplyr.supplyr.repository;

import com.supplyr.supplyr.domain.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Query("SELECT s FROM Offer s WHERE s.asset.name = ?1")
    Optional<List<Offer>> findOffersByAsset_Name(String assetName);

    @Query("SELECT SUM(o.price * o.quantity) FROM Offer o WHERE o.organisationalUnit.name = ?1")
    Optional<Integer> sumCreditsOnOffer(String organisationalUnitName);

    @Query("SELECT SUM(o.quantity) FROM Offer o WHERE o.asset.name =?1")
    Optional<Integer> sumAssetsOnOffer(String assetName);

}
