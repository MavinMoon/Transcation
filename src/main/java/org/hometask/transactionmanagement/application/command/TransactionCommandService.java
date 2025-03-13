package org.hometask.transactionmanagement.application.command;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hometask.transactionmanagement.application.converter.TransactionVOConverter;
import org.hometask.transactionmanagement.application.dto.TransactionCreateDTO;
import org.hometask.transactionmanagement.application.dto.TransactionUpdateDTO;
import org.hometask.transactionmanagement.application.vo.TransactionVO;
import org.hometask.transactionmanagement.domain.model.TransactionModel;
import org.hometask.transactionmanagement.domain.service.TransactionDomainService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class TransactionCommandService {

    @Resource
    private TransactionDomainService transactionDomainService;

    public TransactionVO createTransaction(@NonNull @Valid TransactionCreateDTO dto) {
        // 后续可用设计模式替代
        TransactionModel model = new TransactionModel(dto.getTransactionCode(), dto.getAmount(), dto.getType(), dto.getStatus());
        model = transactionDomainService.createTransaction(model);
        return TransactionVOConverter.INSTANCE.toVO(model);
    }

    public void removeTransaction(@NotNull(message = "id不能为空") Long id) {
        transactionDomainService.removeTransaction(id);
    }

    public TransactionVO modifyTransaction(@NonNull @Valid TransactionUpdateDTO dto) {
        TransactionModel model = new TransactionModel(dto.getId(), dto.getTransactionCode(), dto.getAmount(), dto.getType(), dto.getStatus());
        model = transactionDomainService.modifyTransaction(model);
        return TransactionVOConverter.INSTANCE.toVO(model);
    }
}
