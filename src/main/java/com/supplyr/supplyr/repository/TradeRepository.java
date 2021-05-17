package com.supplyr.supplyr.repository;

import com.supplyr.supplyr.domain.Asset;
import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.OrganisationalUnitAsset;
import com.supplyr.supplyr.domain.Trade;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    Optional<List<Trade>> findTradesByOrganisationalUnit(
            OrganisationalUnit organisationalUnit
    );

    Optional<List<Trade>> findTradesByAsset(
            Asset asset
    );
}
