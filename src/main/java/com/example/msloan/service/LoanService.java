package com.example.msloan.service;

import com.example.msloan.client.UserClient;
import com.example.msloan.client.dto.UserResponseDto;
import com.example.msloan.dao.LoanRepository;
import com.example.msloan.model.LoanEntity;
import com.example.msloan.model.constants.LoanConstants;
import com.example.msloan.model.dto.LoanRequestDto;
import com.example.msloan.model.dto.LoanResponseDto;
import com.example.msloan.model.exception.NotFoundException;
import com.example.msloan.util.CalculationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final UserClient userClient;
    private final LoanRepository loanRepository;

    @Transactional
    public LoanResponseDto createLoan(LoanRequestDto request) {
        log.info("Creating loan for user: {}", request.getUserId());

        UserResponseDto userDto = userClient.getUserById(request.getUserId());

        var monthlyAmount = CalculationUtil.calculateMonthlyAmount(
                request.getAmount(),
                BigDecimal.valueOf(LoanConstants.ANNUAL_INTEREST_RATE),
                request.getDuration());

        LoanEntity loanEntity = LoanEntity.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .monthlyAmount(monthlyAmount)
                .duration(request.getDuration())
                .build();

        LoanEntity savedLoan = loanRepository.save(loanEntity);
        log.info("Loan created successfully with id: {}", savedLoan.getId());

        // Send email notification

        return mapToResponseDto(savedLoan);
    }

    public LoanResponseDto getLoanById(Integer id) {
        log.info("Fetching loan with id: {}", id);

        LoanEntity loanEntity = loanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Loan not found with id: " + id));

        return mapToResponseDto(loanEntity);
    }

    public List<LoanResponseDto> getAllLoans() {
        log.info("Fetching all loans");

        return loanRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<LoanResponseDto> getLoansByUserId(Integer userId) {
        log.info("Fetching loans for user: {}", userId);

        return loanRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoanResponseDto updateLoan(Integer id, LoanRequestDto request) {
        log.info("Updating loan with id: {}", id);

        LoanEntity loanEntity = loanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Loan not found with id: " + id));

        loanEntity.setUserId(request.getUserId());
        loanEntity.setAmount(request.getAmount());
        loanEntity.setDuration(request.getDuration());

        LoanEntity updatedLoan = loanRepository.save(loanEntity);
        log.info("Loan updated successfully with id: {}", updatedLoan.getId());

        return mapToResponseDto(updatedLoan);
    }

    @Transactional
    public void deleteLoan(Integer id) {
        log.info("Deleting loan with id: {}", id);

        if (!loanRepository.existsById(id)) {
            throw new NotFoundException("Loan not found with id: " + id);
        }

        loanRepository.deleteById(id);
        log.info("Loan deleted successfully with id: {}", id);
    }

    private LoanResponseDto mapToResponseDto(LoanEntity loanEntity) {
        return LoanResponseDto.builder()
                .id(loanEntity.getId())
                .userId(loanEntity.getUserId())
                .amount(loanEntity.getAmount())
                .monthlyAmount(loanEntity.getMonthlyAmount())
                .duration(loanEntity.getDuration())
                .createdAt(loanEntity.getCreatedAt())
                .updatedAt(loanEntity.getUpdatedAt())
                .build();
    }
}

