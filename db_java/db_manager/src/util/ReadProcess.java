package util;

import data.MYtime;

import java.io.*;

/**
 * <p>This is a module read record from database and then save the data to the file in exactly DynaMIT format</p>
 * <p>Basically, it convert six type of data, sourced from mitsim, historical and estimated od flow, and sensor, sen_flw and sen_spd</p>
 * @author Meng Yue
 * @since 2016/08/03
 */
public class ReadProcess {
    public static DatabaseDriver DBD = new DatabaseDriver();
    public static String[] bufferedOdPair;
    public static double bufferedFactor;
    public Boolean shallPrintSQL = false;


    public String queryResultString(String date, String tableName,String columnName){
        return DBD.sqlQuery(String.format("SELECT %s FROM %s WHERE simulationDate='%s'",columnName,tableName,date),Boolean.FALSE).get(0);
    }

    public String queryResultString(int id, String tableName,String columnName, String idName){
        return DBD.sqlQuery(String.format("SELECT %s FROM %s WHERE %s=%d",columnName,tableName,idName, id),Boolean.FALSE).get(0);
    }

    public String[] getOdPairArrayFromDemandFile(String dataStr){
        String[] segment = dataStr.split("\n");
        String[] titleArray = segment[0].trim().split(",");
        int count = titleArray.length;
        String[] returnArray = new String[count-1];
        for(int i=1;i<count;i++){
            returnArray[i-1]= titleArray[i];
        }
        return returnArray;
    }

