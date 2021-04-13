package com.supplyr.supplyr.startup;

import com.supplyr.supplyr.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListener {

    @Autowired
    OfferService offerService;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        offerService.initiateOfferQueue();

    }

}
