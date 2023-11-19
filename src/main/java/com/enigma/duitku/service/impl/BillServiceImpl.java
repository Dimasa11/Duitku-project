package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Bill;
import com.enigma.duitku.entity.Transaction;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.exception.WalletException;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.repository.WalletRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.BillService;
import com.enigma.duitku.service.TransactionService;
import com.enigma.duitku.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.TransactionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final JwtUtils jwtUtils;

    private final UserService userService;

    private final WalletRepository walletRepository;

    private final TransactionService transactionService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransactionResponse billPayment(Bill bill, String token) throws WalletException, UserException {

        // TODO 1 Extracting User ID from JWT Token & Validate User
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        // TODO 2 Checking if User is not null
        if(user != null) {

            // TODO 3 Getting User's Wallet and Available Balance
            Wallet wallet = user.getWallet();
            Double availableBalane = wallet.getBalance();

            // TODO 4 Getting List of Bills from Wallet
            List<Bill> listOfBills = wallet.getListofBills();

            // TODO 5 Checking if Available Balance is Sufficient
            if(availableBalane >= bill.getAmount()) {

                // TODO 6 Creating Transaction Request
                TransactionRequest transaction = new TransactionRequest();
                transaction.setReceiver(bill.getReceiver());
                transaction.setBillType(bill.getBillType());
                transaction.setTransactionType("Bill Payment");
                transaction.setAmount(bill.getAmount());
                transaction.setDescription(bill.getDescription());

                // TODO 7 Setting Bill Type, Payment DateTime, and Adding Transaction
                bill.setBillType("Bill Payment");
                bill.setPaymentDateTime(LocalDateTime.now());
                transactionService.addTransaction(transaction, token);

                // TODO 8 Checking if Transaction is not null
                if(transaction != null) {

                    // TODO 9 Updating List of Bills and Wallet Balance
                    listOfBills.add(bill);
                    wallet.setBalance(availableBalane - bill.getAmount());
                    wallet.setListofBills(listOfBills);

                    // TODO 10 Saving and Flushing Wallet
                    walletRepository.saveAndFlush(wallet);

                    // TODO 11 Building and Returning Transaction Response
                    return TransactionResponse.builder()
                            .amount(transaction.getAmount())
                            .transactionType(bill.getBillType())
                            .description(transaction.getDescription())
                            .receiver(transaction.getReceiver())
                            .build();
                } else {
                    // TODO 12 Handling Transaction Failure Exception
                    throw new TransactionException("Transaction failed!");
                }

            } else {
                // TODO 13 Handling Insufficient Funds Exception
                throw new WalletException("Insufficient Funds ! Available Wallet Balance : " + availableBalane);
            }
        } else {
            // TODO 14 Handling User Not Found Exception
            throw new UserException("Please login in");
        }
    }

    @Override
    public Page<Bill> viewBillPayments(Integer page, Integer size, String token) throws UserException {
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if (user != null) {
            Wallet wallet = user.getWallet();

            List<Bill> billList = wallet.getListofBills();

            int start = (int) (page * size);
            int end = Math.min((start + size), billList.size());

            List<Bill> paginatedBillList = billList.subList(start, end);

            List<Bill> billResponses = new ArrayList<>();
            for (Bill bill : paginatedBillList) {
                Bill billResponse = new Bill(
                        bill.getConsumerNo(), bill.getBillType(), bill.getAmount(), bill.getDescription(), bill.getReceiver(), null
                );

                billResponses.add(billResponse);
            }

            Pageable pageable = PageRequest.of(page, size);
            return new PageImpl<>(billResponses, pageable, billList.size());
        } else {
            throw new UserException("Please login!");
        }
    }
}

