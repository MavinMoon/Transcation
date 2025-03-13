package org.hometask.transactionmanagement.utility.exception;

public interface IResponseCode {

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    String getCode();


    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    String getMessage();

    /**
     * 获取http状态码
     *
     * @return http状态码
     */
    Integer getHttpStatus();
}
