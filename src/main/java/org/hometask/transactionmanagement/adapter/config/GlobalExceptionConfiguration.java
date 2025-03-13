package org.hometask.transactionmanagement.adapter.config;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hometask.transactionmanagement.utility.exception.IResponseCode;
import org.hometask.transactionmanagement.utility.exception.TransactionManagerResponseCode;
import org.hometask.transactionmanagement.utility.exception.UtilException;
import org.hometask.transactionmanagement.utility.model.R;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Objects;


@Slf4j
@ControllerAdvice
public class GlobalExceptionConfiguration {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class})
    @ResponseBody
    public R<String> handleBindException(HttpServletRequest req, BindException exception) {

        String defaultMessage = "以下参数不符合要求。";
        StringBuilder message = new StringBuilder(defaultMessage);
        List<ObjectError> errorList = exception.getAllErrors();
        for (ObjectError objectError : errorList) {
            if (objectError instanceof FieldError fieldError) {
                if (fieldError.getRejectedValue() == null || Objects.equals(fieldError.getRejectedValue(), "")) {
                    message.append(CharSequenceUtil.format("{}:{};", fieldError.getField(), objectError.getDefaultMessage()));
                } else {
                    message.append(CharSequenceUtil.format("{}:{}:{};", fieldError.getField(),
                            CharSequenceUtil.subPre(CharSequenceUtil.format("{}", fieldError.getRejectedValue()), 10)
                                    + "...", objectError.getDefaultMessage()));
                }
            } else {
                message.append(objectError.getDefaultMessage());
            }
        }

        if (message.toString().equals(defaultMessage)) {
            message = new StringBuilder(ExceptionUtils.getRootCauseMessage(exception));
        }

        R<String> result = R.fail(TransactionManagerResponseCode.INVALID_PARAM.getCode(), message.toString());

        log.error("------BindException------");
        log.error(ExceptionUtils.getRootCauseMessage(exception), exception);

        return result;
    }

    @ExceptionHandler({UtilException.class})
    @ResponseBody
    public R<String> handleUtilException(HttpServletResponse response, UtilException exception) {

        IResponseCode responseCode = exception.getExceptionCode();
        response.setStatus(responseCode.getHttpStatus());
        R<String> result = R.fail(responseCode.getCode(), exception.getMessage());

        log.error("------" + exception.getClass().getName() + "------");
        log.error(ExceptionUtils.getRootCauseMessage(exception), exception);

        return result;
    }
}
