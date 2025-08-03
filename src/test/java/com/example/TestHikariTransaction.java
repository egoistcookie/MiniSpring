package com.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

public class TestHikariTransaction {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext.xml");

        // 检查数据源
        Object dataSource = context.getBean("dataSource");
        System.out.println("DataSource bean: " + dataSource);
        System.out.println("DataSource class: " + dataSource.getClass());
        if (dataSource instanceof com.zaxxer.hikari.HikariDataSource) {
            com.zaxxer.hikari.HikariDataSource hikariDS = (com.zaxxer.hikari.HikariDataSource) dataSource;
            System.out.println("JDBC URL: " + hikariDS.getJdbcUrl());
        }

        // 获取事务管理器
        Object txManagerBean = context.getBean("transactionManager");
        System.out.println("TransactionManager bean: " + txManagerBean);
        System.out.println("TransactionManager class: " + txManagerBean.getClass());

        if (txManagerBean instanceof DataSourceTransactionManager) {
            DataSourceTransactionManager txManager = (DataSourceTransactionManager) txManagerBean;
            System.out.println("DataSource in txManager: " + txManager.getDataSource());
            if (txManager.getDataSource() != null) {
                System.out.println("DataSource class in txManager: " + txManager.getDataSource().getClass());
            }
        }

        PlatformTransactionManager txManager = (PlatformTransactionManager) context.getBean("transactionManager");

        // 开启事务
        TransactionStatus status = txManager.getTransaction(null);
        try {
            System.out.println("Transaction started with HikariCP");
            txManager.commit(status);
        } catch (Exception e) {
            txManager.rollback(status);
            e.printStackTrace();
        }

        context.close();
    }
}
