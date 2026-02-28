package com.aquaculture.service.impl;

import com.aquaculture.dto.request.ExpenseRequest;
import com.aquaculture.entity.Expense;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.ExpenseMapper;
import com.aquaculture.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private ExpenseMapper expenseMapper;

    @Override
    public Expense create(Long userId, ExpenseRequest request) {
        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setCategory(request.getCategory());
        expense.setCategoryLabel(request.getCategoryLabel());
        expense.setAmount(request.getAmount());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setDescription(request.getDescription());
        expenseMapper.insert(expense);
        log.info("创建支出记录: id={}, amount={}", expense.getId(), expense.getAmount());
        return expense;
    }

    @Override
    public List<Expense> getList(Long userId, String monthPrefix) {
        if (monthPrefix != null && !monthPrefix.isEmpty()) {
            return expenseMapper.findByUserIdAndMonth(userId, monthPrefix + "%");
        }
        return expenseMapper.findByUserId(userId);
    }

    @Override
    public Expense getById(Long id) {
        Expense expense = expenseMapper.findById(id);
        if (expense == null) {
            throw new BusinessException(404, "支出记录不存在");
        }
        return expense;
    }

    @Override
    public Expense update(Long id, ExpenseRequest request) {
        Expense expense = getById(id);
        expense.setCategory(request.getCategory());
        expense.setCategoryLabel(request.getCategoryLabel());
        expense.setAmount(request.getAmount());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setDescription(request.getDescription());
        expenseMapper.update(expense);
        log.info("更新支出记录: id={}", id);
        return expense;
    }

    @Override
    public void delete(Long id) {
        getById(id);
        expenseMapper.deleteById(id);
        log.info("删除支出记录: id={}", id);
    }
}
