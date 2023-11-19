package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.*;
import com.enigma.duitku.exception.TransferException;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.BankAccountRequest;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.BankAccountResponse;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.repository.BankAccountRepository;
import com.enigma.duitku.repository.TransactionRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.repository.WalletRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.BankAccountService;
import com.enigma.duitku.service.TransactionService;
import com.enigma.duitku.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final UserRepository userRepository;

    private final BankAccountRepository bankAccountRepository;

    private final TransactionRepository transactionRepository;

    private final TransactionService transactionService;

    private final WalletRepository walletRepository;

    private final JwtUtils jwtUtils;

    private final UserService userService;

    @Override
    public BankAccountResponse addAccount(BankAccountRequest request, String token) throws UserException {

        // TODO 1 : Extract the user ID from the JWT token using jwtUtils.
        String loggedInUserId = jwtUtils.extractUserId(token);

        // TODO 2 : Retrieve the user object from the userService based on the extracted user ID.
        User user = userService.getById(loggedInUserId);

        // TODO 3 : Check if User Exists
        if(user != null) {

            // TODO 4 : Create a new BankAccount instance
            BankAccount bankAccount = new BankAccount();

            // TODO 5 : Set BankAccount properties from the request
            bankAccount.setId(request.getMobileNumber());
            bankAccount.setAccountNo(request.getAccountNo());

            // TODO 6 : Check Minimum Balance
            if (request.getBalance() >= 50000) {
                bankAccount.setBalance(request.getBalance());
            } else {
                throw new IllegalArgumentException("Minimum Balance 50.000");
            }

            // TODO 7 : Set BankAccount properties from the request (again, if the balance check passes)
            bankAccount.setBalance(request.getBalance());
            bankAccount.setBankName(request.getBankName());
            bankAccount.setUser(user);

            // TODO 8 : Save the BankAccount to the repository
            bankAccountRepository.saveAndFlush(bankAccount);

            // TODO 9 : Build and return BankAccountResponse
            return BankAccountResponse.builder()
                    .mobileNumber(request.getMobileNumber())
                    .bankName(request.getBankName())
                    .accountNo(request.getAccountNo())
                    .balance(request.getBalance())
                    .build();
        } else {
            throw new UserException("Please Login in !");
        }
    }

    @Override
    public BankAccountResponse getById(String id, String token) throws UserException {
        // Ekstrak ID pengguna dari token
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if(user != null) {
            BankAccount bankAccount = bankAccountRepository.findById(id).orElse(null);

            return BankAccountResponse.builder()
                    .mobileNumber(bankAccount.getAccountNo())
                    .bankName(bankAccount.getBankName())
                    .build();
        } else {
            throw new UserException("Plese Login In!");
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public BankAccountResponse topUpWallet(BankAccountRequest request, String token) throws UserException, TransactionException, TransferException{
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(request.getMobileNumber());

        // TODO 1 Extracting User ID from JWT Token & Validate User
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        // TODO 2 Checking if User is not null
        if(user != null) {

            // TODO 3 Mapping Optional Bank Account to Bank Account Response
            return optionalBankAccount.map(bankAccount -> {

                // TODO 4 Extracting Amount from the Request
                double amount = request.getAmount();

                // TODO 5 Getting wallet admin
                String adminMobileNumber = "085156811979";
                Optional<Wallet> admin = walletRepository.findById(adminMobileNumber);

                // TODO 6 Checking if Bank Account Balance is Sufficient
                if (bankAccount.getBalance() >= amount){
                    if (amount < 5.000) {
                        throw new TransferException("Minimum balance requirement not met. Minimum amount: 5,000" );
                    }

                    // TODO 7 Define admin fee rate 1000
                    double adminFee = 1000;

                    // TODO 8 Getting Wallets from user admin and setBalance for admin fee latest
                    if(admin.isPresent()) {
                        Wallet adminWallet = admin.get();
                        double adminBalance = adminWallet.getBalance();
                        adminWallet.setBalance(adminFee + adminBalance);
                        walletRepository.saveAndFlush(adminWallet);
                    }

                    // TODO 9 Updating Bank Account and User Wallet Balances
                    bankAccount.setBalance(bankAccount.getBalance() - amount - adminFee);

                    Wallet userWallet = user.getWallet();
                    userWallet.setBalance(userWallet.getBalance() + amount);

                    // TODO 10 Creating Transaction Request
                    TransactionRequest transaction = new TransactionRequest();
                    user.getMobileNumber();
                    transaction.setDescription("Wallet Top Up");
                    transaction.setTransactionType("E-Wallet Transaction");
                    transaction.setBankName(request.getBankName());
                    transaction.setReceiver(request.getReceiver());
                    transaction.setAmount(request.getAmount());

                    // TODO 11 Handling Transaction Service Exception
                    try {
                        transactionService.addTransaction(transaction, token);
                    } catch (UserException e) {
                        throw new RuntimeException(e);
                    }

                    // TODO 12 Saving Bank Account;
                    bankAccountRepository.saveAndFlush(bankAccount);

                    // TODO 13 Building and Returning Bank Account Response
                    return BankAccountResponse.builder()
                            .mobileNumber(request.getMobileNumber())
                            .bankName(bankAccount.getBankName())
                            .balance(bankAccount.getBalance())
                            .accountNo(bankAccount.getAccountNo())
                            .build();
                }else {
                    throw new TransferException("Insufficient Funds! Available Wallet Balance: " + bankAccount.getBalance());
                }
            }).orElseThrow(() -> new RuntimeException("User Not found"));
        } else {
            throw new UserException("Plese Login In!");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public Page<BankAccountResponse> getAllBankAccount(Integer page, Integer size) throws UserException{

            Pageable pageable = PageRequest.of(page, size);
            Page<BankAccount> bankAccounts = bankAccountRepository.findAll(pageable);
            List<BankAccountResponse> bankAccountResponses = new ArrayList<>();
            for(BankAccount bankAccount: bankAccounts.getContent()) {
                BankAccountResponse bankAccountResponse = BankAccountResponse.builder()
                        .balance(bankAccount.getBalance())
                        .accountNo(bankAccount.getAccountNo())
                        .bankName(bankAccount.getBankName())
                        .mobileNumber(bankAccount.getId())
                        .build();
                bankAccountResponses.add(bankAccountResponse);
            }
            return new PageImpl<>(bankAccountResponses, pageable, bankAccounts.getTotalElements());
    }
}
