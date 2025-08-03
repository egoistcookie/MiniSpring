package com.example.service.impl;

import com.example.service.UserRepository;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    private DataSourceTransactionManager transactionManager;
    private TransactionDefinition definition;
    public void sayHello() {
        userRepository.save();
        System.out.println("Hello from UserService!");
    }

    // Setter 注入
    public void setTransactionManager(DataSourceTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setTransactionDefinition(TransactionDefinition definition) {
        this.definition = definition;
    }

    public void transferMoney() {
        TransactionStatus status = transactionManager.getTransaction(definition);
        try {
            // 执行业务逻辑...
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
