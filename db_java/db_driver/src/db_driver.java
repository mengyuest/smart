/**
 * Created by Meng Yue on 7/24/16.
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class db_driver{
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_LOCAL = "jdbc:postgresql://localhost:5432/dyna";//jdbc:postgresql://137.132.22.82:15063/dynamit-ms
    static final String USER = "dynamit";//"dynamitms"
    static final String PASS = "dynamitFM@09";//"supernetwork"


    Connection conn = null;

    public void println(String str){
        System.out.println(str);
    }

    public void println(int n){
        System.out.println(n);
    }

    public void println(Boolean b){
        System.out.println(b);
    }

    public void print(String str){
        System.out.print(str);
    }

    public void print(int n){
        System.out.print(n);
    }

    public void print(Boolean b){
        System.out.print(b);
    }

    // Should we use finalize???
    protected void finalize() throws Throwable {
        try {
            if (!conn.isClosed()) {
                System.err.println("Woops, you forgot to close db connection, I'm closing it..");
                conn.close();
                conn = null;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public boolean connect(String url) {
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            if (url.isEmpty())
                url = DB_LOCAL;

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(url, USER, PASS);
            System.out.println("Database connected.");

            return true;
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void disconnect() {
        try {
            conn.close();
            System.out.println("Database disconnected.");
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ResultSetConsumer<T>{
        T consume(ResultSet rs);
    }

    public String consume(ResultSet rs) {
        if(rs!=null)
            return rs.toString();
        return "";
    }

    public void sqlExecute(String sqlCommand, Boolean printCommand){
        Statement st = null;
        try{
            st = conn.createStatement();
            st.execute(sqlCommand);
            st.close();
            if (printCommand){
                println("THU> "+sqlCommand);
            }
        }catch(SQLException se){
            se.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
            if(st!=null)
                st.close();
            }catch (SQLException se){
                se.printStackTrace();
            }
        }
    }

    public int sqlUpdate(String sqlCommand, Boolean printCommand){
        int num=0;
        PreparedStatement st=null;
        try{
            st = conn.prepareStatement(sqlCommand);
            num = st.executeUpdate();
            if (printCommand){
                println("THU> "+sqlCommand);
            }
        }catch(SQLException se){
            se.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try{
                if(st != null)
                    st.close();
            }catch (SQLException se){
                se.printStackTrace();
            }
            return num;
        }
    }

    public List<String> sqlQuery(String sqlCommand, Boolean printCommand) {
        Statement stmt = null;
        ResultSet rs = null;
        List<String> resultList=null;
        try {
            int columnCount = 0;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlCommand);

            resultList = new ArrayList<String>();
            if(rs!=null) {
                ResultSetMetaData metadata = rs.getMetaData();
                columnCount = metadata.getColumnCount();
            }
            while (rs.next()) {
                String row = "";
                for (int i =1; i<= columnCount;i++){
                    row += rs.getString(i)+",";
                }
                resultList.add(row.substring(0,row.length()-1));
            }
            if (printCommand){
                println("THU> "+sqlCommand);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(rs!= null)
                    rs.close();
            }catch (SQLException se){
                se.printStackTrace();
            }

            try{
                if(stmt != null)
                    stmt.close();
            }catch (SQLException se){
                se.printStackTrace();
            }
            return resultList;
        }
    }


    public <T> List<T> sqlQuery(String sqlCommand, ResultSetConsumer<T> consumer, Boolean printCommand) {
        Statement stmt = null;
        ResultSet rs = null;
        List<T> resultList=null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlCommand);

            if(consumer!=null) {
                resultList = new ArrayList<T>();
                while (rs.next()) {
                    resultList.add(consumer.consume(rs));
                }
            }
            if (printCommand){
                println("THU> "+sqlCommand);
            }

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(rs!= null)
                    rs.close();
            }catch (SQLException se){
                se.printStackTrace();
            }

            try{
                if(stmt != null)
                    stmt.close();
            }catch (SQLException se){
                se.printStackTrace();
            }
            return resultList;
        }
    }



    public static void main(String[] args) {
        ResultSet rs = null;
        db_driver dbd = new db_driver();

        dbd.connect(db_driver.DB_LOCAL);
        dbd.sqlExecute("CREATE TABLE newOne()",true);
        System.out.println(dbd.sqlUpdate("ALTER TABLE newOne ADD name VARCHAR(20) ",true));
        System.out.println(dbd.sqlUpdate("ALTER TABLE newOne ADD grade int ",true));
        System.out.println(dbd.sqlUpdate("INSERT INTO newOne VALUES('MENG',100)",true));
        System.out.println(dbd.sqlUpdate("INSERT INTO newOne VALUES('YUE',90)",true));
        System.out.println(dbd.sqlQuery("SELECT * FROM newOne",true));
        System.out.println(dbd.sqlQuery("SELECT COUNT(1) FROM pg_tables  WHERE tablename='newone' ",true));
        dbd.sqlExecute("DROP TABLE newOne",true);
        dbd.disconnect();
    }

}
