package com.supplyr.supplyr.service;

import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.InsufficientResourcesException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.exception.UnauthorizedException;
import com.supplyr.supplyr.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class OfferService {

    private final OfferRepository offerRepository;

    private final AssetService assetService;

    private final AssetRepository assetRepository;

    private final UserRepository userRepository;

    private final OrganisationalUnitAssetService organisationalUnitAssetService;

    private final OrganisationalUnitRepository organisationalUnitRepository;

    private final OrganisationalUnitService organisationalUnitService;

    private final OrganisationalUnitAssetRepository organisationalUnitAssetRepository;

    private final SecurityContextService securityContextService;
    private HashMap<Long, OfferBook> offerBooks;

    @Autowired
    public OfferService(OfferRepository offerRepository,
                        AssetService assetService,
                        AssetRepository assetRepository, UserRepository userRepository,
                        OrganisationalUnitAssetService organisationalUnitAssetService, OrganisationalUnitRepository organisationalUnitRepository, OrganisationalUnitService organisationalUnitService, OrganisationalUnitAssetRepository organisationalUnitAssetRepository, SecurityContextService securityContextService) {

        this.offerRepository = offerRepository;
        this.assetService = assetService;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.organisationalUnitAssetService = organisationalUnitAssetService;
        this.organisationalUnitRepository = organisationalUnitRepository;
        this.organisationalUnitService = organisationalUnitService;
        this.organisationalUnitAssetRepository = organisationalUnitAssetRepository;
        this.securityContextService = securityContextService;
    }

    /**
     * Returns the offerBooks hashmap
     *
     * @return offer books hashmap
     */
    public HashMap<Long, OfferBook> getOfferBooks() {
        if (offerBooks == null) {

            return new HashMap<Long, OfferBook>();
        }
        return offerBooks;
    }

    public void setOfferBooks(HashMap<Long, OfferBook> offerBooks) {
        this.offerBooks = offerBooks;
    }

    /**
     * Add new sell offer
     *
     * @param offerRequest Details of offer to be added
     * @return Offer that was added
     * @throws InsufficientResourcesException When organisation does not have enough credits or assets to complete offer
     */
    public Offer addSellOffer(OfferRequest offerRequest) {
        if (isValidOffer(offerRequest)) {

            if (isValidUser(offerRequest)) {

                Asset offerAsset = assetService.getAssetByName(offerRequest.getAsset());
                OrganisationalUnit offerOrganisationalUnit = organisationalUnitService
                        .getOrganisationalUnitByName(offerRequest.getOrganisationalUnit());

                double offerAssetQuantity = offerRequest.getQuantity();

                    OrganisationalUnitAsset offerOrganisationalUnitAsset = organisationalUnitAssetService
                            .getOrganisationalUnitAsset(offerOrganisationalUnit,offerAsset);

                    if (hasSufficientAssets(offerOrganisationalUnitAsset, offerAssetQuantity)) {

                        return saveOffer(offerRequest, offerOrganisationalUnit, offerAsset, OfferType.SELL);
                    }
                    throw new BadRequestException(String.format("Not enough %s to complete the offer request",
                           offerRequest.getAsset()));

            }
            throw new NotFoundException("Organisational Unit or Asset Type does not exist");
        } else {
            throw new BadRequestException("Invalid SELL Offer");
        }

    }

    /**
     * Add new buy offer
     *
     * @param offerRequest Details of offer to be added
     * @return Offer that was added
     * @throws InsufficientResourcesException When organisation does not have enough credits or assets to complete offer
     */
    public Offer addBuyOffer(OfferRequest offerRequest) {
        if (isValidOffer(offerRequest)) {


            if (isValidUser(offerRequest)) {

                Asset offerAsset = assetService.getAssetByName(offerRequest.getAsset());
                OrganisationalUnit offerOrganisationalUnit = organisationalUnitService
                        .getOrganisationalUnitByName(offerRequest.getOrganisationalUnit());

                double creditsNeeded = offerRequest.getPrice() * offerRequest.getQuantity();
                if (hasSufficientCredits(offerOrganisationalUnit, creditsNeeded)) {

                    return saveOffer(offerRequest, offerOrganisationalUnit, offerAsset, OfferType.BUY);
                }

                throw new BadRequestException(
                        "Insufficient funds to complete BUY offer");
            }
            throw new NotFoundException("Organisational Unit or Asset Type does not exist");
        } else {
            throw new BadRequestException("Invalid BUY Offer");
        }


    }

    /**
     * Update an offer quantity
     *
     * @param existingOfferId      Id of Asset to be retrieved from database
     * @param updatedOfferQuantity New quantity of offer
     * @return Asset that was queried
     * @throws NotFoundException When existing offer does not exist
     */
    public Offer updateOffer(Long existingOfferId, double updatedOfferQuantity) {

        return offerRepository.findById(existingOfferId)
                .map(offer -> {
                    offer.setQuantity(updatedOfferQuantity);
                    return offerRepository.save(offer);
                }).orElseThrow(() -> new NotFoundException("Could not find existing offer"));

    }

    private OfferBook getOfferBook(Offer offer, HashMap<Long, OfferBook> currentOfferBook) {

        if (!currentOfferBook.containsKey(offer.getAsset().getAssetId())) {

            currentOfferBook.put(offer.getAsset().getAssetId(),
                    new OfferBook(offer.getAsset().getAssetId()));
        }
        return currentOfferBook.get(offer.getAsset().getAssetId());

    }

    private Offer saveOffer(OfferRequest offerRequest,
                            OrganisationalUnit organisationalUnit,
                            Asset asset,
                            OfferType offerType) {

        Offer offer = new Offer();
        offer.setOrganisationalUnit(organisationalUnit);
        offer.setQuantity(offerRequest.getQuantity());
        offer.setType(offerType);
        offer.setAsset(asset);
        offer.setTimestamp(LocalDateTime.now());
        offer.setPrice(offerRequest.getPrice());

        offerRepository.save(offer);

        addOfferToOfferBook(offer);
        return offer;

    }

    /**
     * Add an offer to the offer book
     *
     * @param offer Offer to be added
     */
    public void addOfferToOfferBook(Offer offer) {

        HashMap<Long, OfferBook> currentOfferBooks = getOfferBooks();
        OfferBook offerBook = getOfferBook(offer, currentOfferBooks);
        offerBook.addOfferHelper(new Offer(
                offer.getId(),
                offer.getOrganisationalUnit(),
                offer.getAsset(),
                offer.getQuantity(),
                offer.getType(),
                offer.getPrice(),
                offer.getTimestamp()
        ));

        setOfferBooks(currentOfferBooks);

    }


    private boolean isValidUser(OfferRequest offerRequest) {
        // TODO set back to original function
        // Get the current user from security context holder
        String currentUser = securityContextService.getCurrentUser();
        Optional<User> optionalUser = userRepository.findByUsername(currentUser);

        if (optionalUser.isPresent()) {

            String organisationalUnitNameFromOffer = offerRequest.getOrganisationalUnit();

            String userOrganisationalUnit = optionalUser.get().getOrganisationalUnit().getName();

            if (userOrganisationalUnit.equals(organisationalUnitNameFromOffer)) {
                return true;
            } else {
                throw new UnauthorizedException(String.format("Access to Organisational Unit %s not permitted",
                        organisationalUnitNameFromOffer));
            }
        }
        throw new NotFoundException("User not found");

    }

    private boolean hasSufficientCredits(OrganisationalUnit organisationalUnit, double creditsNeeded) {

        Optional<Integer> optionalCreditsOnOffer = offerRepository.sumCreditsOnOffer(organisationalUnit.getName());
        int creditsOnOffer = 0;

        if (optionalCreditsOnOffer.isPresent()) {
            creditsOnOffer = optionalCreditsOnOffer.get();


        }
        return !(organisationalUnit.getCredits() - creditsOnOffer - creditsNeeded < 0);

    }

    private boolean hasSufficientAssets(OrganisationalUnitAsset organisationalUnitAsset, double assetQuantityNeeded) {

        Optional<Integer> optionalAssetsOnOffer = offerRepository
                .sumAssetsOnOffer(organisationalUnitAsset.getAsset().getName());

        int assetsOnOffer = 0;

        if (optionalAssetsOnOffer.isPresent()) {
            assetsOnOffer = optionalAssetsOnOffer.get();
        }

        return !(organisationalUnitAsset.getQuantity() - assetsOnOffer - assetQuantityNeeded < 0);

    }


    /**
     * Loads Offers from database into Offer Book
     */
    public void initiateOfferQueue() {
        List<Offer> offerList = offerRepository.findAll();

        if (!offerList.isEmpty()) {

            for (Offer offer : offerList) {

                addOfferToOfferBook(offer);
            }
        }

    }

    /**
     * Delete an Offer with a given id
     *
     * @param offerToBeDeleted Id of offer to be deleted
     */
    public void deleteOfferById(Long offerToBeDeleted) {
        if (offerRepository.existsById(offerToBeDeleted)) {

            Optional<Offer> optionalOffer = offerRepository.findById(offerToBeDeleted);
            if (optionalOffer.isPresent()) {
                HashMap<Long, OfferBook> currentOfferBooks = getOfferBooks();
                OfferBook offerBook = getOfferBook(optionalOffer.get(), currentOfferBooks);
                offerBook.removeExistingOffer(offerToBeDeleted);
                offerRepository.deleteById(offerToBeDeleted);

            }

        } else {
            throw new NotFoundException(String.format("Could not find offer with id %d", offerToBeDeleted));
        }

    }

    private boolean isValidOffer(OfferRequest offerRequest) {
        return !(offerRequest.getPrice() < 1) && !(offerRequest.getQuantity() < 1);

    }

    // Get the current lowest ask
    public List<Double> getLowestAskAndHighestBid(String asset) {

        Optional<List<Offer>> offerList = offerRepository.findOffersByAsset_Name(asset);

        if (offerList.isPresent()) {

            double lowestAsk = Double.MAX_VALUE;
            double highestBid = -1;
            for (Offer offer : offerList.get()) {
                if (offer.getType().equals(OfferType.SELL)) {
                    if (offer.getPrice() < lowestAsk) {
                        lowestAsk = offer.getPrice();
                    }

                } else if (offer.getType().equals(OfferType.BUY)) {
                    if (offer.getPrice() > highestBid) {
                        highestBid = offer.getPrice();
                    }

                }
            }

            if (lowestAsk == Double.MAX_VALUE) {
                lowestAsk = -1;
            }
            return Arrays.asList(lowestAsk, highestBid);
        }
        throw new NotFoundException("No current offers for this asset");

    }


}
