package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.*;
import com.enigma.duitku.exception.*;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.repository.AdminRepository;
import com.enigma.duitku.repository.BankAccountRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.repository.WalletRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.TransactionService;
import com.enigma.duitku.service.UserService;
import com.enigma.duitku.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    private final UserRepository userRepository;

    private final UserService userService;

    private final TransactionService transactionService;

    private final JwtUtils jwtUtils;

    private final BankAccountRepository bankAccountRepository;

    private final AdminRepository adminRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransactionResponse transferMoneyToBeneficiary(TransactionRequest request, String token)
            throws BankAccountException, WalletException, BeneficiaryException, UserException{

        // TODO 1 Extracting User ID from JWT Token & Validate User
        String loggedInUserId = jwtUtils.extractUserId(token);
        User validateUser = userService.getById(loggedInUserId);

        // TODO 2 Checking if User is not null
        if (validateUser != null) {

            // TODO 3 Getting User's Wallet
            Wallet wallet = validateUser.getWallet();

            // TODO 4 Iterating through Beneficiaries to find the target Beneficiary
            List<Beneficiary> listofbeneficiaries = wallet.getListOfBeneficiaries();
            Iterator<Beneficiary> iterator = listofbeneficiaries.iterator();

            Beneficiary targetBeneficiary = null;

            while (iterator.hasNext()) {
                Beneficiary b = iterator.next();
                if (Objects.equals(b.getMobileNumber(), request.getBeneficiaryMobileNumber())) {
                    targetBeneficiary = b;
                    break;
                }
            }

            log.info("Beneficiary Mobile number " + request.getBeneficiaryMobileNumber());

            // TODO 5 Checking if Target Beneficiary is found
            if (targetBeneficiary != null) {
                // TODO 6 Checking Wallet Existence
                Optional<Wallet> optionalWallet = walletRepository.findById(validateUser.getMobileNumber());

                // TODO 7 Checking if Wallet is Present
                if (optionalWallet.isPresent()) {
                    // TODO 8 Checking Available Balance
                    Double availableBalance = optionalWallet.get().getBalance();

                    // TODO 9 Checking if Available Balance is Sufficient
                    if (availableBalance >= request.getAmount()) {

                        // TODO 10 Creating Transaction Request
                        TransactionRequest transaction = new TransactionRequest();
                        targetBeneficiary.getAccountNo();
                        transaction.setDescription(request.getDescription());
                        transaction.setTransactionType("Beneficiary Transaction");
                        transaction.setReceiver(request.getReceiver());
                        transaction.setAmount(request.getAmount());
                        transactionService.addTransaction(transaction, token);

                        if (transaction != null) {

                            // TODO 11 Updating Wallet Balance and Saving Transaction
                            wallet.setBalance(availableBalance - request.getAmount());
                            walletRepository.save(wallet);

                            // TODO 12 Building and Returning Transaction Response
                            return TransactionResponse.builder()
                                    .amount(transaction.getAmount())
                                    .receiver(transaction.getReceiver())
                                    .transactionType(transaction.getTransactionType())
                                    .description(transaction.getDescription())
                                    .balance(optionalWallet.get().getBalance())
                                    .build();
                        } else {
                            throw new TransactionException("Transaction Failed");
                        }
                    } else {
                        throw new WalletException("Insufficient Funds. Available Bank Balance : " + availableBalance);
                    }
                } else {
                    throw new BankAccountException("No Registered Bank Account Found With This Mobile Number : " + validateUser.getMobileNumber());
                }
            } else {
                throw new BeneficiaryException("No Registered Beneficiary found with this Mobile Number: " + request.getBeneficiaryMobileNumber());
            }
        } else {
            throw new UserException("Please Login in");
        }
    }

    @Override
    public TransactionResponse transferMoneyToUser(TransactionRequest request, String token)
            throws UserException, TargetUserNotFoundException, UserNotFoundException, TransferException {

        // TODO 1 Extracting User ID from JWT Token & Validate User
        String loggedInUserId = jwtUtils.extractUserId(token);
        User validateUser = userService.getById(loggedInUserId);

        // TODO 2 Checking if User is not null
        if (validateUser != null) {
            // TODO 3 Getting Optional User by ID
            Optional<User> optionalUser = userRepository.findById(validateUser.getMobileNumber());

            log.info("ID Login " + validateUser.getWallet().getId());

            // TODO 4 Checking if Optional User is present
            if (optionalUser.isPresent()) {

                // TODO 5 Getting Optional Target User by Mobile Number
                Optional<User> targetUser = userRepository.findById(request.getTargetMobileNumber());
                log.info(" Target Mobile Number " + request.getTargetMobileNumber());

                // TODO 6 Initializing Balance Variables
                Double availableBalance = null;
                Double targetAvailableBalance;

                // TODO 7 Checking if Target User is present
                if (targetUser != null) {

                    // TODO 8 Getting Wallets of Source and Target Users
                    Optional<User> user = optionalUser;
                    Wallet wallet = user.get().getWallet();
                    Wallet targetWallet = targetUser.get().getWallet();

                    // TODO 9 Getting wallet admin
                    String adminMobileNumber = "085156811979";
                    Optional<Wallet> admin = walletRepository.findById(adminMobileNumber);

                    // TODO 10 Checking for Self-Transfer and Returning Error:
                    if (validateUser.getMobileNumber().equals(request.getTargetMobileNumber())) {
                        return TransactionResponse.builder()
                                .errors("Cannot transfer money to yourself")
                                .build();
                    }

                    // TODO 11 Getting Balances and Target User's Transaction List
                    availableBalance = wallet.getBalance();
                    targetAvailableBalance = targetWallet.getBalance();

                    List<Transaction> targetListOfTransactions = targetWallet.getListOfTransactions();

                    // TODO 12 Checking if Available Balance is Sufficient
                    if (availableBalance >= request.getAmount()) {

                        // TODO 13 Creating Transaction Request and Adding Transaction
                        TransactionRequest transactionRequest = new TransactionRequest();
                        transactionRequest.setTransactionType(request.getTransactionType());
                        transactionRequest.setDescription(request.getDescription());
                        transactionRequest.setAmount(request.getAmount());
                        transactionRequest.setReceiver(request.getReceiver());
                        transactionService.addTransaction(transactionRequest, token);

                        // TODO 14 Checking for Minimum Transfer Amount:
                        if (request.getAmount() < 10000) {
                            return TransactionResponse.builder()
                                    .errors("Minimum balance requirement not met. Minimum amount: 10,000")
                                    .build();
                        }

                        // TODO 15 Updating Balances and Saving Wallets
                        targetWallet.setBalance(targetAvailableBalance + request.getAmount());

                        // TODO 16 Define admin fee rate 2000
                        double adminFee = 2000;

                        // TODO 17 Getting Wallets from user admin and setBalance for admin fee latest
                        if(admin.isPresent()) {
                            Wallet adminWallet = admin.get();
                            double adminBalance = adminWallet.getBalance();
                            adminWallet.setBalance(adminFee + adminBalance);
                            walletRepository.saveAndFlush(adminWallet);
                        }

                        // TODO 18 Setting wallet user aftertotal amount fee admin
                        double amountAfterAdminFee = request.getAmount() + adminFee;

                        // TODO 19 Set the latest money amount after admin fee
                        wallet.setBalance(availableBalance - amountAfterAdminFee);

                        // TODO 20 Save to database wallet and target wallet latest
                        walletRepository.saveAndFlush(wallet);
                        walletRepository.saveAndFlush(targetWallet);

                        // TODO 21 Building and Returning Transaction Response
                        return TransactionResponse.builder()
                                .amount(transactionRequest.getAmount())
                                .receiver(transactionRequest.getReceiver())
                                .description(transactionRequest.getDescription())
                                .transactionType(transactionRequest.getTransactionType())
                                .build();
                    } else {
                        throw new TransferException("Insufficient Funds! Available Wallet Balance: " + availableBalance);
                    }
                } else {
                    throw new TargetUserNotFoundException("Target user not found with mobile number: " + request.getTargetMobileNumber());
                }
            } else {
                throw new UserNotFoundException("User Not Found with mobile number " + validateUser.getMobileNumber());
            }
        } else {
            throw new UserNotFoundException("User Not Found with mobile number " + validateUser.getMobileNumber());
        }
    }

    @Override
    public Wallet getById(String id, String token) throws UserException {
        String loggedInUserId = jwtUtils.extractUserId(token);
        User validateUser = userService.getById(loggedInUserId);

        if(validateUser != null) {
            return walletRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet Not Found"));
        } else {
            throw new UserException("Please Login in");
        }
    }

}
