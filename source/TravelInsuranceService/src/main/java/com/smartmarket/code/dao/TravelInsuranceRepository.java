package com.smartmarket.code.dao;

import com.smartmarket.code.model.TravelInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface TravelInsuranceRepository extends JpaRepository<TravelInsurance, String> {
}
