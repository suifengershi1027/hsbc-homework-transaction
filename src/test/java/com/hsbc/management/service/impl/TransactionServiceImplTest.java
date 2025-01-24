package com.hsbc.management.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.hsbc.management.common.dto.TransactionDTO;
import com.hsbc.management.common.entity.Transaction;
import com.hsbc.management.common.vo.TransactionVO;
import com.hsbc.management.dao.TransactionRepository;
import com.hsbc.management.exception.BizException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

public class TransactionServiceImplTest{

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTransactionTransactionAlreadyExists() {
        // Arrange
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionNo("12345");
        transactionDTO.setSourceAccountId(1L);
        transactionDTO.setTargetAccountId(2L);
        transactionDTO.setAmount(BigDecimal.valueOf(100));
        transactionDTO.setDescription("Test Transaction");
    
        when(transactionRepository.findByTransactionNo("12345")).thenReturn(Optional.of(new Transaction()));
    
        // Act & Assert
        BizException exception = assertThrows(BizException.class, () -> {
            transactionService.createTransaction(transactionDTO);
        });
    
        assertEquals("Transaction with transactionNo 12345 already exists.", exception.getMessage());
        verify(transactionRepository, times(1)).findByTransactionNo("12345");
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    void testCreateTransactionSuccess() {
        // Arrange
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionNo("12345");
        transactionDTO.setSourceAccountId(1L);
        transactionDTO.setTargetAccountId(2L);
        transactionDTO.setAmount(BigDecimal.valueOf(100));
        transactionDTO.setDescription("Test Transaction");
    
        Transaction transaction = new Transaction();
        transaction.setSourceAccountId(1L);
        transaction.setTargetAccountId(2L);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setDescription("Test Transaction");
        transaction.setTransactionNo("12345");
        long now = System.currentTimeMillis();
        transaction.setCreateTime(now);
        transaction.setUpdateTime(now);
    
        when(transactionRepository.findByTransactionNo("12345")).thenReturn(Optional.empty());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
    
        // Act
        TransactionVO result = transactionService.createTransaction(transactionDTO);
    
        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getTransactionNo());
        assertEquals(BigDecimal.valueOf(100), result.getAmount());
        assertEquals("Test Transaction", result.getDescription());
        verify(transactionRepository, times(1)).findByTransactionNo("12345");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testDeleteTransactionTransactionExists() {
        // Arrange
        Long transactionId = 1L;
        when(transactionRepository.existsById(transactionId)).thenReturn(true);
    
        // Act & Assert
        transactionService.deleteTransaction(transactionId);
        verify(transactionRepository, times(1)).deleteById(transactionId);
        verify(transactionRepository, times(1)).existsById(transactionId);
    }

    @Test
    void testDeleteTransactionTransactionDoesNotExist() {
        // Arrange
        Long transactionId = 1L;
        when(transactionRepository.existsById(transactionId)).thenReturn(false);
    
        // Act & Assert
        BizException exception = assertThrows(BizException.class, () -> {
            transactionService.deleteTransaction(transactionId);
        });
    
        assertEquals("Transaction with id 1 does not exist.", exception.getMessage());
        verify(transactionRepository, times(1)).existsById(transactionId);
        verify(transactionRepository, times(0)).deleteById(transactionId);
    }

    @Test
    void testModifyTransactionTransactionNotFound() {
        // Arrange
        Long id = 1L;
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSourceAccountId(2L);
        transactionDTO.setTargetAccountId(3L);
        transactionDTO.setAmount(BigDecimal.valueOf(200));
        transactionDTO.setDescription("Updated Transaction");
    
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());
    
        // Act & Assert
        BizException exception = assertThrows(BizException.class, () -> {
            transactionService.modifyTransaction(id, transactionDTO);
        });
    
        assertEquals("Transaction with id 1 does not exist.", exception.getMessage());
        verify(transactionRepository, times(1)).findById(id);
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    void testModifyTransactionSuccess() {
        // Arrange
        Long id = 1L;
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSourceAccountId(2L);
        transactionDTO.setTargetAccountId(3L);
        transactionDTO.setAmount(BigDecimal.valueOf(200));
        transactionDTO.setDescription("Updated Transaction");
    
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setSourceAccountId(1L);
        transaction.setTargetAccountId(2L);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setDescription("Original Transaction");
    
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
    
        // Act
        TransactionVO result = transactionService.modifyTransaction(id, transactionDTO);
    
        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getSourceAccountId());
        assertEquals(3L, result.getTargetAccountId());
        assertEquals(BigDecimal.valueOf(200), result.getAmount());
        assertEquals("Updated Transaction", result.getDescription());
        verify(transactionRepository, times(1)).findById(id);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testListAllTransactionsWithTransactionNo() {
        // Arrange
        String transactionNo = "12345";
        Pageable pageable = PageRequest.of(0, 10);
        Transaction transaction = new Transaction();
        Page<Transaction> transactionPage = new PageImpl<>(Collections.singletonList(transaction));
    
        when(transactionRepository.findByTransactionNoContaining(transactionNo, pageable)).thenReturn(transactionPage);
    
        // Act
        Page<TransactionVO> result = transactionService.listAllTransactions(transactionNo, pageable);
    
        // Assert
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository, times(1)).findByTransactionNoContaining(transactionNo, pageable);
    }

    @Test
    void testListAllTransactionsWithoutTransactionNo() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Transaction transaction = new Transaction();
        Page<Transaction> transactionPage = new PageImpl<>(Collections.singletonList(transaction));
    
        when(transactionRepository.findAll(pageable)).thenReturn(transactionPage);
    
        // Act
        Page<TransactionVO> result = transactionService.listAllTransactions(null, pageable);
    
        // Assert
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetTransactionByIdTransactionExists() {
        // Arrange
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
    
        // Act
        Optional<TransactionVO> result = transactionService.getTransactionById(transactionId);
    
        // Assert
        assertTrue(result.isPresent());
        assertEquals(transactionId, result.get().getId());
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void testGetTransactionByIdTransactionDoesNotExist() {
        // Arrange
        Long transactionId = 1L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());
    
        // Act
        Optional<TransactionVO> result = transactionService.getTransactionById(transactionId);
    
        // Assert
        assertFalse(result.isPresent());
        verify(transactionRepository, times(1)).findById(transactionId);
    }

}