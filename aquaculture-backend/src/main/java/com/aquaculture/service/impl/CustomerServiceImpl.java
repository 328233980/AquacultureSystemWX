package com.aquaculture.service.impl;

import com.aquaculture.dto.request.CustomerRequest;
import com.aquaculture.entity.Customer;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.CustomerMapper;
import com.aquaculture.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public Customer create(Long userId, CustomerRequest request) {
        Customer customer = new Customer();
        customer.setUserId(userId);
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setRemark(request.getRemark());
        customerMapper.insert(customer);
        log.info("创建客户配置: id={}, name={}", customer.getId(), customer.getName());
        return customer;
    }

    @Override
    public List<Customer> getList(Long userId) {
        return customerMapper.findByUserId(userId);
    }

    @Override
    public Customer getById(Long id) {
        Customer customer = customerMapper.findById(id);
        if (customer == null) {
            throw new BusinessException(404, "客户不存在");
        }
        return customer;
    }

    @Override
    public Customer update(Long id, CustomerRequest request) {
        Customer customer = getById(id);
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setRemark(request.getRemark());
        customerMapper.update(customer);
        log.info("更新客户配置: id={}", id);
        return customer;
    }

    @Override
    public void delete(Long id) {
        getById(id);
        customerMapper.deleteById(id);
        log.info("删除客户配置: id={}", id);
    }
}
