package org.hometask.transactionmanagement.domain.service;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import jakarta.annotation.Resource;
import lombok.NonNull;
import org.hometask.transactionmanagement.domain.model.TransactionModel;
import org.hometask.transactionmanagement.domain.repository.ITransactionRepository;
import org.hometask.transactionmanagement.utility.exception.UtilException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hometask.transactionmanagement.utility.exception.TransactionManagerResponseCode.DUPLICATE_TRANSACTION;
import static org.hometask.transactionmanagement.utility.exception.TransactionManagerResponseCode.TRANSACTION_NOT_FOUND;


@Service
public class TransactionDomainService {

    @Resource
    private ITransactionRepository transactionRepository;

    public TransactionModel createTransaction(@NonNull TransactionModel model) {
        if (ObjectUtil.isNotNull(transactionRepository.selectTransactionByTransactionCode(model.getTransactionCode()))) {
            throw new UtilException(DUPLICATE_TRANSACTION);
        }
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        return transactionRepository.saveTransaction(model);
    }

    public void removeTransaction(@NonNull Long id) {
        transactionRepository.deleteTransaction(id);
    }

    public TransactionModel modifyTransaction(@NonNull TransactionModel model) {
        TransactionModel oldModel = transactionRepository.selectTransactionByTransactionCode(model.getTransactionCode());
        if (ObjectUtil.isNotNull(oldModel) && !ObjectUtil.equals(oldModel.getId(), model.getId())) {
            throw new UtilException(DUPLICATE_TRANSACTION);
        }
        model.setUpdateTime(LocalDateTime.now());
        return transactionRepository.updateTransaction(model);
    }
}
