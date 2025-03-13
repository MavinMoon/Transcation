package org.hometask.transactionmanagement.infrastructure.repository.impl;

import cn.hutool.core.util.ObjectUtil;
import org.hometask.transactionmanagement.domain.model.TransactionModel;
import org.hometask.transactionmanagement.domain.repository.ITransactionRepository;
import org.hometask.transactionmanagement.utility.exception.UtilException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.hometask.transactionmanagement.utility.exception.TransactionManagerResponseCode.TRANSACTION_NOT_FOUND;

@Repository
public class TransactionRepositoryImpl implements ITransactionRepository {

    // 存储所有交易记录
    private final Map<Long, TransactionModel> transactionToIdMap = new ConcurrentHashMap<>();

    private final Map<String, TransactionModel> transactionToTransactionCodeMap = new ConcurrentHashMap<>();

    // 用于生成自增ID
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public TransactionModel selectTransactionByTransactionCode(String transactionCode) {
        return transactionToTransactionCodeMap.get(transactionCode);
    }

    @Override
    public TransactionModel saveTransaction(TransactionModel model) {
        if (ObjectUtil.isNull(model.getId())) {
            model.setId(idGenerator.getAndIncrement());
        }
        transactionToIdMap.put(model.getId(), model);
        transactionToTransactionCodeMap.put(model.getTransactionCode(), model);
        return model;
    }

    @Override
    public void deleteTransaction(Long id) {
        if (!transactionToIdMap.containsKey(id)) {
            throw new UtilException(TRANSACTION_NOT_FOUND);
        }
        TransactionModel model = transactionToIdMap.get(id);
        transactionToIdMap.remove(id);
        transactionToTransactionCodeMap.remove(model.getTransactionCode());
    }

    @Override
    public TransactionModel selectTransactionById(Long id) {
        return transactionToIdMap.get(id);
    }

    @Override
    public TransactionModel updateTransaction(TransactionModel model) {
        Long id = model.getId();
        TransactionModel oldModel = transactionToIdMap.get(id);
        if (ObjectUtil.isNull(oldModel)) {
            throw new UtilException(TRANSACTION_NOT_FOUND);
        }
        transactionToIdMap.put(id, model);
        transactionToTransactionCodeMap.put(model.getTransactionCode(), model);
        return model;
    }

    @Override
    public List<TransactionModel> selectAllTransactions() {
        return transactionToIdMap.values().stream().toList();
    }

}
