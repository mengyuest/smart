package util;

import java.util.List;

/**
 * This is a simple module to check if the certain date record is inserted into the database.
 * Because currently, everyday has only one record, and the date has been set as Primary Key in the main table.
 * If in the future the date is not unique anymore, this class can be disposed.
 * @author Meng Yue
 * @since 2016/08/06
 */
public class RowDateChecker {

    public static DatabaseDriver DBD = new DatabaseDriver();

    public static void main(String[] args){
        if(args.length!=1){
            Tool.println("PARAMETER FAULT: should use only one parameter representing date");
            System.exit(1);
            return;
        }
        DBD.connect();

        System.out.println("Searching date "+args[0]);
        List list = DBD.sqlQuery(String.format("SELECT 1 FROM main where simulationdate='%s'",args[0].trim()),false);
        System.exit(list.size());
    }
}
