import util.JdbcUtil;
import java.sql.Connection;
import java.sql.SQLException;


public class PoolTest {

    /**
     * 测试数据库连接池
     * @param args
     */
    public static void main(String[] args) {
        JdbcUtil util = new JdbcUtil();
        try {
            Connection conn = util.getConnection();
            if(conn != null){
                System.out.println("我得到了一个连接");
            }
            util.closeConnection(conn, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}