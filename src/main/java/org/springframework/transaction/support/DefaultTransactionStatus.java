package org.springframework.transaction.support;

import org.springframework.transaction.TransactionStatus;

/**
 * 默认事务状态实现
 */
public class DefaultTransactionStatus implements TransactionStatus {
    private final Object resource; // 实际存储 ConnectionHolder
    private final boolean newTransaction;
    private boolean rollbackOnly = false;

    public DefaultTransactionStatus(Object resource, boolean newTransaction) {
        this.resource = resource;
        this.newTransaction = newTransaction;
    }

    public Object getResource() {
        return resource;
    }

    @Override
    public boolean isNewTransaction() {
        return newTransaction;
    }

    @Override
    public void setRollbackOnly() {
        this.rollbackOnly = true;
    }

    @Override
    public boolean isRollbackOnly() {
        return rollbackOnly;
    }
}