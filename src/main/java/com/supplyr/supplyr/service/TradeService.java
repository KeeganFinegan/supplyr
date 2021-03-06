package com.supplyr.supplyr.service;

import com.supplyr.supplyr.domain.Asset;
import com.supplyr.supplyr.domain.Offer;
import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.Trade;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class TradeService {


    private final TradeRepository tradeRepository;

    private final OrganisationalUnitService organisationalUnitService;

    private final AssetService assetService;

    @Autowired
    public TradeService(TradeRepository tradeRepository, OrganisationalUnitService organisationalUnitService, AssetService assetService) {
        this.tradeRepository = tradeRepository;
        this.organisationalUnitService = organisationalUnitService;
        this.assetService = assetService;
    }

    /**
     * Add new trade into database
     *
     * @param offerToBeTraded    Offer involved in the trade
     * @param quantityToBeTraded quantity of that offer that is being traded
     * @throws BadRequestException If the trade has a negative quantity
     */
    public Trade addTrade(Offer offerToBeTraded, double quantityToBeTraded) {

        if (quantityToBeTraded < 0 || offerToBeTraded.getQuantity() < 0) {
            throw new BadRequestException("A trade cannot have a negative quantity");
        }
        Trade trade = new Trade();
        trade.setAsset(offerToBeTraded.getAsset());
        trade.setQuantity(quantityToBeTraded);
        trade.setFulfilled(true);
        trade.setPrice(offerToBeTraded.getPrice());
        trade.setTimestamp(LocalDateTime.now());
        trade.setOrganisationalUnit(offerToBeTraded.getOrganisationalUnit());
        trade.setType(offerToBeTraded.getType());

        return tradeRepository.save(trade);


    }

    /**
     * Get a list of trades for an Organisational Unit
     *
     * @param organisationalUnitName Name of Organisational Unit to get trades for
     * @return a list of trades for a particular Organisational Unit
     * @throws NotFoundException when there are no trades for that unit or the unit doesn't exist
     */
    public List<Trade> getTradesByUnit(String organisationalUnitName) {
        try {
            OrganisationalUnit organisationalUnit = organisationalUnitService
                    .getOrganisationalUnitByName(organisationalUnitName);
            Optional<List<Trade>> optional = tradeRepository.findTradesByOrganisationalUnit(organisationalUnit);

            if (optional.isPresent()) {
                return tradeRepository.findTradesByOrganisationalUnit(organisationalUnit).get();
            } else {
                throw new NotFoundException("No trades");
            }


        } catch (Exception e) {
            throw new NotFoundException("Could not find organisational unit " + organisationalUnitName);
        }


    }

    /**
     * Get a list of trades trades for a particular Asset
     *
     * @param assetName Asset to receive trades for
     * @return List of trades for an Asset
     * @throws NotFoundException When there are no trades or the Asset does not exist
     */
    public List<Trade> getAssetTrades(String assetName) {
        try {
            Asset asset = assetService.getAssetByName(assetName);
            Optional<List<Trade>> optionalTrades = tradeRepository.findTradesByAsset(asset);
            if (optionalTrades.isPresent()) {
                return optionalTrades.get();
            } else {
                throw new NotFoundException("No trades of asset type " + assetName);
            }

        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }

    }
}
