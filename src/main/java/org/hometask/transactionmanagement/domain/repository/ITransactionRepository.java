package org.hometask.transactionmanagement.domain.repository;

import org.hometask.transactionmanagement.domain.model.TransactionModel;

import java.util.List;

/**
 * @author Mavin
 * @date 2025/3/13 0:12
 * @description
 */
public interface ITransactionRepository {

    /**
     * 根据交易流水号查询交易记录
     *
     * @param transactionCode 交易流水号
     * @return 交易记录
     */
    TransactionModel selectTransactionByTransactionCode(String transactionCode);

    /**
     * 保存交易记录
     *
     * @param model 交易记录
     * @return 交易记录
     */
    TransactionModel saveTransaction(TransactionModel model);

    /**
     * 删除交易记录
     *
     * @param id 交易记录ID
     */
    void deleteTransaction(Long id);

    /**
     * 根据ID查询交易记录
     *
     * @param id 交易记录ID
     * @return 交易记录
     */
    TransactionModel selectTransactionById(Long id);

    /**
     * 修改交易记录
     *
     * @param model 交易记录
     * @return 交易记录
     */
    TransactionModel updateTransaction(TransactionModel model);

    /**
     * 查询所有交易记录
     *
     * @return 交易记录列表
     */
    List<TransactionModel> selectAllTransactions();

}
