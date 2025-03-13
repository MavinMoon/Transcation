package org.hometask.transactionmanagement.application.query;

import jakarta.annotation.Resource;
import org.hometask.transactionmanagement.application.converter.TransactionVOConverter;
import org.hometask.transactionmanagement.application.vo.TransactionVO;
import org.hometask.transactionmanagement.domain.model.TransactionModel;
import org.hometask.transactionmanagement.domain.repository.ITransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionQueryService {

    @Resource
    private ITransactionRepository transactionRepository;

    public List<TransactionVO> listAllTransactions() {
        List<TransactionModel> transactions = transactionRepository.selectAllTransactions();
        return transactions.stream().map(TransactionVOConverter.INSTANCE::toVO).toList();
    }
}
