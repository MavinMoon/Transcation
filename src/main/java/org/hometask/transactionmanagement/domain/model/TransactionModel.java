package org.hometask.transactionmanagement.domain.model;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Mavin
 * @date 2025/3/12 22:04
 * @description
 */
@Data
@Accessors(chain = true)
public class TransactionModel {

    /**
     * ID
     */
    private Long id;

    /**
     * 交易流水号
     */
    private String transactionCode;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 交易类型
     */
    private Integer type;

    /**
     * 交易状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public TransactionModel(@NonNull Long id, @NonNull String transactionCode, @NonNull BigDecimal amount, @NonNull Integer type, @NonNull String status) {
        this.id = id;
        this.transactionCode = transactionCode;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

    public TransactionModel(@NonNull String transactionCode, @NonNull BigDecimal amount, @NonNull Integer type, @NonNull String status) {
        this.transactionCode = transactionCode;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

}
