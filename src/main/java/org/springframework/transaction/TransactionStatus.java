package org.springframework.transaction;

// 事务状态
public interface TransactionStatus {
    boolean isNewTransaction(); // 是否是新事务
    void setRollbackOnly();     // 标记为仅回滚
    boolean isRollbackOnly();   // 是否必须回滚
}
