package com.supplyr.supplyr.repository;

import com.supplyr.supplyr.domain.Asset;
import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.OrganisationalUnitAsset;
import com.supplyr.supplyr.domain.OrganisationalUnitAssetId;
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
