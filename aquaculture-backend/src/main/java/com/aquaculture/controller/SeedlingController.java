package com.aquaculture.controller;

import com.aquaculture.dto.request.SeedlingRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Seedling;
import com.aquaculture.service.SeedlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/seedlings")
public class SeedlingController {
    @Autowired
    private SeedlingService seedlingService;

    @PostMapping
    public ApiResponse<Seedling> create(HttpServletRequest request, @Valid @RequestBody SeedlingRequest seedlingRequest) {
        Long userId = (Long) request.getAttribute("userId");
        Seedling seedling = seedlingService.create(userId, seedlingRequest);
        return ApiResponse.success("种苗配置创建成功", seedling);
    }

    @GetMapping
    public ApiResponse<List<Seedling>> getList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Seedling> seedlings = seedlingService.getList(userId);
        return ApiResponse.success(seedlings);
    }

    @GetMapping("/{id}")
    public ApiResponse<Seedling> getDetail(@PathVariable Long id) {
        Seedling seedling = seedlingService.getById(id);
        return ApiResponse.success(seedling);
    }

    @PutMapping("/{id}")
    public ApiResponse<Seedling> update(@PathVariable Long id, @Valid @RequestBody SeedlingRequest seedlingRequest) {
        Seedling seedling = seedlingService.update(id, seedlingRequest);
        return ApiResponse.success("种苗配置更新成功", seedling);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        seedlingService.delete(id);
        return ApiResponse.success("种苗配置删除成功", null);
    }
}
