package org.springframework.transaction;

import org.springframework.aop.framework.MethodInterceptor;
import org.springframework.aop.framework.MethodInvocation;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionInterceptor implements MethodInterceptor {
    private final PlatformTransactionManager transactionManager;

    public TransactionInterceptor(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        try {
            // 执行目标方法
            Object result = invocation.proceed();
            transactionManager.commit(status);
            return result;
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
