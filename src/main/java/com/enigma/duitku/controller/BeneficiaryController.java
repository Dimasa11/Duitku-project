package com.enigma.duitku.controller;

import com.enigma.duitku.exception.BeneficiaryException;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.BeneficiaryRequest;
import com.enigma.duitku.model.response.BankAccountResponse;
import com.enigma.duitku.model.response.BeneficiaryResponse;
import com.enigma.duitku.model.response.CommonResponse;
import com.enigma.duitku.model.response.PagingResponse;
import com.enigma.duitku.security.AuthTokenFilter;
import com.enigma.duitku.service.BeneficiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/beneficiary")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    private final AuthTokenFilter authTokenFilter;

    @PostMapping("/add")
    public ResponseEntity<?> addBeneficiary(@RequestBody BeneficiaryRequest request, HttpServletRequest httpServletRequest) {

        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            if(jwtToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .build());
            } else {
                BeneficiaryResponse beneficiaryResponse = beneficiaryService.addBeneficiary(request, jwtToken);

                if(beneficiaryResponse.getErrors() != null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(CommonResponse.builder()
                                    .statusCode(HttpStatus.CONFLICT.value())
                                    .data(beneficiaryResponse)
                                    .message("Cannot add a new account because it is already registered")
                                    .build());
                } else {
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(CommonResponse.builder()
                                    .statusCode(HttpStatus.CREATED.value())
                                    .data(beneficiaryResponse)
                                    .message("Successfully create beneficiary   ")
                                    .build());
                }
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Add Failed " + e.getMessage())
                            .build());
        }


    }

    @GetMapping("/viewall")
    public ResponseEntity<?> getAllBeneficiaryResponse(
            @RequestParam(name = "page", required = false, defaultValue = "0")Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5")Integer size,
            HttpServletRequest httpServletRequest
    ) throws UserException {

        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);

            if(jwtToken != null) {
                Page<BeneficiaryResponse> beneficiaryResponses = beneficiaryService.viewAllBeneficiaries(page, size, jwtToken);
                PagingResponse pagingResponse = PagingResponse.builder()
                        .currentPage(page)
                        .totalPage(beneficiaryResponses.getTotalPages())
                        .size(size)
                        .build();

                return ResponseEntity.status(HttpStatus.OK)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully get all beneficiaries")
                                .data(beneficiaryResponses.getContent())
                                .paging(pagingResponse)
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .build());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("View all failed " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/viewall/admin")
    public ResponseEntity<?> getAllBeneficiaryResponse(
            @RequestParam(name = "page", required = false, defaultValue = "0")Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5")Integer size
    ) throws UserException {

        try {
                Page<BeneficiaryResponse> beneficiaryResponses = beneficiaryService.viewAllBeneficiariesAdmin(page, size);
                PagingResponse pagingResponse = PagingResponse.builder()
                        .currentPage(page)
                        .totalPage(beneficiaryResponses.getTotalPages())
                        .size(size)
                        .build();

                return ResponseEntity.status(HttpStatus.OK)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully get all beneficiaries")
                                .data(beneficiaryResponses.getContent())
                                .paging(pagingResponse)
                                .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("View all failed " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity <?> deleteBeneficiary(@RequestParam String beneficiaryMobileNumber, HttpServletRequest httpServletRequest) throws BeneficiaryException, LoginException {

        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);

            if (jwtToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .message("Unauthorized: Missing or invalid token")
                                .build());
            }

            String result = beneficiaryService.deleteByMobileNumber(beneficiaryMobileNumber, jwtToken);

            CommonResponse response;
            HttpStatus httpStatus;

            if ("Beneficiary has been Successfully Deleted!".equals(result)) {
                response = CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Beneficiary deleted successfully")
                        .build();
                httpStatus = HttpStatus.OK;
            } else {
                response = CommonResponse.builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("Beneficiary not found or you don't have permission to delete it")
                        .build();
                httpStatus = HttpStatus.NOT_FOUND;
            }
            return ResponseEntity.status(httpStatus).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Delete failed " + e.getMessage())
                            .build());
        }
    }
}
