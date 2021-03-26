package com.supplyr.supplyr.repository;

import com.supplyr.supplyr.model.OrganisationalUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;


public interface OrganisationalUnitRepository extends JpaRepository<OrganisationalUnit, Long> {

    @Query("SELECT s FROM OrganisationalUnit s WHERE s.name = ?1")
    Optional<OrganisationalUnit> findByUnitName(String name);

}
