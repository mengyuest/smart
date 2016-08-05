package util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dynamit on 8/4/16.
 */
public class RowDateChecker {

    public static DatabaseDriver DBD = new DatabaseDriver();

    //receive date and check if it has been in the main database already.
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
