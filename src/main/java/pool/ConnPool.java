package pool;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * 简单实现数据库连接池
 *
 */
public class ConnPool implements DataSource {

    private static LinkedList<Connection> connPool = new LinkedList<>();
    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    // 最大连接数
    private static int maxSize;
    // todo 超时回收非核心空闲连接
    private static int survivalTime;

    // 计数器，统计连接池连接数
    private static AtomicInteger totalSize= new AtomicInteger(0);;

    //在静态代码块中加载配置文件
    static{
        InputStream in = ConnPool.class.getClassLoader().getResourceAsStream("db.properties");
        Properties prop = new Properties();
        try {
            prop.load(in);
            driver = prop.getProperty("driver");
            url = prop.getProperty("url");
            user = prop.getProperty("user");
            password = prop.getProperty("password");
            //数据库连接池的初始化连接数的大小
            int  InitSize = Integer.parseInt(prop.getProperty("InitSize"));
            // 最大连接
            maxSize = Integer.parseInt(prop.getProperty("maxSize"));
            // 存活时间
            survivalTime = Integer.parseInt(prop.getProperty("survivalTime"));
            //加载驱动
            Class.forName(driver);
            for(int loop=0; loop<InitSize; loop++){
                Connection conn = DriverManager.getConnection(url, user, password);
                System.out.println("初始化数据库连接池，创建第 " + (loop + 1) +" 个连接，添加到池中");
                connPool.add(conn);
                totalSize.incrementAndGet();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取数据库连接
     */
    public Connection getConnection() throws SQLException {
        if(connPool.size() > 0){
            //从集合中获取一个连接
            Connection conn = connPool.removeFirst();
            //返回Connection的代理对象
            return conn;
        }else{
            if (totalSize.intValue()<maxSize){
                // 添加连接
                Connection conn = DriverManager.getConnection(url, user, password);
                connPool.add(conn);
                totalSize.incrementAndGet();
                return this.getConnection();
            }else {
                // 连接到上限
                throw new RuntimeException("数据库繁忙，稍后再试");
            }
        }
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public void setLoginTimeout(int seconds) throws SQLException {

    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public Object unwrap(Class iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class iface) throws SQLException {
        return false;
    }

    public Connection getConnection(String username, String password)
            throws SQLException {
        return null;
    }

    public static AtomicInteger getTotalSize() {
        return totalSize;
    }

    public static void setTotalSize(AtomicInteger totalSize) {
        ConnPool.totalSize = totalSize;
    }
}

