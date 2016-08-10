/**
 * Created by dynamit on 7/28/16.
 */
package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


/**
 * This class implements setup table based on the configuration file named usually as 'database.config',using DatabaseDriver module
 * Be careful to use this class because it will CLEAR all the data before. Bear in mind to do the BACKUP for the old data!
 * @author Meng Yue
 * @since 2016/07/28
 * */
public class SetupProcess {
    /**
     * ConfigPath declares the path of the configuration file, normally named as 'database.config'
     * DBD is the DatabaseDriver we are using
     * shallPrintCommand is a switch controlling whether to print out the database operation log or not
     */
    public static String ConfigPath= "/home/dynamit/student/mengyue/drill/db_java/db_manager/config/database.config";
    public static DatabaseDriver DBD = new DatabaseDriver();
    public static Boolean shallPrintCommand = true;

    // Initialize and setup the table according to the database.config

    /**
     * <p>The whole process to setup a database</p>
     * <p>First it read infos from the database.config,
     * then tries to create every table it needs,
     * at last create columns for each table </p>
     */
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

    /**
     * <p>Setup the table</p>
     * <p> This function setup an empty table (without other columns).
     *  If the table has already existed, then drop the table and create a new one. </p>
     */
    public void setup_table(String tableName){
        if (table_exist(tableName)){
            DBD.sqlExecute(String.format("DROP TABLE %s CASCADE ", tableName),shallPrintCommand);
        }
        DBD.sqlExecute(String.format("CREATE TABLE %s()",tableName),shallPrintCommand);
    }


    /**
     * <p>Check if the table has already been there</p>
     */
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

