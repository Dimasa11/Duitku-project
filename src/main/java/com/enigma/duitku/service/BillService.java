package com.enigma.duitku.service;

import com.enigma.duitku.entity.Bill;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.exception.WalletException;
import com.enigma.duitku.model.response.TransactionResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BillService {

    TransactionResponse billPayment(Bill bill, String token) throws WalletException, UserException;

    Page<Bill> viewBillPayments(Integer page, Integer size, String token) throws UserException;
}
