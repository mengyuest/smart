package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by dynamit on 8/8/16.
 */
public class EvaluateProcess {
    public static ReadProcess rp = new ReadProcess();

    public static LinkedList<Double> lld = new LinkedList<>();

    public static double calculateError(String date){
        double error = 0;
        String sensorStr = rp.queryResultString(date, "main", "sensor_flow");
        String sen_flwStr = rp.queryResultString(date, "main", "sen_flw");
        double sum=0;
        int count = 0;
        String[] sensorStr_seg = sensorStr.trim().split("\n");
        String[] sen_flwStr_seg = sen_flwStr.trim().split("\n");
        int time_count = sensorStr_seg.length-1;
        int sensor_count = sensorStr_seg[0].split(",").length-1;
        double uppon = 0;
        double down = 0;
        for(int i=1;i<time_count;i++){
            String[] sensorValueList = sensorStr_seg[i].trim().split(",");
            String[] sen_flwValueList1 = sen_flwStr_seg[(i-1)*5+1].trim().split(",");
            String[] sen_flwValueList2 = sen_flwStr_seg[(i-1)*5+2].trim().split(",");
            String[] sen_flwValueList3 = sen_flwStr_seg[(i-1)*5+3].trim().split(",");
            String[] sen_flwValueList4 = sen_flwStr_seg[(i-1)*5+4].trim().split(",");
            String[] sen_flwValueList5 = sen_flwStr_seg[(i-1)*5+5].trim().split(",");
            for(int j=1;j<sensor_count;j++){
                double d1 = Double.parseDouble(sensorValueList[j]);
                double d2=0;
                d2 += Double.parseDouble(sen_flwValueList1[j]);
                d2 += Double.parseDouble(sen_flwValueList2[j]);
                d2 += Double.parseDouble(sen_flwValueList3[j]);
                d2 += Double.parseDouble(sen_flwValueList4[j]);
                d2 += Double.parseDouble(sen_flwValueList5[j]);
                d2 =d2/60;
                sum += (d1-d2)*(d1-d2);
                down += (d1/2+d2/2);
            }
        }
        uppon = Math.sqrt(sum*(time_count)*sensor_count);
        error =  uppon/down;//RMSN version
        //error = sum/time_count/sensor_count; //SSD version
        return error;
    }




    public static void main(String[] args)throws IOException {

        rp.DBD.connect();


        BufferedWriter b = new BufferedWriter( new FileWriter("/home/dynamit/student/mengyue/drill/test/result.dat"));

        for(int i=1;i<=4;i++){
            for(int j=1;j<=10;j++){
                String date;
                if(j!=10) {
                    date = String.format("2016/0%d/0%d", i,j);
                }
                else{
                    date = String.format("2016/0%d/10", i);
                }
                double result = calculateError(date);
                b.write(String.format("%f ",result));
                Tool.println(result);
            }
            Tool.println("");
            Tool.println("");
        }

        b.close();



        rp.DBD.disconnect();

    }
}
