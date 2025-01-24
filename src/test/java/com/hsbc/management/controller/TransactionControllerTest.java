package com.hsbc.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hsbc.management.common.dto.TransactionDTO;
import com.hsbc.management.common.vo.TransactionVO;
import com.hsbc.management.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListAllTransactions() throws Exception {
        // Arrange
        TransactionVO transactionVO = new TransactionVO();
        transactionVO.setId(1L);
        transactionVO.setTransactionNo("12345");
    
        Page<TransactionVO> page = new PageImpl<>(Collections.singletonList(transactionVO));
        when(transactionService.listAllTransactions(anyString(), any(Pageable.class))).thenReturn(page);
    
        // Act & Assert
        mockMvc.perform(get("/transactions")
                .param("transactionNo", "12345")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1L))
                .andExpect(jsonPath("$.data.content[0].transactionNo").value("12345"));
    }

    @Test
    public void testGetTransactionById() throws Exception {
        // Arrange
        TransactionVO transactionVO = new TransactionVO();
        transactionVO.setId(1L);
        transactionVO.setTransactionNo("12345");
    
        when(transactionService.getTransactionById(anyLong())).thenReturn(Optional.of(transactionVO));
    
        // Act & Assert
        mockMvc.perform(get("/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.transactionNo").value("12345"));
    }

    @Test
    public void testGetTransactionByIdNotFound() throws Exception {
        // Arrange
        when(transactionService.getTransactionById(anyLong())).thenReturn(Optional.empty());
    
        // Act & Assert
        mockMvc.perform(get("/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testCreateTransaction() throws Exception {
        // Arrange
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(BigDecimal.valueOf(100.0));
        transactionDTO.setTransactionNo("12345");
        transactionDTO.setSourceAccountId(1L);
        transactionDTO.setTargetAccountId(2L);
        transactionDTO.setDescription("Test Transaction");
    
        TransactionVO transactionVO = new TransactionVO();
        transactionVO.setId(1L);
        transactionVO.setTransactionNo("12345");
    
        when(transactionService.createTransaction(any(TransactionDTO.class))).thenReturn(transactionVO);
    
        String transactionDTOJson = "{\"amount\":100.0,\"transactionNo\":\"12345\",\"sourceAccountId\":1,\"targetAccountId\":2,\"description\":\"Test Transaction\"}";
    
        // Act & Assert
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionDTOJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.transactionNo").value("12345"));
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        // Arrange
        doNothing().when(transactionService).deleteTransaction(1L);
    
        // Act & Assert
        mockMvc.perform(delete("/transactions/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testModifyTransaction() throws Exception {
        // Arrange
        TransactionVO transactionVO = new TransactionVO();
        transactionVO.setId(1L);
        transactionVO.setTransactionNo("12345");
    
        when(transactionService.modifyTransaction(anyLong(), any(TransactionDTO.class))).thenReturn(transactionVO);
    
        // Act & Assert
        mockMvc.perform(put("/transactions/{id}", 1L)
                .contentType("application/json")
                .content("{\"amount\": 100.0,\"transactionNo\": \"12345\",\"sourceAccountId\": 1,\"targetAccountId\": 2,\"description\": \"Test Transaction\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.transactionNo").value("12345"));
    }

}