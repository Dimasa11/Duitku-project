package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Beneficiary;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.BeneficiaryException;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.BeneficiaryRequest;
import com.enigma.duitku.model.response.BeneficiaryResponse;
import com.enigma.duitku.repository.BeneficiaryRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.repository.WalletRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.BeneficiaryService;
import com.enigma.duitku.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final UserService userService;

    private final WalletRepository walletRepository;

    private final BeneficiaryRepository beneficiaryRepository;

    private final JwtUtils jwtUtils;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public BeneficiaryResponse addBeneficiary(BeneficiaryRequest request, String token) {

        // TODO 1: Validate user
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if (user != null) {

            // TODO 2: Retrieve wallet and beneficiaries
            Wallet wallet = user.getWallet();
            List<Beneficiary> beneficiaryList = wallet.getListOfBeneficiaries();

            Beneficiary databaseBeneficiary = null;

            // TODO 3: Check for existing beneficiary using iterator
            Iterator<Beneficiary> iterator = beneficiaryList.iterator();
            while (iterator.hasNext()) {
                Beneficiary b = iterator.next();
                if (Objects.equals(b.getMobileNumber(), request.getMobileNumber())) {
                    databaseBeneficiary = b;
                    break;
                }
            }

            Beneficiary existingBeneficiary = beneficiaryRepository.findByAccountNo(request.getAccountNo());

            // TODO 4: Add beneficiary if not exist
            if (existingBeneficiary == null) {

                // TODO 5: Data does not exist or status is not deleted, add new Beneficiary
                Beneficiary newBeneficiary = new Beneficiary();
                newBeneficiary.setMobileNumber(request.getMobileNumber());
                newBeneficiary.setName(request.getName());
                newBeneficiary.setBankName(request.getBankName());
                newBeneficiary.setAccountNo(request.getAccountNo());
                newBeneficiary.setStatus("Active");

                // TODO 6: Save the newBeneficiary first
                newBeneficiary = beneficiaryRepository.save(newBeneficiary);

                // TODO 7 : Add the newBeneficiary to the list of beneficiaries
                beneficiaryList.add(newBeneficiary);
                wallet.setListOfBeneficiaries(beneficiaryList);

                // TODO 8 : Save the wallet with the updated list of beneficiaries
                walletRepository.save(wallet);
            } else  {

                // TODO 9 : Data exists and status is deleted, update the status to Active
                existingBeneficiary.setMobileNumber(request.getMobileNumber());
                existingBeneficiary.setName(request.getName());
                existingBeneficiary.setBankName(request.getBankName());
                existingBeneficiary.setStatus("Active");
            }

            // TODO 10: Return BeneficiaryResponse
            return BeneficiaryResponse.builder()
                    .mobileNumber(request.getMobileNumber())
                    .bankName(request.getBankName())
                    .name(request.getName())
                    .accountNo(request.getAccountNo())
                    .build();
        } else {
            return BeneficiaryResponse.builder()
                    .errors("Mobile Number Not Registered!")
                    .build();
        }
    }

    public List<Beneficiary> getAllActiveBeneficiaries(User user) {
        List<Beneficiary> allBeneficiaries = user.getWallet().getListOfBeneficiaries();
        List<Beneficiary> activeBeneficiaries = new ArrayList<>();

        for (Beneficiary beneficiary : allBeneficiaries) {
            if ("Active".equals(beneficiary.getStatus())) {
                activeBeneficiaries.add(beneficiary);
            }
        }

        return activeBeneficiaries;
    }

    @Override
    public Page<BeneficiaryResponse> viewAllBeneficiaries(Integer page, Integer size, String token) throws UserException {

        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);
        if(user != null) {

            List<Beneficiary> activeBeneficiaries = getAllActiveBeneficiaries(user);

            int start = page * size;
            int end = Math.min((start + size), activeBeneficiaries.size());
            Page<Beneficiary> pageBeneficiaries = new PageImpl<>(activeBeneficiaries.subList(start, end), PageRequest.of(page, size), activeBeneficiaries.size());

            List<BeneficiaryResponse> beneficiaryResponses = new ArrayList<>();
            for (Beneficiary beneficiary : pageBeneficiaries.getContent()) {
                BeneficiaryResponse beneficiaryResponse = BeneficiaryResponse.builder()
                        .mobileNumber(beneficiary.getMobileNumber())
                        .accountNo(beneficiary.getAccountNo())
                        .name(beneficiary.getName())
                        .bankName(beneficiary.getBankName())
                        .build();
                beneficiaryResponses.add(beneficiaryResponse);
            }

            return new PageImpl<>(beneficiaryResponses, pageBeneficiaries.getPageable(), pageBeneficiaries.getTotalElements());
        } else {
            throw new UserException("Please Login In!");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public Page<BeneficiaryResponse> viewAllBeneficiariesAdmin(Integer page, Integer size) throws UserException {

            Pageable pageable = PageRequest.of(page, size);
            Page<Beneficiary> beneficiaries = beneficiaryRepository.findAll(pageable);
            List<BeneficiaryResponse> allBeneficiaries = new ArrayList<>();
            for (Beneficiary beneficiary : beneficiaries.getContent()) {
                BeneficiaryResponse beneficiaryResponse = BeneficiaryResponse.builder()
                        .mobileNumber(beneficiary.getMobileNumber())
                        .accountNo(beneficiary.getAccountNo())
                        .name(beneficiary.getName())
                        .bankName(beneficiary.getBankName())
                        .build();
                allBeneficiaries.add(beneficiaryResponse);
            }
        return new PageImpl<>(allBeneficiaries, pageable, beneficiaries.getTotalElements());
    }

    @Override
    public String deleteByMobileNumber(String beneficiaryMobileNumber, String token) throws BeneficiaryException, LoginException {

        try {
            // TODO 1: Validate user
            String loggedInUserId = jwtUtils.extractUserId(token);
            User user = userService.getById(loggedInUserId);

            if (user != null) {
                // TODO 2 : Retrieve the user's wallet and list of beneficiaries
                Wallet wallet = user.getWallet();
                List<Beneficiary> listofbeneficiaries = wallet.getListOfBeneficiaries();

                // TODO 3 : Check if the list of beneficiaries is not empty
                if (!listofbeneficiaries.isEmpty()) {
                    Beneficiary targetBeneficiary = null;
                    Iterator<Beneficiary> iterator = listofbeneficiaries.iterator();

                    // TODO 4 : Iterate through the list of beneficiaries
                    while (iterator.hasNext()) {
                        Beneficiary b = iterator.next();

                        // TODO 5 : Check if the mobile number matches
                        if (Objects.equals(b.getMobileNumber(), beneficiaryMobileNumber)) {
                            targetBeneficiary = b;
                            break;
                        }
                    }

                    // TODO 6 : Check if a matching beneficiary is found
                    if (targetBeneficiary != null) {

                        // TODO 7 : Set the status to "deleted" and delete the beneficiary
                        targetBeneficiary.setStatus("deleted");
                        beneficiaryRepository.delete(targetBeneficiary);
                        return "Beneficiary has been Successfully Deleted!";

                    } else {
                        // TODO 8 : No matching beneficiary found, throw an exception
                        throw new BeneficiaryException("No Registered Beneficiary Found with this Mobile Number: " + beneficiaryMobileNumber);
                    }

                } else {
                    throw new BeneficiaryException("No Registered Beneficiary Found with this Mobile Number: " + beneficiaryMobileNumber);
                }

            } else {
                throw new LoginException("Please log in!");
            }

        } catch (Exception e) {
            throw new BeneficiaryException("Error deleting Beneficiary: " + e.getMessage());
        }
    }
}
