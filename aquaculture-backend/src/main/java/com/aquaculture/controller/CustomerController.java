package com.aquaculture.controller;

import com.aquaculture.dto.request.CustomerRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Customer;
import com.aquaculture.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ApiResponse<Customer> create(HttpServletRequest request, @Valid @RequestBody CustomerRequest customerRequest) {
        Long userId = (Long) request.getAttribute("userId");
        Customer customer = customerService.create(userId, customerRequest);
        return ApiResponse.success("客户创建成功", customer);
    }

    @GetMapping
    public ApiResponse<List<Customer>> getList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Customer> customers = customerService.getList(userId);
        return ApiResponse.success(customers);
    }

    @GetMapping("/{id}")
    public ApiResponse<Customer> getDetail(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        return ApiResponse.success(customer);
    }

    @PutMapping("/{id}")
    public ApiResponse<Customer> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest customerRequest) {
        Customer customer = customerService.update(id, customerRequest);
        return ApiResponse.success("客户更新成功", customer);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ApiResponse.success("客户删除成功", null);
    }
}
