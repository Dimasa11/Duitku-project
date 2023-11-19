package com.enigma.duitku.service;


import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.*;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.TransactionResponse;

public interface WalletService {

        TransactionResponse transferMoneyToBeneficiary(TransactionRequest transaction, String token)
                throws BankAccountException, WalletException, BeneficiaryException, UserException;

        TransactionResponse transferMoneyToUser(TransactionRequest request, String token)
                throws UserException, TargetUserNotFoundException, UserNotFoundException, TransferException;

        Wallet getById(String id, String token) throws UserException;
}
