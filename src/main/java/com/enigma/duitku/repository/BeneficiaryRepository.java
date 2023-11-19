package com.enigma.duitku.repository;

import com.enigma.duitku.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, String> {
    Beneficiary findByAccountNo(String accountNo);

}

