
/**
 * Created by MENG YUE on 7/24/16.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class db_setup{
    public static String ConfigPath= "/home/dynamit/student/mengyue/drill/database.config";
    public static db_driver DBD = new db_driver();
    public static Boolean shallPrintCommand = true;

    public void println(String a){
        System.out.println(a);
    }



    // Initialize and setup the table according to the database.config
    public void setup_database() {
        try {
            FileReader fr = new FileReader(ConfigPath);
            BufferedReader br = new BufferedReader(fr);
            String instring;
            String tableName="";
            while((instring = br.readLine()) != null){
                instring = unComment(instring.trim());
                if(0 != instring.length()){
                    String[] strArray = instring.split("\"");
                    int count = strArray.length;
                    if(count < 2) {
                        continue;
                    }
                    else if(count == 2){
                        tableName=strArray[1];
                        setup_table(tableName);
                    }
                    else{
                        String colName = strArray[1];
                        String colType = strArray[3];
                        String command = String.format("ALTER TABLE %s ADD %s %s ",tableName,colName,colType);
                        DBD.sqlUpdate(command,shallPrintCommand);
                    }
                }
            }
        }catch(IOException ioe){
            println("Can't find file, terminate process!");
            return;
        }

    }

    //Setup the table (only, no other columns), checking if it exists first.
    public void setup_table(String tableName){
        if (table_exist(tableName)){
            DBD.sqlExecute(String.format("DROP TABLE %s CASCADE ", tableName),shallPrintCommand);
        }
        DBD.sqlExecute(String.format("CREATE TABLE %s()",tableName),shallPrintCommand);
    }

    // Check if the table has already been there
    public boolean table_exist(String tableName){
        tableName = tableName.toLowerCase();
        List<String> result = DBD.sqlQuery(String.format("SELECT COUNT(*) FROM pg_tables WHERE tablename='%s' ",tableName),shallPrintCommand);
        if (0==Integer.parseInt(result.get(0))){
            return false;
        }
        return true;
    }

    public String unComment(String str){
        if(str!=null && (str.contains("//") || str.contains("#"))){
            int index = str.length();
            int index1 = str.indexOf('/');
            int index2 = str.indexOf('#');
            if(index1 >= 0 && index1 < index-1){
                index = index1+1;
            }
            if(index2 >= 0 && index2 < index-1){
                index = index2+1;
            }
            str = str.substring(0,index);
        }
        return str;
    }

    // Show (part of) the table
    public void show_database(){

    }


    public static void main(String[] args) {
        db_setup dbs = new db_setup();

        DBD.connect(db_driver.DB_LOCAL);

        dbs.setup_database();

        dbs.show_database();

        DBD.disconnect();
    }


}
