package com.aquaculture.controller;

import com.aquaculture.dto.request.SupplierRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Supplier;
import com.aquaculture.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public ApiResponse<Supplier> create(HttpServletRequest request, @Valid @RequestBody SupplierRequest supplierRequest) {
        Long userId = (Long) request.getAttribute("userId");
        Supplier supplier = supplierService.create(userId, supplierRequest);
        return ApiResponse.success("供应商创建成功", supplier);
    }

    @GetMapping
    public ApiResponse<List<Supplier>> getList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Supplier> suppliers = supplierService.getList(userId);
        return ApiResponse.success(suppliers);
    }

    @GetMapping("/{id}")
    public ApiResponse<Supplier> getDetail(@PathVariable Long id) {
        Supplier supplier = supplierService.getById(id);
        return ApiResponse.success(supplier);
    }

    @PutMapping("/{id}")
    public ApiResponse<Supplier> update(@PathVariable Long id, @Valid @RequestBody SupplierRequest supplierRequest) {
        Supplier supplier = supplierService.update(id, supplierRequest);
        return ApiResponse.success("供应商更新成功", supplier);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ApiResponse.success("供应商删除成功", null);
    }
}
