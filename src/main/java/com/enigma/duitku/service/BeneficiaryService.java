package com.enigma.duitku.service;

import com.enigma.duitku.exception.BeneficiaryException;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AuthRequest;
import com.enigma.duitku.model.request.BeneficiaryRequest;
import com.enigma.duitku.model.response.BeneficiaryResponse;
import org.springframework.data.domain.Page;

import javax.security.auth.login.LoginException;
import java.util.List;

public interface BeneficiaryService {

    BeneficiaryResponse addBeneficiary(BeneficiaryRequest request, String token);

    Page<BeneficiaryResponse> viewAllBeneficiaries(Integer page, Integer size, String token) throws UserException;

    Page<BeneficiaryResponse> viewAllBeneficiariesAdmin(Integer page, Integer size) throws UserException;

    String deleteByMobileNumber(String beneficiaryMobileNumber, String token) throws BeneficiaryException, LoginException;
}
