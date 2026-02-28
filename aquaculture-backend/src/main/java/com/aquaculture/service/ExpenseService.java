package com.aquaculture.service;

import com.aquaculture.dto.request.ExpenseRequest;
import com.aquaculture.entity.Expense;
import java.util.List;

public interface ExpenseService {
    Expense create(Long userId, ExpenseRequest request);
    List<Expense> getList(Long userId, String monthPrefix);
    Expense getById(Long id);
    Expense update(Long id, ExpenseRequest request);
    void delete(Long id);
}
