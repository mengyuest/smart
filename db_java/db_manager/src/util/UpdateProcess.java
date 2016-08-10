package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;

/**
 * This is the module implementing the update process method.
 * The dictionary is just for remember the index order of date simply.
 * It should be altered or changed into more proper structure in the future.
 * The fifth method seems to have a little problem in the simulation. It shall be fixed before use.
 * @author  Meng Yue
 * @since 2016/08/06
 */
public class UpdateProcess {

    public static int WindowSize = 3;
    public static double Alpha = 0.2;
    public static String CurrDate = "2016/02/01";
    public static String DynaMITpath = "/home/dynamit/student/mengyue/drill/test/DynaMIT/";
    public static String backupHODfile = "demand_backup.dat";
    public static String tempHODfile="demand_temp.dat";
    public static String targetHODname = "demand_DynaMIT_hist_nZero_pert_Gaussian_BN5.dat";

    public static HashMap<String,String> prevDate = new HashMap<>();

    public static ReadProcess rp = new ReadProcess();
    //TODO: get windowSize and alpha from config file
    public static void getParamsFromFile(){
        String[] dateList = new String[50];
        for(int i=1;i<=5;i++){
            for(int j=1;j<=10;j++){
                if(j==10){
                    dateList[i*10-1] = "2016/0"+i+"/10";
                }else {
                    dateList[(i - 1) * 10 + j - 1] = String.format("2016/0%d/0%d", i, j);
                }
            }
        }
        for(int i=0;i<50;i++)
        {
            if(i%10==0){
                prevDate.put(dateList[i],dateList[9]);
            }
            else {
                prevDate.put(dateList[i],dateList[i-1]);
            }
        }
    }

    public static void copyFile(String source, String destination) throws IOException{
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;

        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(destination).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }

    }

    //Every time use same historical data
    //This copy of historical data comes from demand_backup.dat
    public static void fixedHistoricalMethod() throws IOException {
        copyFile(DynaMITpath+backupHODfile,DynaMITpath+targetHODname);
    }

    //get the last date of historical data
    public static void lastEstimatedODMethod()throws IOException {
        rp.saveODFlowToFile(rp.queryResultString(prevDate.get(CurrDate), "main", "estimatedOD"), DynaMITpath + tempHODfile);
        copyFile(DynaMITpath+tempHODfile,DynaMITpath+targetHODname);
    }

    public static void simpleMoveAverageMethod()throws IOException{
        double[][][] matrixList = new double[WindowSize][][];
        String cursor = CurrDate;
        for(int i=0;i<WindowSize;i++){
            cursor = prevDate.get(cursor);
            matrixList[i] = rp.getEstimateOdFlowToArray(rp.queryResultString(cursor,"main","estimatedOD"));
        }
        int[] size = Tool.getSizeOfMatrix(matrixList[0]);
        double[][] result = new double[size[0]][size[1]];

        for(int i=0;i<size[0];i++){
            for(int j=1;j<size[1];j++){
                for(int k=0;k<WindowSize;k++){
                    result[i][j] += matrixList[k][i][j];
                }
                result[i][j] = result[i][j]/WindowSize;
            }
            result[i][0] = matrixList[0][i][0];
        }

        rp.saveODFlowToFile(rp.getEstimateOdFlowToString(result),DynaMITpath+tempHODfile);
        copyFile(DynaMITpath+tempHODfile,DynaMITpath+targetHODname);
    }


    public static void exponentialMoveAverageMethod()throws IOException{
        double[][][] matrixList = new double[WindowSize][][];
        String cursor = CurrDate;
        for(int i=0;i<WindowSize;i++){
            cursor = prevDate.get(cursor);
            matrixList[i] = rp.getEstimateOdFlowToArray(rp.queryResultString(cursor,"main","estimatedOD"));
        }
        int[] size = Tool.getSizeOfMatrix(matrixList[0]);
        double[][] result = new double[size[0]][size[1]];

        for(int i=0;i<size[0];i++){
            for(int j=1;j<size[1];j++){
                for(int k=0;k<WindowSize;k++) {
                    result[i][j] = matrixList[k][i][j] * Alpha + result[i][j] * (1 - Alpha);
                }
            }
            result[i][0] = matrixList[0][i][0];
        }

        rp.saveODFlowToFile(rp.getEstimateOdFlowToString(result),DynaMITpath+tempHODfile);
        copyFile(DynaMITpath+tempHODfile,DynaMITpath+targetHODname);
    }

    public static void averageMethod()throws IOException{
        int index = Integer.parseInt(CurrDate.substring(CurrDate.length()-2));
        double[][] result;
        if(index==1) {
            double[][][] matrixList = new double[10][][];
            String cursor = CurrDate;
            for(int i=0;i<10;i++){
                cursor = prevDate.get(cursor);
                matrixList[i] = rp.getEstimateOdFlowToArray(rp.queryResultString(cursor,"main","estimatedOD"));
            }
            int[] size = Tool.getSizeOfMatrix(matrixList[0]);
            result = new double[size[0]][size[1]];

            for (int i = 0; i < size[0]; i++) {
                for (int j = 1; j < size[1]; j++) {
                    for(int k=0;k<10;k++) {
                        result[i][j] += matrixList[k][i][j];
                    }
                    result[i][j] = result[i][j]/10;
                }

                result[i][0] = matrixList[0][i][0];
            }

        }
        else{
            double[][] matrix;
            double[][] lastMatrix;
            matrix = rp.getEstimateOdFlowToArray(rp.queryResultString(prevDate.get(CurrDate),"main","estimatedOD"));
            lastMatrix = rp.getEstimateOdFlowToArray(rp.queryResultString(prevDate.get(CurrDate),"main","histOD_flow"));
            int[] size = Tool.getSizeOfMatrix(matrix);
            result = new double[size[0]][size[1]];

            for (int i = 0; i < size[0]; i++) {
                for (int j = 1; j < size[1]; j++) {
                        result[i][j] = matrix[i][j]/(index+9) + lastMatrix[i][j]*(index+8)/(index+9);
                }

                result[i][0] = matrix[i][0];
            }
        }
        rp.saveODFlowToFile(rp.getEstimateOdFlowToString(result),DynaMITpath+tempHODfile);
        copyFile(DynaMITpath+tempHODfile,DynaMITpath+targetHODname);
    }

    public static void main(String[] args)throws IOException {
        //if(args.length==0) {return;}
        getParamsFromFile();
        ReadProcess.DBD.connect();
        CurrDate = args[0];//"2016/05/03";//args[0];
        int algorType = Integer.parseInt(CurrDate.trim().split("/")[1]);
        switch (algorType){
            case 1:
                fixedHistoricalMethod();
                break;
            case 2:
                lastEstimatedODMethod();
                break;
            case 3:
                simpleMoveAverageMethod();
                break;
            case 4:
                exponentialMoveAverageMethod();
                break;
            case 5:
                averageMethod();
                break;
        }
        ReadProcess.DBD.disconnect();

    }
}
