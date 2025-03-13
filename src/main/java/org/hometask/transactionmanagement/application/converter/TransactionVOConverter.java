package org.hometask.transactionmanagement.application.converter;

import org.hometask.transactionmanagement.application.vo.TransactionVO;
import org.hometask.transactionmanagement.domain.model.TransactionModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface TransactionVOConverter {

    TransactionVOConverter INSTANCE = Mappers.getMapper(TransactionVOConverter.class);

    TransactionVO toVO(TransactionModel model);

}
