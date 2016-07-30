/**
 * Created by dynamit on 7/28/16.
 */
package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class SetupProcess {
    public static String ConfigPath= "/home/dynamit/student/mengyue/drill/db_java/db_manager/config/database.config";
    public static DatabaseDriver DBD = new DatabaseDriver();
    public static Boolean shallPrintCommand = true;

    // Initialize and setup the table according to the database.config
    public void setup_database() {
        try {
            FileReader fr = new FileReader(ConfigPath);
            BufferedReader br = new BufferedReader(fr);
            String instring;
            String tableName="";
            while((instring = br.readLine()) != null){
                instring = Tool.unComment(instring.trim());
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
            Tool.println("Can't find file, terminate process!");
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

    //TODO: Update the file paths and other parameters from file
    public void UpdatePathFromFile(){

    }

    public static void main(String[] args) {
        SetupProcess dbs = new SetupProcess();

        DBD.UpdatePathFromFile();
        dbs.UpdatePathFromFile();

        DBD.connect();

        dbs.setup_database();

        DBD.disconnect();
    }


}

