package com.supplyr.supplyr.startup;

import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.User;
import com.supplyr.supplyr.service.OfferService;
import com.supplyr.supplyr.service.OrganisationalUnitService;
import com.supplyr.supplyr.service.SupplyrUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListener {

    @Autowired
    OfferService offerService;

    @Autowired
    SupplyrUserDetailsService supplyrUserDetailsService;

    @Autowired
    OrganisationalUnitService organisationalUnitService;


    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        offerService.initiateOfferQueue();


        organisationalUnitService.createOrganisationalUnit(new OrganisationalUnit("Supplyr Admin", 100));


        supplyrUserDetailsService.registerNewAdmin(new User("admin", "12345"));


    }

}
