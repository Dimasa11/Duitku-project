package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Beneficiary;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.BankAccountException;
import com.enigma.duitku.exception.BeneficiaryException;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.exception.WalletException;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.repository.BankAccountRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.repository.WalletRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.TransactionService;
import com.enigma.duitku.service.UserService;
import com.enigma.duitku.service.WalletService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Optional;

import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    void itShouldReturnTransactionWhenTransferMoneyToBeneficiary() throws BankAccountException, WalletException, BeneficiaryException, UserException {
        TransactionRequest request = new TransactionRequest();
        request.setBeneficiaryMobileNumber("beneficiaryMobileNumber");
        request.setAmount(100.0);
        request.setDescription("Test Description");
        request.setReceiver("Test Receiver");

        String token = "testToken";
        String loggedInUserId = "loggedInUserId";

        User user = new User();
        user.setMobileNumber(loggedInUserId);

        Wallet wallet = new Wallet();
        wallet.setListOfBeneficiaries(new ArrayList<>());
        wallet.setBalance(200.0);

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setMobileNumber("beneficiaryMobileNumber");

        wallet.getListOfBeneficiaries().add(beneficiary);

        when(jwtUtils.extractUserId(token)).thenReturn(loggedInUserId);
        when(userService.getById(loggedInUserId)).thenReturn(user);
        when(walletRepository.findById(loggedInUserId)).thenReturn(Optional.of(wallet));
        when(transactionService.addTransaction(any(TransactionRequest.class), eq(token))).thenReturn(new TransactionResponse());

        TransactionResponse response = walletService.transferMoneyToBeneficiary(request, token);

        assertNotNull(response);
        assertEquals(100.0, response.getAmount());
        assertEquals("Test Receiver", response.getReceiver());
        assertEquals("Beneficiary Transaction", response.getTransactionType());
        assertEquals("Test Description", response.getDescription());
        assertEquals(100.0, response.getBalance());

        Mockito.verify(walletRepository).findById(loggedInUserId);
        Mockito.verify(transactionService).addTransaction(any(TransactionRequest.class), eq(token));
        Mockito.verify(walletRepository).save(wallet);
    }

    @Test
    void transferMoneyToUser() {
    }

    @Test
    void getById() {
    }
}