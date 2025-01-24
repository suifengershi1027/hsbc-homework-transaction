package com.hsbc.management.common.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 交易视图对象
 */
@Data
public class TransactionVO {
    /**
     * 主键ID
     */
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
