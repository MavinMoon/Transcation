package org.hometask.transactionmanagement.application.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@Tag(name = "TransactionCreateDTO")
public class TransactionCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 交易流水号
     */
    @NotBlank(message = "交易流水号不能为空")
    private String transactionCode;

    /**
     * 交易金额
     */
    @NotNull(message = "交易金额不能为空")
    private BigDecimal amount;

    /**
     * 交易类型
     */
    @NotNull(message = "交易类型不能为空")
    private Integer type;

    /**
     * 交易状态
     */
    @NotNull(message = "交易状态不能为空")
    private String status;


}
