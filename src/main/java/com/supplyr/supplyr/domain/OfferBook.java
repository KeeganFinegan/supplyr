package com.supplyr.supplyr.domain;

import com.supplyr.supplyr.service.OfferService;
import com.supplyr.supplyr.utility.BeanUtility;

import java.util.*;

public class OfferBook {

    private Long assetId;

    // Stores BUY offers that are sorted buy price and then timestamp
    private PriorityQueue<Offer> buyOffers;

    // Stores SELL offers that are sorted buy price and then timestamp
    private PriorityQueue<Offer> sellOffers;
    private Map<Long, Offer> filledOffers;
    private Map<Long, Offer> offerMap;

    // Inject OfferService class into non-bean class
    OfferService offerService = BeanUtility.getBean(OfferService.class);

    /**
     * Custom constructor to override compare method for BUY and SELL priority queues
     */
    public OfferBook(Long assetId) {
        this.assetId = assetId;
        this.filledOffers = new HashMap<>();
        this.offerMap = new HashMap<>();

        this.buyOffers = new PriorityQueue<>(new Comparator<>() {
            /**
             *  BUY offers are sorted by timestamp in descending order
             */
            @Override
            public int compare(Offer o1, Offer o2) {

                if (o1.getPrice() < o2.getPrice()) {
                    return 1;
                } else if (o1.getPrice() > o2.getPrice()) {
                    return -1;
                } else {
                    if (o1.getTimestamp().isBefore(o2.getTimestamp())) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        });

        this.sellOffers = new PriorityQueue<>(new Comparator<>() {
            /**
             *  SELL offers are sorted by timestamp in descending order
             */
            @Override
            public int compare(Offer o1, Offer o2) {

                if (o1.getPrice() < o2.getPrice()) {
                    return 1;
                } else if (o1.getPrice() > o2.getPrice()) {
                    return -1;
                } else {
                    if (o1.getTimestamp().isBefore(o2.getTimestamp())) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        });

    }

    public Long getAsset() {
        return assetId;
    }

    public void setAsset(Long asset) {
        this.assetId = asset;
    }

    public PriorityQueue<Offer> getBuyOffers() {
        return buyOffers;
    }

    public void setBuyOffers(PriorityQueue<Offer> buyOffers) {
        this.buyOffers = buyOffers;
    }

    public PriorityQueue<Offer> getSellOffers() {
        return sellOffers;
    }

    public void setSellOffers(PriorityQueue<Offer> sellOffers) {
        this.sellOffers = sellOffers;
    }

    public Map<Long, Offer> getFilledOffers() {
        return filledOffers;
    }

    public void setFilledOffers(Map<Long, Offer> filledOffers) {
        this.filledOffers = filledOffers;
    }

    public Map<Long, Offer> getOfferMap() {
        return offerMap;
    }

    public void setOfferMap(Map<Long, Offer> offerMap) {
        this.offerMap = offerMap;
    }

    /**
     * Remove an offer from SELL/BUY queue and offer Map
     *
     * @param offerId offerId for the offer to be removed
     */
    public void removeExistingOffer(Long offerId) {

        if (offerMap.containsKey(offerId)) {
            if (offerMap.get(offerId).getType().equals(OfferType.BUY)) {
                buyOffers.remove(offerMap.get(offerId));
            } else {
                sellOffers.remove(offerMap.get(offerId));
            }
            System.out.println("DELETE OFFER " + offerId);
        }
        offerMap.remove(offerId);
    }

    /**
     * Helper method to add new BUY or SELL offer
     *
     * @param placedOffer new offer to be added
     */
    public void addOfferHelper(Offer placedOffer) {

        // Remove any existing entry of offer with same ID
        removeExistingOffer(placedOffer.getId());

        if (!filledOffers.containsKey(placedOffer.getId())) {
            // Process BUY offer
            if (placedOffer.getType().equals(OfferType.BUY)) {
                addOffer(placedOffer, sellOffers, buyOffers);
                offerMap.put(placedOffer.getId(), placedOffer);
                // Process SELL offer
            } else if (placedOffer.getType().equals(OfferType.SELL)) {
                addOffer(placedOffer, buyOffers, sellOffers);
                offerMap.put(placedOffer.getId(), placedOffer);
            }
        }
    }

    /**
     * Partially fill an offer
     *
     * @param offerToBeFilled offer to be partially fulfilled
     * @param orderQuantity   quantity to be partially fulfilled
     */
    public void fillPartialOrders(Offer offerToBeFilled, double orderQuantity) {

        if (filledOffers.containsKey(offerToBeFilled.getId())) {
            filledOffers.get(offerToBeFilled.getId()).setQuantity(filledOffers
                    .get(offerToBeFilled.getId()).getQuantity() + orderQuantity);
        } else {
            try {
                Offer partialPlacedOffer = (Offer) offerToBeFilled.clone();
                partialPlacedOffer.setQuantity(orderQuantity);
                filledOffers.put(offerToBeFilled.getId(), partialPlacedOffer);

            } catch (CloneNotSupportedException e) {

                System.out.println("FILL ERROR");
            }
        }
    }

    public void executeOfferFromQueue(Offer currentOfferFromQueue, double currentOfferFromQueueQuantity) {
        if (filledOffers.containsKey(currentOfferFromQueue.getId())) {
            filledOffers.get(currentOfferFromQueue.getId()).setQuantity(filledOffers
                    .get(currentOfferFromQueue.getId()).getQuantity() + currentOfferFromQueueQuantity);
        } else {
            filledOffers.put(currentOfferFromQueue.getId(), currentOfferFromQueue);
            offerService.deleteOfferById(currentOfferFromQueue.getId());

        }
    }

    public double executeOffer(Offer placedOffer, double placedOfferQuantity, Queue offersQueue, Offer currentOfferFromQueue,
                               double currentOfferFromQueueQuantity) {

        if (currentOfferFromQueueQuantity <= placedOfferQuantity) {
            executeOfferFromQueue(currentOfferFromQueue, currentOfferFromQueueQuantity);
            offersQueue.poll();
            fillPartialOrders(placedOffer, currentOfferFromQueueQuantity);
            placedOfferQuantity = placedOfferQuantity - currentOfferFromQueueQuantity;
        } else {
            double currentFilledSellOffer = currentOfferFromQueueQuantity - placedOfferQuantity;
            currentOfferFromQueue.setQuantity(currentFilledSellOffer);
            try {
                Offer partialPlacedOffer = (Offer) currentOfferFromQueue.clone();
                partialPlacedOffer.setQuantity(placedOfferQuantity);
                executeOfferFromQueue(partialPlacedOffer, placedOfferQuantity);
            } catch (CloneNotSupportedException e) {
                System.out.println("Execute Error");
            }
            fillPartialOrders(placedOffer, placedOfferQuantity);
            placedOfferQuantity = 0;
        }
        return placedOfferQuantity;

    }

    public void addOffer(Offer placedOffer, Queue<Offer> offersQueue, Queue<Offer> addPartialOffers) {
        double placedOfferQuantity = placedOffer.getQuantity();

        while (!offersQueue.isEmpty() && placedOfferQuantity != 0) {
            // Peek current offer from the opposite order type queue
            Offer currentOfferFromQueue = offersQueue.peek();
            // Determine if placed offer and queue offer are valid
            if (compatibleOffers(placedOffer, currentOfferFromQueue)) {
                double currentOfferFromQueueQuantity = currentOfferFromQueue.getQuantity();
                // Execute BUY offer if the price is accepted by queue offer
                if (placedOffer.getType().equals(OfferType.BUY)) {
                    if (placedOffer.getPrice() >= currentOfferFromQueue.getPrice()) {
                        placedOfferQuantity = executeOffer(placedOffer, placedOfferQuantity, offersQueue,
                                currentOfferFromQueue, currentOfferFromQueueQuantity);
                    } else {
                        break;
                    }
                    // Execute SELL offer if the price is accepted by queue offer
                } else {
                    if (placedOffer.getPrice() <= currentOfferFromQueue.getPrice()) {
                        placedOfferQuantity = executeOffer(placedOffer, placedOfferQuantity, offersQueue,
                                currentOfferFromQueue, currentOfferFromQueueQuantity);
                    } else {
                        break;
                    }
                }
            } else {
                break;
            }
        }
        // Add any remaining quantity to partial offers queue
        if (placedOfferQuantity > 0) {
            try {
                Offer partialRemainingOffer = (Offer) placedOffer.clone();
                partialRemainingOffer.setQuantity(placedOfferQuantity);
                addPartialOffers.add(partialRemainingOffer);
            } catch (CloneNotSupportedException e) {
                System.out.println("ADD ERROR");
            }
        }
    }

    /**
     * Determine if two orders can be executed
     *
     * @param offer1 first offer in potential trade
     * @param offer2 second offer in potential trade
     * @return True if offers are compatible or false if not
     */
    private boolean compatibleOffers(Offer offer1, Offer offer2) {
        if (offer1.getOrganisationalUnit().getId().equals(offer2.getOrganisationalUnit().getId())) {
            return false;
        } else {
            return true;
        }
    }

    private void executeTrade(Offer offerTraded) {


    }


    @Override
    public String toString() {
        return "OfferBook{" +
                "asset=" + assetId +
                ", buyOffers=" + buyOffers.toString() +
                ", sellOffers=" + sellOffers +
                ", filledOffers=" + filledOffers.toString() +
                ", offerMap=" + offerMap +
                '}';
    }
}
