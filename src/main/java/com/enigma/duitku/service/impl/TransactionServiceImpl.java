package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Bill;
import com.enigma.duitku.entity.Transaction;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.repository.TransactionRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.repository.WalletRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.TransactionService;
import com.enigma.duitku.service.UserService;
import com.enigma.duitku.service.WalletService;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final WalletRepository walletRepository;

    private final UserService userService;

    private final UserRepository userRepository;

    private final JwtUtils jwtUtils;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransactionResponse addTransaction(TransactionRequest request, String token) throws UserException{

        String loggedInUserId = jwtUtils.extractUserId(token);

        log.info("Adding transaction for user with ID: {}", loggedInUserId);

        User user = userService.getById(loggedInUserId);

        if(user != null) {

            Wallet wallet = user.getWallet();

            Transaction transaction = new Transaction();

            transaction.setAmount(request.getAmount());
            transaction.setLocalDate(LocalDateTime.now());
            transaction.setDescription(request.getDescription());
            transaction.setReceiver(request.getReceiver());
            transaction.setType(request.getTransactionType());

            log.info("DESCRIPTION" + request.getDescription());

            transaction.setWalletId(wallet.getId());

            List<Transaction> listoftransactions = wallet.getListOfTransactions();

            listoftransactions.add(transaction);

            wallet.setListOfTransactions(listoftransactions);

            transactionRepository.saveAndFlush(transaction);
            walletRepository.saveAndFlush(wallet);

            return TransactionResponse.builder()
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .transactionType(request.getTransactionType())
                    .build();
        } else {
            throw new UserException("Plese Login In ");
        }
    }

    public Page<Transaction> getTransactionsByWalletId(String someWalletId, Pageable pageable) {
        return transactionRepository.findByWalletId(someWalletId, pageable);
    }

    // Admin
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public Page<TransactionResponse> viewAllTransaction(Integer page, Integer size ) throws UserException{

            Pageable pageable = PageRequest.of(page, size);
            Page<Transaction> transactions = transactionRepository.findAll(pageable);
            List<TransactionResponse> transactionResponses = new ArrayList<>();
            for (Transaction transaction : transactions.getContent()) {
                TransactionResponse transactionResponse = new TransactionResponse(transaction.getId(), transaction.getDescription(), transaction.getType(), transaction.getAmount(), transaction.getWalletId(), null);

                transactionResponses.add(transactionResponse);
            }
            return new PageImpl<>(transactionResponses, pageable, transactions.getTotalElements());
    }
}
