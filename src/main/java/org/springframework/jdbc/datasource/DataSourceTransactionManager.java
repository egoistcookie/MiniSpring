package org.springframework.jdbc.datasource;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceTransactionManager implements PlatformTransactionManager {
    private DataSource dataSource;

    // 确保有公共无参构造函数
    public DataSourceTransactionManager() {
        System.out.println("DataSourceTransactionManager default constructor called");
    }

    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
        System.out.println("DataSourceTransactionManager created with dataSource: " + dataSource);
    }

    // Setter 注入（XML 配置使用）
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        System.out.println("DataSource set: " + dataSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void beginTransaction() throws SQLException {
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        // 通常通过 TransactionStatus 保存连接状态（此处简化）
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) {
        try {
            // 如果未传入定义，使用默认配置
            TransactionDefinition def = (definition != null ? definition : new DefaultTransactionDefinition());

            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            // 设置隔离级别（如果非默认）
            if (def.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
                connection.setTransactionIsolation(def.getIsolationLevel());
            }

            // 创建ConnectionHolder并将其存储在TransactionStatus中
            ConnectionHolder connectionHolder = new ConnectionHolder(connection);
            return new DefaultTransactionStatus(connectionHolder, true);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to begin transaction", e);
        }
    }

    @Override
    public void commit(TransactionStatus status) {
        DefaultTransactionStatus txStatus = (DefaultTransactionStatus) status;
        Object resource = txStatus.getResource();

        try {
            if (resource instanceof ConnectionHolder) {
                ConnectionHolder holder = (ConnectionHolder) resource;
                holder.getConnection().commit();
            } else if (resource instanceof Connection) {
                // 直接是Connection对象的情况
                ((Connection) resource).commit();
            } else {
                throw new RuntimeException("Unknown resource type: " +
                        (resource != null ? resource.getClass().getName() : "null"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Commit failed", e);
        } finally {
            releaseConnection(resource);
        }
    }

    @Override
    public void rollback(TransactionStatus status) {
        DefaultTransactionStatus txStatus = (DefaultTransactionStatus) status;
        Object resource = txStatus.getResource();

        try {
            if (resource instanceof ConnectionHolder) {
                ConnectionHolder holder = (ConnectionHolder) resource;
                holder.getConnection().rollback();
            } else if (resource instanceof Connection) {
                // 直接是Connection对象的情况
                ((Connection) resource).rollback();
            } else {
                throw new RuntimeException("Unknown resource type: " +
                        (resource != null ? resource.getClass().getName() : "null"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Rollback failed", e);
        } finally {
            releaseConnection(resource);
        }
    }

    private void releaseConnection(Object resource) {
        try {
            Connection connection = null;
            if (resource instanceof ConnectionHolder) {
                connection = ((ConnectionHolder) resource).getConnection();
            } else if (resource instanceof Connection) {
                connection = (Connection) resource;
            }

            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {
            // 忽略关闭异常
        }
    }
}