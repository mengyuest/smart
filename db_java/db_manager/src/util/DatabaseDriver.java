/**
 * Created by Meng Yue on 7/28/16.
 */

package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Paths;

//TODO: write driver config to the file
public class DatabaseDriver {
    static String PATH = "/home/dynamit/student/mengyue/drill/db_java/db_manager/config/param.config";
    static  Boolean RUN_LOCAL = true;
    static  String JDBC_DRIVER = "org.postgresql.Driver";
    static  String DB_LOCAL = "jdbc:postgresql://localhost:5432/dyna";
    static String USER_LOCAL = "dynamit";
    static  String PASS_LOCAL = "dynamitFM@09";
    static String DB_SERVER = "jdbc:postgresql://137.132.22.82:15063/dynamit-ms";
    static String USER_SERVER = "dynamitms";
    static  String PASS_SERVER ="supernetwork";

    Connection conn = null;



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

    public boolean connect() {
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);


            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            if(RUN_LOCAL){
                conn = DriverManager.getConnection(DB_LOCAL, USER_LOCAL, PASS_LOCAL);
            }
            else{
                conn = DriverManager.getConnection(DB_SERVER, USER_SERVER, PASS_SERVER);
            }
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
                Tool.println("THU> "+sqlCommand);
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
                Tool.println("THU> "+sqlCommand);
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
                Tool.println("THU> "+sqlCommand);
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
                Tool.println("THU> "+sqlCommand);
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

    //TODO: Update the file paths and other parameters from file
    public void UpdatePathFromFile(){
        try {
            FileInputStream f = new FileInputStream(PATH);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));

            String line = "";
            while ((line = b.readLine()) != null) {
                String realLine = Tool.unComment(line).trim();
                String[] segList = realLine.split("=");
                switch (segList[0].trim()) {
                    case "RUN_LOCAL":
                        RUN_LOCAL = (segList[1].toLowerCase().contains("true"));
                        break;
                    case "JDBC_DRIVER":
                        JDBC_DRIVER = Tool.unquote(segList[1]);
                        break;
                    case "DB_LOCAL":
                       DB_LOCAL = Tool.unquote(segList[1]);
                        break;
                    case "USER_LOCAL":
                        USER_LOCAL = Tool.unquote(segList[1]);
                        break;
                    case "PASS_LOCAL":
                        PASS_LOCAL = Tool.unquote(segList[1]);
                        break;
                    case "DB_SERVER":
                        DB_SERVER = Tool.unquote(segList[1]);
                        break;
                    case "USER_SERVER":
                        USER_SERVER = Tool.unquote(segList[1]);
                        break;
                    case "PASS_SERVER":
                        PASS_SERVER = Tool.unquote(segList[1]);
                        break;
                }
            }
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ResultSet rs = null;
        DatabaseDriver dbd = new DatabaseDriver();


        Tool.println(Paths.get(".").toAbsolutePath().normalize().toString());
        dbd.UpdatePathFromFile();

        dbd.connect();
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
