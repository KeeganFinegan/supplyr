package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.model.Offer;
import com.supplyr.supplyr.model.OfferRequest;
import com.supplyr.supplyr.service.OfferService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Offer addSellOffer(@RequestBody OfferRequest offerRequest){
        return offerService.addSellOffer(offerRequest);

    }

    /**
     * Add a new SELL offer
     */
    @PostMapping("/buy")
    public Offer addBuyOffer(@RequestBody OfferRequest offerRequest){
        return offerService.addBuyOffer(offerRequest);

    }

}
