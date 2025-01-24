package com.hsbc.management.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 交易实体
 */
@Data
@Table(name = "transaction_management", indexes = {
        @Index(name = "idx_transaction_no", columnList = "transaction_no", unique = true)
})
@Entity
public class Transaction {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    /**
     * 交易编号
     */
    private String transactionNo;
    /**
     * 来源账号ID
     */
    private Long sourceAccountId;
    /**
     * 收款账号ID
     */
    private Long targetAccountId;
    /**
     * 交易金额
     */
    private BigDecimal amount;
    /**
     * 交易描述
     */
    private String description;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
}
