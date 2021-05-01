package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.domain.Offer;
import com.supplyr.supplyr.domain.OfferRequest;
import com.supplyr.supplyr.service.OfferService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/offers")
public class OfferController {


    OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    /**
     * Add a new SELL offer
     */
    @PostMapping("/sell")
    public Offer addSellOffer(@RequestBody OfferRequest offerRequest) {
        //TODO: check for negative price and quantity
        return offerService.addSellOffer(offerRequest);

    }

    /**
     * Add a new SELL offer
     */
    @PostMapping("/buy")
    public Offer addBuyOffer(@RequestBody OfferRequest offerRequest) {
        //TODO: check for negative price and quantity
        return offerService.addBuyOffer(offerRequest);

    }

    @PostMapping("/delete/{offerId}")
    public void deleteOffer(@PathVariable Long offerId){
        offerService.deleteOfferById(offerId);
        }


}
