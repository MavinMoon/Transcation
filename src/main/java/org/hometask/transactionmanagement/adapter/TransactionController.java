package org.hometask.transactionmanagement.adapter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.hometask.transactionmanagement.application.command.TransactionCommandService;
import org.hometask.transactionmanagement.application.dto.TransactionCreateDTO;
import org.hometask.transactionmanagement.application.dto.TransactionUpdateDTO;
import org.hometask.transactionmanagement.application.query.TransactionQueryService;
import org.hometask.transactionmanagement.application.vo.TransactionVO;
import org.hometask.transactionmanagement.utility.model.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
@Tag(name = "Transaction Management", description = "Transaction management APIs")
public class TransactionController {

    @Resource
    private TransactionCommandService transactionCommandService;

    @Resource
    private TransactionQueryService transactionQueryService;


    @Operation(summary = "Create a new transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "409", description = "Duplicate transaction"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public R<TransactionVO> createTransaction(@RequestBody TransactionCreateDTO dto) {
        TransactionVO vo = transactionCommandService.createTransaction(dto);
        return R.ok(vo);
    }

    @Operation(summary = "Delete a transaction by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public R<Void> removeTransaction(@PathVariable Long id) {
        transactionCommandService.removeTransaction(id);
        return R.ok();
    }

    @Operation(summary = "Modify an existing transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public R<TransactionVO> modifyTransaction(@PathVariable Long id, @RequestBody TransactionUpdateDTO dto) {
        TransactionVO vo = transactionCommandService.modifyTransaction(dto);
        return R.ok(vo);
    }

    @Operation(summary = "Get all transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all transactions")
    })
    @GetMapping("/list")
    public R<List<TransactionVO>> listAllTransactions() {
        List<TransactionVO> transactions = transactionQueryService.listAllTransactions();
        return R.ok(transactions);
    }


}
