package com.supplyr.supplyr.service;

import com.supplyr.supplyr.exception.InsufficientResourcesException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.model.*;
import com.supplyr.supplyr.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OfferService {

    @Autowired
    OfferRepository offerRepository;
    @Autowired
    OrganisationalUnitAssetRepository organisationalUnitAssetRepository;
    @Autowired
    OrganisationalUnitRepository organisationalUnitRepository;
    @Autowired
    AssetRepository assetRepository;
    @Autowired
    UserRepository userRepository;

    public Offer addSellOffer(OfferRequest offerRequest) {
        Optional<OrganisationalUnit> optionalOrganisationalUnit = organisationalUnitRepository
                .findById(offerRequest.getOrganisationalUnitId());
        Optional<Asset> optionalAsset = assetRepository
                .findById(offerRequest.getAssetId());

        if (optionalAsset.isPresent() && optionalOrganisationalUnit.isPresent()) {
            Optional<OrganisationalUnitAsset> optionalOrganisationalUnitAsset = organisationalUnitAssetRepository
                    .findByOrganisationalUnitAndAsset(optionalOrganisationalUnit.get(), optionalAsset.get());

            // Attempt to retrieve organisational unit asset from database
            double orderQuantity = offerRequest.getQuantity();

            if (optionalOrganisationalUnitAsset.isPresent()) {
                if (optionalOrganisationalUnitAsset.get().getQuantity() >= orderQuantity) {

                    Offer offer = new Offer();
                    offer.setOrganisationalUnit(optionalOrganisationalUnit.get());
                    offer.setQuantity(orderQuantity);
                    offer.setSell(true);
                    offer.setBuy(false);
                    offer.setAsset(optionalAsset.get());
                    offer.setTimestamp(LocalDateTime.now());
                    offer.setPrice(offerRequest.getPrice());
                    return offerRepository.save(offer);
                }
                throw new InsufficientResourcesException(String.format("Not enough %s to complete the offer request",
                        optionalAsset.get().getName()));

            }
            throw new NotFoundException(String.format("Organisational Unit %s does not possess asset with id %d",
                    optionalOrganisationalUnit.get().getName(), offerRequest.getAssetId()));

        }
        throw new NotFoundException("Organisational Unit or Asset Type does not exist");
    }

    public Offer addBuyOffer(OfferRequest offerRequest) {
        Optional<OrganisationalUnit> optionalOrganisationalUnit = organisationalUnitRepository
                .findById(offerRequest.getOrganisationalUnitId());
        Optional<Asset> optionalAsset = assetRepository
                .findById(offerRequest.getAssetId());

        if (optionalOrganisationalUnit.isPresent() && optionalAsset.isPresent()) {
            double totalFundsRequired = offerRequest.getPrice() * offerRequest.getQuantity();
            if (optionalOrganisationalUnit.get().getCredits() >= totalFundsRequired) {
                Offer offer = new Offer();
                offer.setOrganisationalUnit(optionalOrganisationalUnit.get());
                offer.setQuantity(offerRequest.getQuantity());
                offer.setSell(false);
                offer.setBuy(true);
                offer.setAsset(optionalAsset.get());
                offer.setTimestamp(LocalDateTime.now());
                offer.setPrice(offerRequest.getPrice());
                return offerRepository.save(offer);
            }
            throw new InsufficientResourcesException(String
                    .format("Organisational Unit %s does not have sufficient funds",
                            optionalOrganisationalUnit.get().getName()));
        }
        throw new NotFoundException("Organisational Unit or Asset Type does not exist");

    }
}
