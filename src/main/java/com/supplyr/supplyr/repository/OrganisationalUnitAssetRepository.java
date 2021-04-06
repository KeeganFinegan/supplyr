package com.supplyr.supplyr.repository;

import com.supplyr.supplyr.model.Asset;
import com.supplyr.supplyr.model.OrganisationalUnit;
import com.supplyr.supplyr.model.OrganisationalUnitAsset;
import com.supplyr.supplyr.model.OrganisationalUnitAssetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OrganisationalUnitAssetRepository extends JpaRepository<OrganisationalUnitAsset,
        OrganisationalUnitAssetId> {

    Optional<OrganisationalUnitAsset> findByOrganisationalUnitAndAsset(
            OrganisationalUnit organisationalUnit, Asset asset
    );



}
