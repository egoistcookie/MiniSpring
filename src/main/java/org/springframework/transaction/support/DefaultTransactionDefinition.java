package org.springframework.transaction.support;

import org.springframework.transaction.TransactionDefinition;

/**
 * TransactionDefinition 的默认实现
 */
public class DefaultTransactionDefinition implements TransactionDefinition {
    private int propagationBehavior = PROPAGATION_REQUIRED; // 默认传播行为
    private int isolationLevel = ISOLATION_DEFAULT;         // 默认隔离级别
    private int timeout = TIMEOUT_DEFAULT;                 // 默认超时时间
    private boolean readOnly = false;                      // 默认非只读

    @Override
    public int getPropagationBehavior() {
        return propagationBehavior;
    }

    public void setPropagationBehavior(int propagationBehavior) {
        this.propagationBehavior = propagationBehavior;
    }

    @Override
    public int getIsolationLevel() {
        return isolationLevel;
    }

    public void setIsolationLevel(int isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
