package com.supplyr.supplyr.startup;

import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.User;
import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.service.OfferService;
import com.supplyr.supplyr.service.OrganisationalUnitService;
import com.supplyr.supplyr.service.SupplyrUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListener {


    private final OfferService offerService;


    private final SupplyrUserDetailsService supplyrUserDetailsService;


    private final OrganisationalUnitService organisationalUnitService;

    @Autowired
    public StartupApplicationListener(OfferService offerService, SupplyrUserDetailsService supplyrUserDetailsService, OrganisationalUnitService organisationalUnitService) {
        this.offerService = offerService;
        this.supplyrUserDetailsService = supplyrUserDetailsService;
        this.organisationalUnitService = organisationalUnitService;
    }

    /**
     * Tasks to be performed on application start up
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {

        offerService.initiateOfferQueue();

        try {
            organisationalUnitService.createOrganisationalUnit(new OrganisationalUnit("Supplyr Admin", 100));

            supplyrUserDetailsService.registerNewAdmin(new User("admin", "12345"));

        } catch (AlreadyExistsException e) {
            System.out.println("LOADED DATA FROM DATABASE");
        }


    }

}
