package org.hometask.transactionmanagement.utility.exception;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = false)
@Data
public class UtilException extends RuntimeException {

    /**
     * 业务异常码
     */
    protected final transient IResponseCode exceptionCode;

    /**
     * 业务异常信息
     */
    protected final String message;

    public UtilException() {
        this(TransactionManagerResponseCode.UNKNOWN);
    }

    public UtilException(IResponseCode resultCode) {
        super(resultCode.getMessage());

        this.exceptionCode = resultCode;
        this.message = resultCode.getMessage();
    }

    public UtilException(String messagePattern, Object... arguments) {
        super(CharSequenceUtil.format(messagePattern, arguments));

        this.exceptionCode = TransactionManagerResponseCode.UNKNOWN;
        this.message = CharSequenceUtil.format(messagePattern, arguments);
    }


    public UtilException(Throwable throwable) {
        super(throwable);

        this.exceptionCode = TransactionManagerResponseCode.UNKNOWN;
        this.message = throwable.getMessage();
    }

    public UtilException(IResponseCode resultCode, String messagePattern, Object... arguments) {
        super(CharSequenceUtil.format(messagePattern, arguments));

        this.exceptionCode = resultCode;
        this.message = CharSequenceUtil.format(messagePattern, arguments);
    }


    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return CharSequenceUtil.format("{} code={} message={}", this.getClass().getSimpleName(), this.exceptionCode, this.message);
    }

}
