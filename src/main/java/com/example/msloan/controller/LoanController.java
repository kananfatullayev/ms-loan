package com.example.msloan.controller;

import com.example.msloan.model.dto.LoanRequestDto;
import com.example.msloan.model.dto.LoanResponseDto;
import com.example.msloan.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Loan Management", description = "APIs for managing loan records")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @Operation(summary = "Create a new loan", description = "Creates a new loan record with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan created successfully",
                    content = @Content(schema = @Schema(implementation = LoanResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    public ResponseEntity<LoanResponseDto> createLoan(
            @Parameter(description = "Loan details to create", required = true)
            @RequestBody LoanRequestDto request) {
        LoanResponseDto response = loanService.createLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan by ID", description = "Retrieves a specific loan by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan found",
                    content = @Content(schema = @Schema(implementation = LoanResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Loan not found", content = @Content)
    })
    public ResponseEntity<LoanResponseDto> getLoanById(
            @Parameter(description = "ID of the loan to retrieve", required = true)
            @PathVariable Integer id) {
        LoanResponseDto response = loanService.getLoanById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all loans", description = "Retrieves a list of all loans in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of loans",
                    content = @Content(schema = @Schema(implementation = LoanResponseDto.class)))
    })
    public ResponseEntity<List<LoanResponseDto>> getAllLoans() {
        List<LoanResponseDto> response = loanService.getAllLoans();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get loans by user ID", description = "Retrieves all loans for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's loans",
                    content = @Content(schema = @Schema(implementation = LoanResponseDto.class)))
    })
    public ResponseEntity<List<LoanResponseDto>> getLoansByUserId(
            @Parameter(description = "ID of the user whose loans to retrieve", required = true)
            @PathVariable Integer userId) {
        List<LoanResponseDto> response = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a loan", description = "Updates an existing loan with new details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan updated successfully",
                    content = @Content(schema = @Schema(implementation = LoanResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Loan not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    public ResponseEntity<LoanResponseDto> updateLoan(
            @Parameter(description = "ID of the loan to update", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Updated loan details", required = true)
            @RequestBody LoanRequestDto request) {
        LoanResponseDto response = loanService.updateLoan(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a loan", description = "Deletes a loan by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Loan deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Loan not found", content = @Content)
    })
    public ResponseEntity<Void> deleteLoan(
            @Parameter(description = "ID of the loan to delete", required = true)
            @PathVariable Integer id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}

