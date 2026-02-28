package com.aquaculture.dto.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    @NotBlank(message = "支出类别不能为空")
    private String category;
    
    private String categoryLabel;
    
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;
    
    @NotNull(message = "支出日期不能为空")
    private LocalDate expenseDate;
    
    private String description;
}
