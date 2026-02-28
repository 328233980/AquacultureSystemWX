package com.aquaculture.service;

import com.aquaculture.dto.request.CustomerRequest;
import com.aquaculture.entity.Customer;
import java.util.List;

public interface CustomerService {
    Customer create(Long userId, CustomerRequest request);
    List<Customer> getList(Long userId);
    Customer getById(Long id);
    Customer update(Long id, CustomerRequest request);
    void delete(Long id);
}
