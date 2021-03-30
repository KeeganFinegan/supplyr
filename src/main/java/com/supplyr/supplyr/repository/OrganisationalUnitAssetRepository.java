package com.supplyr.supplyr.repository;

import com.supplyr.supplyr.model.OrganisationalUnitAsset;
import com.supplyr.supplyr.model.OrganisationalUnitAssetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrganisationalUnitAssetRepository extends JpaRepository<OrganisationalUnitAsset, OrganisationalUnitAssetId> {



}
