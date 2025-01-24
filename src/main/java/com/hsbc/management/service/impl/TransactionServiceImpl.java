package com.hsbc.management.service.impl;

import com.hsbc.management.common.dto.TransactionDTO;
import com.hsbc.management.common.vo.TransactionVO;
import com.hsbc.management.dao.TransactionRepository;
import com.hsbc.management.common.entity.Transaction;
import com.hsbc.management.exception.BizException;
import com.hsbc.management.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * 创建交易
     *
     * @param dto 包含交易信息的DTO对象
     * @return 新建的交易对象
     * @throws BizException 如果transactionNo已存在，则抛出BizException异常
     */
    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionVO createTransaction(TransactionDTO dto) {
        Optional<Transaction> existingTransaction = transactionRepository.findByTransactionNo(dto.getTransactionNo());
        if (existingTransaction.isPresent()) {
            throw new BizException("Transaction with transactionNo " + dto.getTransactionNo() + " already exists.");
        }
        Transaction transaction = convertToEntity(dto);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToVO(savedTransaction);
    }

    /**
     * 删除交易
     *
     * @param id 交易ID
     * @throws BizException 如果交易ID不存在，则抛出BizException异常
     */
    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new BizException("Transaction with id " + id + " does not exist.");
        }
        transactionRepository.deleteById(id);
    }

    /**
     * 修改交易
     *
     * @param id 交易ID
     * @param dto 包含修改信息的DTO对象
     * @return 修改后的交易对象
     * @throws BizException 如果交易ID不存在，则抛出BizException异常
     */
    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionVO modifyTransaction(Long id, TransactionDTO dto) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()) {
            throw new BizException("Transaction with id " + id + " does not exist.");
        }
        Transaction transaction = optionalTransaction.get();
        transaction.setSourceAccountId(dto.getSourceAccountId());
        transaction.setTargetAccountId(dto.getTargetAccountId());
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setUpdateTime(System.currentTimeMillis());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToVO(savedTransaction);
    }

    /**
     * 获取所有交易记录的分页列表
     *
     * @param transactionNo 交易编号，用于筛选交易记录
     * @param pageable      分页参数
     * @return 包含交易记录的分页列表
     */
    @Cacheable(value = "transactions",
            key = "#transactionNo != null ? #transactionNo + '_' + #pageable.pageNumber + '_' + #pageable.pageSize : 'all_' + #pageable.pageNumber + '_' + #pageable.pageSize",
            unless = "#result == null")
    public Page<TransactionVO> listAllTransactions(String transactionNo, Pageable pageable) {
        Page<Transaction> transactions;
        if (transactionNo != null && !transactionNo.isEmpty()) {
            transactions = transactionRepository.findByTransactionNoContaining(transactionNo, pageable);
        } else {
            transactions = transactionRepository.findAll(pageable);
        }
        return transactions.map(this::convertToVO);
    }

    /**
     * 根据交易ID获取交易信息
     *
     * @param id 交易ID
     * @return 包含交易信息的Optional<TransactionVO>对象，如果不存在则返回Optional.empty()
     */
    @Cacheable(value = "transactions", key = "#id")
    public Optional<TransactionVO> getTransactionById(Long id) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        return optionalTransaction.map(this::convertToVO);
    }

    /**
     * 将TransactionDTO对象转换为Transaction实体对象
     *
     * @param dto TransactionDTO对象
     * @return 转换后的Transaction实体对象
     */
    private Transaction convertToEntity(TransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setSourceAccountId(dto.getSourceAccountId());
        transaction.setTargetAccountId(dto.getTargetAccountId());
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionNo(dto.getTransactionNo());
        long now = System.currentTimeMillis();
        transaction.setCreateTime(now);
        transaction.setUpdateTime(now);
        return transaction;
    }

    /**
     * 将Transaction实体对象转换为TransactionVO对象
     *
     * @param transaction Transaction实体对象
     * @return 转换后的TransactionVO对象
     */
    private TransactionVO convertToVO(Transaction transaction) {
        TransactionVO vo = new TransactionVO();
        vo.setId(transaction.getId());
        vo.setSourceAccountId(transaction.getSourceAccountId());
        vo.setTargetAccountId(transaction.getTargetAccountId());
        vo.setAmount(transaction.getAmount());
        vo.setDescription(transaction.getDescription());
        vo.setCreateTime(transaction.getCreateTime());
        vo.setUpdateTime(transaction.getUpdateTime());
        vo.setTransactionNo(transaction.getTransactionNo());
        return vo;
    }

    /**
     * 生成缓存键
     *
     * @param transactionNo 交易编号，可为空
     * @param pageable      分页信息
     * @return 生成的缓存键
     */
    private String generateCacheKey(String transactionNo, Pageable pageable) {
        if (Objects.nonNull(transactionNo)) {
            return transactionNo + "_" + pageable.getPageNumber() + "_" + pageable.getPageSize();
        }
        return "all_" + pageable.getPageNumber() + "_" + pageable.getPageSize();
    }
}
