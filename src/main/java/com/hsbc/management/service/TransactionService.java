package com.hsbc.management.service;

import com.hsbc.management.common.dto.TransactionDTO;
import com.hsbc.management.common.dto.TransactionModifyDTO;
import com.hsbc.management.common.vo.TransactionVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface TransactionService {

    /**
     * 创建一个新的交易对象。
     *
     * @return 返回新创建的交易对象。
     */
    TransactionVO createTransaction(TransactionDTO dto);

    /**
     * 删除指定ID的交易记录。
     *
     * @param id 需要删除的交易记录的ID。
     */
    void deleteTransaction(Long id);

    /**
     * 修改交易信息
     *
     * @param id 交易ID
     * @param dto 交易信息对象，包含需要修改的交易信息
     * @return 修改后的交易信息对象
     */
    TransactionVO modifyTransaction(Long id, TransactionModifyDTO dto);

    /**
     * 获取所有交易记录。
     *
     * @return 包含所有交易记录的列表。
     */
    Page<TransactionVO> listAllTransactions(String transactionNo, Pageable pageable);

    /**
     * 根据交易ID获取对应的交易记录。
     *
     * @param id 交易记录的ID。
     * @return 包含交易记录的Optional对象，如果不存在则返回Optional.empty()。
     */
    Optional<TransactionVO> getTransactionById(Long id);
}
