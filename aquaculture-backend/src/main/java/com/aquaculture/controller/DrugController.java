package com.aquaculture.controller;

import com.aquaculture.dto.request.DrugRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Drug;
import com.aquaculture.service.DrugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/drugs")
public class DrugController {
    @Autowired
    private DrugService drugService;

    @PostMapping
    public ApiResponse<Drug> create(HttpServletRequest request, @Valid @RequestBody DrugRequest drugRequest) {
        Long userId = (Long) request.getAttribute("userId");
        Drug drug = drugService.create(userId, drugRequest);
        return ApiResponse.success("药品配置创建成功", drug);
    }

    @GetMapping
    public ApiResponse<List<Drug>> getList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Drug> drugs = drugService.getList(userId);
        return ApiResponse.success(drugs);
    }

    @GetMapping("/{id}")
    public ApiResponse<Drug> getDetail(@PathVariable Long id) {
        Drug drug = drugService.getById(id);
        return ApiResponse.success(drug);
    }

    @PutMapping("/{id}")
    public ApiResponse<Drug> update(@PathVariable Long id, @Valid @RequestBody DrugRequest drugRequest) {
        Drug drug = drugService.update(id, drugRequest);
        return ApiResponse.success("药品配置更新成功", drug);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        drugService.delete(id);
        return ApiResponse.success("药品配置删除成功", null);
    }
}
