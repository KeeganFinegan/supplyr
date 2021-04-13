package com.supplyr.supplyr.repository;

import com.supplyr.supplyr.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query("SELECT s FROM Asset s WHERE s.name = ?1")
    Optional<Asset> findByName(String assetName);


}
