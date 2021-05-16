package com.supplyr.supplyr.service;

import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.exception.InsufficientResourcesException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

    private HashMap<Long, OfferBook> offerBooks;

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
        Optional<OrganisationalUnit> optionalOrganisationalUnit = organisationalUnitRepository
                .findByUnitName(offerRequest.getOrganisationalUnit());

        Optional<Asset> optionalAsset = assetRepository
                .findByName(offerRequest.getAsset());

        if (optionalAsset.isPresent() && optionalOrganisationalUnit.isPresent() && isValidUser(offerRequest)) {

            Asset offerAsset = optionalAsset.get();
            OrganisationalUnit offerOrganisationalUnit = optionalOrganisationalUnit.get();

            Optional<OrganisationalUnitAsset> optionalOrganisationalUnitAsset = organisationalUnitAssetRepository
                    .findByOrganisationalUnitAndAsset(offerOrganisationalUnit, offerAsset);

            double offerAssetQuantity = offerRequest.getQuantity();

            if (optionalOrganisationalUnitAsset.isPresent()) {
                OrganisationalUnitAsset offerOrganisationalUnitAsset = optionalOrganisationalUnitAsset.get();
                if (hasSufficientAssets(offerOrganisationalUnitAsset, offerAssetQuantity)) {

                    return saveOffer(offerRequest, offerOrganisationalUnit, offerAsset, OfferType.SELL);
                }
                throw new InsufficientResourcesException(String.format("Not enough %s to complete the offer request",
                        optionalAsset.get().getName()));

            }
            throw new NotFoundException(String.format("Organisational Unit %s does not possess %s",
                    optionalOrganisationalUnit.get().getName(), offerRequest.getAsset()));

        }
        throw new NotFoundException("Organisational Unit or Asset Type does not exist");
    }

    /**
     * Add new buy offer
     *
     * @param offerRequest Details of offer to be added
     * @return Offer that was added
     * @throws InsufficientResourcesException When organisation does not have enough credits or assets to complete offer
     */
    public Offer addBuyOffer(OfferRequest offerRequest) {
        Optional<OrganisationalUnit> optionalOrganisationalUnit = organisationalUnitRepository
                .findByUnitName(offerRequest.getOrganisationalUnit());
        Optional<Asset> optionalAsset = assetRepository
                .findByName(offerRequest.getAsset());

        if (optionalOrganisationalUnit.isPresent() && optionalAsset.isPresent() && isValidUser(offerRequest)) {

            Asset offerAsset = optionalAsset.get();
            OrganisationalUnit offerOrganisationalUnit = optionalOrganisationalUnit.get();

            double creditsNeeded = offerRequest.getPrice() * offerRequest.getQuantity();
            if (hasSufficientCredits(offerOrganisationalUnit, creditsNeeded)) {

                return saveOffer(offerRequest, offerOrganisationalUnit, offerAsset, OfferType.BUY);
            }
            throw new InsufficientResourcesException(String
                    .format("Organisational Unit '%s' does not have sufficient funds",
                            optionalOrganisationalUnit.get().getName()));
        }
        throw new NotFoundException("Organisational Unit or Asset Type does not exist");

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
        System.out.println("OFFER UPDATED");
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
//        // Get the current user from security context holder
//        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
//        Optional<User> optionalUser = userRepository.findByUsername(currentUser);
//
//        if (optionalUser.isPresent()) {
//
//            Long organisationalUnitIdFromOffer = offerRequest.getOrganisationalUnitId();
//
//            Long organisationalUnitIdForCurrentUser = optionalUser.get().getOrganisationalUnit().getId();
//
//            if (organisationalUnitIdFromOffer.equals(organisationalUnitIdForCurrentUser)) {
//                return true;
//            } else {
//                throw new AccessDeniedException(String.format("Access to Organisational Unit %d not permitted",
//                        organisationalUnitIdFromOffer));
//            }
//        }
//        throw new NotFoundException("Username not found");
        return true;
    }


    private boolean hasSufficientCredits(OrganisationalUnit organisationalUnit, double creditsNeeded) {

        return organisationalUnit.getCredits() >= creditsNeeded;

    }

    private boolean hasSufficientAssets(OrganisationalUnitAsset organisationalUnitAsset, double assetQuantityNeeded) {

        return organisationalUnitAsset.getQuantity() > assetQuantityNeeded;

    }

    @Scheduled(fixedDelay = 5000)
    public void printOfferBook() {
        Optional<Asset> optionalAsset = assetRepository.findById(1L);

        if (optionalAsset.isPresent()) {
            try {
                HashMap<Long, OfferBook> currentOrderBook = getOfferBooks();

                OfferBook ob = currentOrderBook.get(optionalAsset.get().getAssetId());
                PriorityQueue<Offer> buyOffers = ob.getBuyOffers();
                PriorityQueue<Offer> sellOffers = ob.getSellOffers();
                Map<Long, Offer> fulfilledOffers = ob.getFilledOffers();
                System.out.println("BUY BOOK");
                System.out.println(buyOffers.toString());
                System.out.println(" ");
                System.out.println("SELL BOOK");
                System.out.println(sellOffers.toString());
                System.out.println(" ");
                System.out.println("FULFILLED BOOK");
                for (Map.Entry<Long, Offer> entry : fulfilledOffers.entrySet()) {
                    System.out.println(entry);
                }
                System.out.println(" ");

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        } else {
            System.out.println("NO OFFERS");
        }
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
}
