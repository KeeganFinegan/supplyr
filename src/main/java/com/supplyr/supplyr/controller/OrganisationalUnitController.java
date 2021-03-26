package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.model.OrganisationalUnit;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/organisational-unit")
public class OrganisationalUnitController {

    @Autowired
    OrganisationalUnitRepository organisationalUnitRepository;

    @PostMapping()
    public ResponseEntity<String> createOrganisationalUnit(@RequestBody OrganisationalUnit organisationalUnit){
        Optional<OrganisationalUnit> optUnit = organisationalUnitRepository
                .findByUnitName(organisationalUnit.getName());

        if(optUnit.isPresent()){
            return new ResponseEntity<>(
                    "Organisational unit already exists!",
                    HttpStatus.BAD_REQUEST
            );
        } else{
            organisationalUnitRepository.save(organisationalUnit);
            return new ResponseEntity<>(
                    "Organisational unit successfully created!",
                    HttpStatus.OK
            );
        }
    }

}
