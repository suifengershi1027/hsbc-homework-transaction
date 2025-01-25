package com.hsbc.management.controller;

import com.hsbc.management.common.BaseResult;
import com.hsbc.management.common.dto.TransactionDTO;
import com.hsbc.management.common.dto.TransactionModifyDTO;
import com.hsbc.management.common.vo.TransactionVO;
import com.hsbc.management.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * 获取所有交易记录
     *
     * @param transactionNo 交易编号，可选参数
     * @param pageable    分页参数
     * @return 包含交易记录的分页结果
     */
    @GetMapping
    public BaseResult<Page<TransactionVO>> listAllTransactions(
            @RequestParam(required = false) String transactionNo,
            @PageableDefault(page = 1, size = 10) Pageable pageable) {
        Pageable adjustedPageable = Pageable.ofSize(pageable.getPageSize()).withPage(pageable.getPageNumber() - 1);
        Page<TransactionVO> transactions = transactionService.listAllTransactions(transactionNo, adjustedPageable);
        return BaseResult.succeed(transactions);
    }

    /**
     * 根据交易ID获取交易信息
     *
     * @param id 交易ID
     * @return 包含交易信息的BaseResult对象
     */
    @GetMapping("/{id}")
    public BaseResult<TransactionVO> getTransactionById(@PathVariable Long id) {
        Optional<TransactionVO> transactionOptional = transactionService.getTransactionById(id);
        return BaseResult.succeed(transactionOptional.orElse(null));
    }

    /**
     * 创建交易
     *
     * @param dto 交易信息DTO对象
     * @return 包含创建成功后的交易信息的BaseResult对象
     */
    @PostMapping
    public BaseResult<TransactionVO> createTransaction(@RequestBody @Valid TransactionDTO dto) {
        TransactionVO vo = transactionService.createTransaction(dto);
        return BaseResult.succeed(vo);
    }

    /**
     * 删除交易记录
     *
     * @param id 交易记录的ID
     * @return 操作结果，成功返回BaseResult对象
     */
    @DeleteMapping("/{id}")
    public BaseResult deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return BaseResult.succeed();
    }

    /**
     * 修改交易记录
     *
     * @param id  交易记录的ID
     * @param dto 包含修改信息的DTO对象
     * @return 包含修改后的交易信息的BaseResult对象
     */
    @PutMapping("/{id}")
    public BaseResult<TransactionVO> modifyTransaction(@PathVariable Long id, @RequestBody @Valid TransactionModifyDTO dto) {
        TransactionVO vo = transactionService.modifyTransaction(id, dto);
        return BaseResult.succeed(vo);
    }
}
