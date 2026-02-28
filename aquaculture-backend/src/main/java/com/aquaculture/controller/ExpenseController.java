package com.aquaculture.controller;

import com.aquaculture.dto.request.ExpenseRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Expense;
import com.aquaculture.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ApiResponse<Expense> create(HttpServletRequest request, @Valid @RequestBody ExpenseRequest expenseRequest) {
        Long userId = (Long) request.getAttribute("userId");
        Expense expense = expenseService.create(userId, expenseRequest);
        return ApiResponse.success("支出记录创建成功", expense);
    }

    @GetMapping
    public ApiResponse<List<Expense>> getList(HttpServletRequest request,
                                               @RequestParam(required = false) String month) {
        Long userId = (Long) request.getAttribute("userId");
        List<Expense> expenses = expenseService.getList(userId, month);
        return ApiResponse.success(expenses);
    }

    @GetMapping("/{id}")
    public ApiResponse<Expense> getDetail(@PathVariable Long id) {
        Expense expense = expenseService.getById(id);
        return ApiResponse.success(expense);
    }

    @PutMapping("/{id}")
    public ApiResponse<Expense> update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest expenseRequest) {
        Expense expense = expenseService.update(id, expenseRequest);
        return ApiResponse.success("支出记录更新成功", expense);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        expenseService.delete(id);
        return ApiResponse.success("支出记录删除成功", null);
    }
}