    public void saveODFlowToFile(String dataStr, String filePath){
        String[] segment = dataStr.trim().split("\n");
        String[] titleArray = segment[0].trim().split(",");
        int colNum = titleArray.length;
        int factor = (int)(Double.parseDouble(titleArray[0]));

        int count = segment.length;


        try {
            Tool.IfNotExistThenCreateDir(filePath);
            BufferedWriter b = new BufferedWriter( new FileWriter(filePath));
            for(int i=1;i<count;i++){
                String[] dataArray = segment[i].trim().split(",");
                b.write(String.format("%s 0 %d\n",dataArray[0],factor));
                b.write("{\n");
                StringBuilder sb = new StringBuilder();
                for(int j=1;j<colNum;j++){
                    sb.append(String.format("{%s %s}\n",titleArray[j],dataArray[j].trim()));
                }
                b.write(sb.substring(0));
                b.write("}\n");
                b.write("\n");
            }
            b.write("<END>");
            b.close();
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public double[][] getEstimateOdFlowToArray(String dataStr) {
        bufferedOdPair = getOdPairArrayFromDemandFile(dataStr);
        bufferedFactor = Double.parseDouble(dataStr.split(",")[0]);
        String[] segment = dataStr.trim().split("\n");
        String[] subSegment = segment[1].trim().split(",");
        int subcount = subSegment.length;

        double[][] matrix = new double[segment.length - 1][subcount];

        for (int i = 1; i < segment.length; i++) {
            subSegment = segment[i].trim().split(",");
            for (int j = 0; j < subcount; j++) {
                matrix[i - 1][j] = Double.parseDouble(subSegment[j]);
            }
        }
        return matrix;
    }

    public String getEstimateOdFlowToString(double[][] matrix){
        StringBuilder sb= new StringBuilder();
        int count = bufferedOdPair.length;
        sb.append(bufferedFactor);
        for(int i=0;i<count;i++){
            sb.append(","+bufferedOdPair[i]);
        }
        sb.append("\n");
        int[] size = Tool.getSizeOfMatrix(matrix);
        for(int i=0;i<size[0];i++){
            sb.append((int)matrix[i][0]);
            for(int j=1;j<size[1];j++){
                double left = matrix[i][j] % 1.0;
                if (left <0.1)                {
                    sb.append(String.format(",%d",(int)matrix[i][j]));
                }else if(left>0.9){
                    sb.append(String.format(",%d",(int)matrix[i][j])+1);
                }else {
                    sb.append(String.format(",%.3f",matrix[i][j]));
                }
            }
            sb.append("\n");
        }
        return sb.substring(0);
    }

    public void saveEstimatedODFlowToFile(String dataStr, String rootDirPath, int maxEstIter, int minutes){

        String[] segment = dataStr.trim().split("\n");

        try{
            String startT = MYtime.generateDate(Integer.parseInt(segment[1].trim().split(",")[0]),":");
            String stopT = MYtime.generateDate(Integer.parseInt(segment[1].trim().split(",")[0])+minutes*60,":");
            String filePath = rootDirPath+String.format("/temp/estimatedOD[%s,%s]%d.dat",startT,stopT,maxEstIter);
            Tool.IfNotExistThenCreateDir(filePath);
            BufferedWriter b = new BufferedWriter( new FileWriter(filePath));
            String[] subSegment =segment[1].trim().split(",");
            int subcount = subSegment.length;
            for(int i=1;i<subcount;i++){
                b.write(subSegment[i]+"\n");
            }

            b.close();
            b=new BufferedWriter(new FileWriter(rootDirPath+"EOD.txt"));
            int count = segment.length;
            for(int i=2;i<count;i++){
                b.write(segment[i]+"\n");
            }
            b.close();

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveSensorToFile(String dataStr, String filePath){
        String[] segment = dataStr.trim().split("\n");
        int count = segment.length;
        try{
            Tool.IfNotExistThenCreateDir(filePath);
            BufferedWriter b = new BufferedWriter( new FileWriter(filePath));
            String[] subSegment =segment[1].trim().split(",");
            int subcount = subSegment.length;
            for(int i=1;i<count;i++){
                subSegment = segment[i].trim().split(",");
                b.write(subSegment[0]+" {\n");
                for(int j=0;j<subcount-1;j++){
                    b.write(String.format("  %d %d %s\n",j,1,subSegment[j+1]));
                }
                b.write("}\n");
                b.write("\n");
            }
            b.close();

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveSen_DataToFile(String dataStr,String fileName,int intervalValue, String rootDir){
        String[] segment = dataStr.trim().split("\n");
        int count = segment.length;
        int fileNum = (count-1)/intervalValue;
        try{
            Tool.IfNotExistThenCreateDir(rootDir+"out/");

            int subCount = segment[0].trim().split(",").length;
            StringBuilder sb = new StringBuilder();
            sb.append("0000");
            for(int k=1;k<subCount;k++){
                sb.append("\t"+(k-1));
            }
            sb.append("\n");

            for(int i=0;i<fileNum;i++){
                String startTime=MYtime.generateDate(Integer.parseInt(segment[i*intervalValue+1].trim().split(",")[0].trim())*60,"");
                String stopTime=MYtime.generateDate(Integer.parseInt(segment[i*intervalValue+1].trim().split(",")[0].trim())*60+intervalValue*60,"");
                String fullFileName = fileName +"_"+startTime+"-"+stopTime+".out";
                BufferedWriter b = new BufferedWriter( new FileWriter(rootDir +"out/"+ fullFileName));

                b.write(sb.substring(0));
                for(int j=0;j<intervalValue;j++){
                    b.write(segment[1+i*intervalValue+j].replace(",","\t")+"\n");
                }
                b.close();
            }
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public static void main(String[] args){
        ReadProcess rp = new ReadProcess();
        DBD.connect();
        System.out.println("length="+args.length);

        if(args.length!=2){
            return ;
        }
        System.out.println(args[0]);
        System.out.println(args[1]);
        String filePath = args[0];
        String date = args[1];


        Tool.IfNotExistThenCreateDir(filePath);

        int minutes= (MYtime.generateSeconds(rp.queryResultString(date,"main","simulationStopTime"),":")-
                MYtime.generateSeconds(rp.queryResultString(date,"main","simulationStartTime"),":"))/60/
                Integer.parseInt(rp.queryResultString(date,"main","intervalNum"));

        int dtaparamId = Integer.parseInt(rp.queryResultString(date,"main","dtaparamId"));
        int maxEstIter =  Integer.parseInt(rp.queryResultString(dtaparamId,"dtaparam","MaxEstIter","dtaparamId"));
        rp.saveODFlowToFile(rp.queryResultString(date,"main","histOD_flow"), filePath+"histOD.dat");
        rp.saveODFlowToFile(rp.queryResultString(date,"main","mitsimOD_flow"), filePath+"mitsimOD.dat");
        rp.saveODFlowToFile(rp.queryResultString(date,"main","estimatedOD"), filePath+"histOD_est.dat");
        rp.saveEstimatedODFlowToFile(rp.queryResultString(date,"main","estimatedOD"), filePath, maxEstIter, minutes);
        rp.saveSensorToFile(rp.queryResultString(date,"main","sensor_flow"), filePath+"sensor.dat");

        rp.saveSen_DataToFile(rp.queryResultString(date,"main","sen_flw"),"sen_flw_Est", minutes,filePath);
        rp.saveSen_DataToFile(rp.queryResultString(date,"main","sen_spd"),"sen_spd_Est", minutes,filePath);
        DBD.disconnect();
    }
}
