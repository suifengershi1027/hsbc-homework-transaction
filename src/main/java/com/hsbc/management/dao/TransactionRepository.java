package com.hsbc.management.dao;

import com.hsbc.management.common.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * 根据交易编号搜索交易记录的分页列表
     *
     * @param transactionNo 交易编号，用于搜索交易记录
     * @param pageable      分页参数
     * @return 包含匹配交易编号的交易记录的分页列表
     */
    Page<Transaction> findByTransactionNoContaining(String transactionNo, Pageable pageable);

    /**
     * 获取所有交易记录的分页列表
     *
     * @param pageable 分页参数
     * @return 包含所有交易记录的分页列表
     */
    Page<Transaction> findAll(Pageable pageable);

    /**
     * 根据交易编号查找交易记录
     *
     * @param transactionNo 交易编号
     * @return 包含匹配交易编号的交易记录的Optional对象，如果不存在则返回Optional.empty()
     */
    Optional<Transaction> findByTransactionNo(String transactionNo);
}
