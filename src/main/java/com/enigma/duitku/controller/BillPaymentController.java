package com.enigma.duitku.controller;

import com.enigma.duitku.entity.Bill;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.exception.WalletException;
import com.enigma.duitku.model.response.BankAccountResponse;
import com.enigma.duitku.model.response.CommonResponse;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.security.AuthTokenFilter;
import com.enigma.duitku.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bill")
public class BillPaymentController {


    private final BillService billService;

    private final AuthTokenFilter authTokenFilter;

    @PostMapping("/payment")
    public ResponseEntity<?> BillPayment(@RequestBody Bill bill, HttpServletRequest httpServletRequest) throws  WalletException, UserException {

        String jwtToken = authTokenFilter.parseJwt(httpServletRequest);

        try {
            TransactionResponse transactionResponse =billService.billPayment(bill, jwtToken );

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.ACCEPTED.value())
                            .data(transactionResponse)
                            .message("Successfully payment")
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Payment Failed" + e.getMessage())
                            .build());
        }

    }

    @GetMapping("/view/allpayments")
    public ResponseEntity<?> viewBillPayments(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpServletRequest) {
        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            Page<Bill> billPage = billService.viewBillPayments(page, size, jwtToken);
            if (billPage != null) {
                return ResponseEntity.ok(billPage);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
}
