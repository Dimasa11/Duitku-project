package com.enigma.duitku.controller;


import com.enigma.duitku.exception.*;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.BankAccountResponse;
import com.enigma.duitku.model.response.CommonResponse;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.security.AuthTokenFilter;
import com.enigma.duitku.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/wallet")
public class WalletController {

    private final WalletService walletService;

    private final AuthTokenFilter authTokenFilter;

    @PostMapping("/transfertobeneficiary")
    public ResponseEntity<?> transferMoney(@RequestBody TransactionRequest request, HttpServletRequest httpServletRequest)  throws BankAccountException, WalletException, BeneficiaryException, UserException {

        try{
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);

            if(jwtToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .build());
            }

            TransactionResponse transactionResponse = walletService.transferMoneyToBeneficiary(request, jwtToken);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.OK.value())
                            .message("Successfully transfer money")
                            .data(transactionResponse)
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Failed transfer to beneficiary " + e.getMessage())
                            .build());
        }

    }

    @PostMapping("/transfertouser")
    public ResponseEntity<?>transferMoneyBetweenApplicationUsers(@RequestBody TransactionRequest request, HttpServletRequest httpServletRequest)
            throws UserException, TargetUserNotFoundException, UserNotFoundException, TransferException {

        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);

            if (jwtToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .build());
            }

            TransactionResponse transactionResponse = walletService.transferMoneyToUser(request, jwtToken);
            if(transactionResponse.getErrors() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .message("Failed to transfer")
                                .data(transactionResponse)
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully transfer money")
                                .data(transactionResponse)
                                .build());
            }

        } catch (TransferException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Failed transfer to user " + e.getMessage())
                            .build());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getWalletById(@RequestParam String id, HttpServletRequest httpServletRequest) throws UserException {
        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.OK.value())
                            .message("Successfully get wallet by id")
                            .data(walletService.getById(id, jwtToken))
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Failed get wallet by id " + e.getMessage())
                            .build());
        }
    }
}
