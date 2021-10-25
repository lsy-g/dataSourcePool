package util;


import pool.ConnPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 获取连接的工具类
 *
 */
public class JdbcUtil {

    // 数据库连接池
    private static ConnPool connPool = new ConnPool();

    /**
     * 从池中获取一个连接
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException{
        return connPool.getConnection();
    }

    /**
     * 关闭连接
     */
    public static void closeConnection(Connection conn, Statement st, ResultSet rs) throws SQLException{
        // 关闭存储查询结果的ResultSet对象
        if(rs != null){
            rs.close();
        }

        // 关闭Statement对象
        if(st != null){
            st.close();
        }

        // 关闭连接,计数器减1
        if(conn != null){
            conn.close();
            connPool.getTotalSize().decrementAndGet();
            System.out.println(connPool.getTotalSize());
        }
    }

}

