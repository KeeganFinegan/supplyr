package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.domain.Offer;
import com.supplyr.supplyr.domain.OfferRequest;
import com.supplyr.supplyr.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/offers")
public class OfferController {


    private final OfferService offerService;

    @Autowired
    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    /**
     * REST endpoint to add a new SELL offer
     * * @param offerRequest details of SELL offer to be added
     */
    @PostMapping("/sell")
    public Offer addSellOffer(@RequestBody OfferRequest offerRequest) {
        //TODO: check for negative price and quantity
        return offerService.addSellOffer(offerRequest);

    }

    /**
     * REST endpoint to add a new SELL offer
     *
     * @param offerRequest details of BUY offer to be added
     */
    @PostMapping("/buy")
    public Offer addBuyOffer(@RequestBody OfferRequest offerRequest) {
        //TODO: check for negative price and quantity
        return offerService.addBuyOffer(offerRequest);

    }

    /**
     * REST endpoint to delete an offer by id
     *
     * @param offerId Id of offer to be deleted
     */
    @PostMapping("/delete/{offerId}")
    public void deleteOffer(@PathVariable Long offerId) {
        offerService.deleteOfferById(offerId);
    }


}
