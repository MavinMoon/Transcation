package org.hometask.transactionmanagement.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import org.hometask.transactionmanagement.application.command.TransactionCommandService;
import org.hometask.transactionmanagement.application.dto.TransactionCreateDTO;
import org.hometask.transactionmanagement.application.dto.TransactionUpdateDTO;
import org.hometask.transactionmanagement.application.query.TransactionQueryService;
import org.hometask.transactionmanagement.application.vo.TransactionVO;
import org.hometask.transactionmanagement.utility.exception.UtilException;
import org.hometask.transactionmanagement.utility.model.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
//@Transactional 如果是在真实数据库中操作，需要使用该注解，确保测试后数据回滚
public class TransactionControllerTest {

    @Autowired
    private TransactionController controller;

    @Autowired
    private TransactionCommandService commandService;

    @Autowired
    private TransactionQueryService queryService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private TransactionVO mockTransactionVO;
    private TransactionCreateDTO mockCreateDTO;
    private TransactionUpdateDTO mockUpdateDTO;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockTransactionVO = new TransactionVO().setId(1L)
                .setTransactionCode("1234567890")
                .setAmount(BigDecimal.valueOf(100))
                .setType(1)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
                .setStatus("1");
        mockCreateDTO = new TransactionCreateDTO()
                .setTransactionCode("1234567890")
                .setAmount(BigDecimal.valueOf(100))
                .setType(1)
                .setStatus("1");
        mockUpdateDTO = new TransactionUpdateDTO()
                .setId(1L)
                .setTransactionCode("1234567890")
                .setAmount(BigDecimal.valueOf(100))
                .setType(1)
                .setStatus("1");
    }

    // 测试创建成功
    @Test
    public void should_returnTransactionVO_when_createTransaction_given_validDTO() throws Exception {
        // given

        // when
        MvcResult result = mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateDTO)))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        R<TransactionVO> response = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
        TransactionVO responseTransaction = response.getData();

        // then
        assertNotNull(responseTransaction, "Response transaction should not be null");
        assertNotNull(responseTransaction.getId(), "Transaction ID should not be null");
        assertEquals(mockCreateDTO.getTransactionCode(), responseTransaction.getTransactionCode(), "Transaction transactionCode should match");

        List<TransactionVO> transactions = queryService.listAllTransactions();
        assertEquals(mockCreateDTO.getTransactionCode(), transactions.get(0).getTransactionCode());

        // 删除数据，避免对其他case的影响
        commandService.removeTransaction(responseTransaction.getId());
    }

    // 测试创建重复异常
    @Test
    public void should_returnDuplicateException_when_createTransaction_given_duplicateTransactionCode() throws Exception {
        // given

        // when
        mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Exception exception = assertThrows(ServletException.class,
                () -> mockMvc.perform(post("/api/v1/transaction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockCreateDTO)))
                        .andExpect(status().isConflict())
                        .andReturn()
        );

        // then
        // 验证异常类型
        assertTrue(exception.getCause() instanceof UtilException);
        UtilException utilException = (UtilException) exception.getCause();
        assertEquals("999997", utilException.getExceptionCode().getCode());
    }

    // 测试删除交易成功
    @Test
    public void should_returnSuccess_when_removeTransaction_given_validId() throws Exception {
        // given
        TransactionVO mockVO = commandService.createTransaction(mockCreateDTO);// 先创建记录

        // when
        MvcResult result = mockMvc.perform(delete("/api/v1/transaction/{id}", mockVO.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<TransactionVO> transactions = queryService.listAllTransactions();
        assertTrue(transactions.stream().noneMatch(t -> t.getId().equals(mockVO.getId())));
    }

    // 测试修改交易
    @Test
    public void should_returnUpdatedVO_when_modifyTransaction_given_validDTO() throws Exception {
        // given
        TransactionVO existing = commandService.createTransaction(mockCreateDTO);// 先创建记录
        mockUpdateDTO.setAmount(BigDecimal.valueOf(200)); // 修改金额

        // when
        MvcResult result = mockMvc.perform(put("/api/v1/transaction/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUpdateDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        R<TransactionVO> response = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
        TransactionVO updated = response.getData();

        // then
        assertEquals(BigDecimal.valueOf(200), updated.getAmount());

        // 清理
        commandService.removeTransaction(existing.getId());
    }

    // 测试查询所有交易
    @Test
    public void should_returnTransactionList_when_listAllTransactions_given_validRequest() throws Exception {
        // given
        commandService.createTransaction(mockCreateDTO); // 先创建记录

        // when
        MvcResult result = mockMvc.perform(get("/api/v1/transaction/list"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        R<List<TransactionVO>> response = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
        List<TransactionVO> transactions = response.getData();

        // then
        assertEquals(1, transactions.size());
        assertEquals(mockCreateDTO.getTransactionCode(), transactions.get(0).getTransactionCode());

        // 清理
        commandService.removeTransaction(transactions.get(0).getId());
    }


}
