package com.enigma.duitku.controller;

import com.enigma.duitku.entity.Transaction;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.BankAccountResponse;
import com.enigma.duitku.model.response.CommonResponse;
import com.enigma.duitku.model.response.PagingResponse;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.security.AuthTokenFilter;
import com.enigma.duitku.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    private final AuthTokenFilter authTokenFilter;

    @GetMapping("/{walletId}")
    public Page<Transaction> getTransactionsByWalletId(
            @PathVariable String walletId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return transactionService.getTransactionsByWalletId(walletId, PageRequest.of(page, size));
    }

    // Admin
    @GetMapping("/viewall")
    public ResponseEntity<?> viewAllTransaction(
            @RequestParam(name= "page", required = false, defaultValue = "0")Integer page,
            @RequestParam(name= "size", required = false, defaultValue = "5")Integer size
    ) throws UserException {
        try {
            Page<TransactionResponse> transactionResponses = transactionService.viewAllTransaction(page, size);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .currentPage(page)
                    .totalPage(transactionResponses.getTotalPages())
                    .size(size)
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.OK.value())
                            .message("Successfully get all transaction")
                            .data(transactionResponses.getContent())
                            .paging(pagingResponse)
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("View all failed" + e.getMessage())
                            .build());
        }

    }
}
