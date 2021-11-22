package com.smartmarket.code.dao;

import com.smartmarket.code.model.PendingBICTransaction;
import com.smartmarket.code.model.TravelInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface TravelInsuranceRepository extends JpaRepository<TravelInsurance, String> {
    @Query(value = "SELECT * FROM travel_insurance WHERE id=:id and state ='Succeeded'", nativeQuery = true)
    public Optional<TravelInsurance> findById(@Param("id") String id);

//    @Query(value = "SELECT * FROM travel_insurance WHERE id=:id", nativeQuery = true)
//    public Optional<TravelInsurance> findById(@Param("id") String id);
}
