package org.hometask.transactionmanagement.utility.exception;

import cn.hutool.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum TransactionManagerResponseCode implements IResponseCode {

    /**
     * code: 代码，国际化资源KEY
     * message: 错误提示信息
     * httpStatus: HTTP状态码
     */
    SUCCESS("200", "操作成功", HttpStatus.HTTP_OK),
    UNKNOWN("999999", "未知业务异常", HttpStatus.HTTP_INTERNAL_ERROR),
    INVALID_PARAM("999998", "参数校验失败", HttpStatus.HTTP_BAD_REQUEST),
    DUPLICATE_TRANSACTION("999997", "交易单已存在", HttpStatus.HTTP_CONFLICT),
    TRANSACTION_NOT_FOUND("999996", "交易单不存在", HttpStatus.HTTP_NOT_FOUND),


    ;

    private final String code;

    private final String message;

    private final Integer httpStatus;

}
