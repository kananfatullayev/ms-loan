package com.example.msloan.dao;

import com.example.msloan.model.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Integer> {

    List<LoanEntity> findByUserId(Integer userId);
}

