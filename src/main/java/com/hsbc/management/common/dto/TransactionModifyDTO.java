package com.hsbc.management.common.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 交易数据传输对象
 */
@Data
public class TransactionModifyDTO {
    /**
     * 来源账号ID
     */
    @NotNull(message = "sourceAccountId不能为空")
    private Long sourceAccountId;
    /**
     * 收款账号ID
     */
    @NotNull(message = "targetAccountId不能为空")
    private Long targetAccountId;
    /**
     * 交易金额
     */
    @NotNull(message = "交易金额amount不能为空")
    @Digits(integer = Integer.MAX_VALUE, fraction = 2, message = "金额必须为两位小数")
    @DecimalMin(value = "0.01", inclusive = false, message = "金额必须大于 0.01")
    private BigDecimal amount;
    /**
     * 交易描述
     */
    @NotBlank(message = "description不能为空")
    @Size(min = 2, max = 30, message = "description长度必须大于2小于30")
    private String description;
}
