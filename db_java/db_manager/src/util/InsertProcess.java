/**
 * Created by dynamit on 7/28/16.
 */

package util;

import java.lang.reflect.Type;
import java.util.regex.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.*;

import java.text.*;
public class InsertProcess {

    public static DatabaseDriver DBD = new DatabaseDriver();
    public Boolean shallPrintSQL = false;

    private static String dtaparamPath = "/home/dynamit/student/mengyue/drill/DynaMIT/dtaparam.dat";
    private static String networkPath = "/home/dynamit/student/mengyue/drill/DynaMIT/july_demo_network_v11.dat";
    private static String behaviorPath = "/home/dynamit/student/mengyue/drill/DynaMIT/BehavioralParameters.dat";
    private static String supplyparamPath = "/home/dynamit/student/mengyue/drill/DynaMIT/supplyparam.dat";
    private static String sensorPath="/home/dynamit/student/mengyue/drill/DynaMIT/sensor.dat";
    private static String histODCsvPath = "/home/dynamit/student/mengyue/drill/DynaMIT/demand_DynaMIT_july_demo_5min_Final7.csv";
    private static String mitsimODCsvPath = "/home/dynamit/student/mengyue/drill/DynaMIT/demand_DynaMIT_july_demo_5min_Final8.csv";
    private static String histODPath = "/home/dynamit/student/mengyue/drill/DynaMIT/demand_DynaMIT_july_demo_5min_Final7.dat";
    private static String mitsimODPath = "/home/dynamit/student/mengyue/drill/DynaMIT/demand_DynaMIT_july_demo_5min_Final8.dat";
    private static String configPath="/home/dynamit/student/mengyue/drill/db_java/db_manager/config/database.config";
    private static String odFlowPath="/home/dynamit/student/mengyue/drill/DynaMIT/temp/";
    private static String sensorDataPath="/home/dynamit/student/mengyue/drill/DynaMIT/";
    private static String sen_path="/home/dynamit/student/mengyue/drill/DynaMIT/output/";

    private List<Integer> id_list = new LinkedList<Integer>();
    private int intervalNum;
    private int intervalValue;
    private int MaxEstIter = 1;
    private String simuStartTimeStr;
    private String simuStopTimeStr;


    private DtaparamData dtaparamData;
    private NetworkData networkData;
    private BehaviorData behaviorData;
    private SupplyData supplyData;
    private MainRecord mainRecord;




    public static void LoadFilePath(String filePathPath){
        if(filePathPath==null || filePathPath.length()==0){
            return;
        }
        else{
            try{
                FileInputStream f= new FileInputStream(filePathPath);
                BufferedReader b= new BufferedReader(new InputStreamReader(f));
                String str;
                while((str=b.readLine())!=null){
                    String realLine = Tool.unComment(str);
                    if(realLine.length()>0&&realLine.contains("=")){
                        String[] seg = realLine.split("=");
                        seg[0] = seg[0].trim();
                        seg[1] = seg[1].trim();

                        switch (seg[0]){
                            case "config":
                                configPath = seg[1];
                                break;
                            case "dtaparam":
                                dtaparamPath = seg[1];
                                break;
                            case "network":
                                networkPath = seg[1];
                                break;
                            case "behavior":
                                behaviorPath = seg[1];
                                break;
                            case "supplyparam":
                                supplyparamPath = seg[1];
                                break;
                            case "sensor":
                                sensorPath = seg[1];
                                break;
                            case "histODCsvPath":
                                histODCsvPath = seg[1];
                                break;
                            case "mitsimODCsvPath":
                                mitsimODCsvPath = seg[1];
                                break;
                            case "histODPath":
                                histODPath = seg[1];
                                break;
                            case "mitsimODPath":
                                mitsimODPath = seg[1];
                                break;
                            case "odFlowPath":
                                odFlowPath = seg[1];
                                break;
                            case "sensorDataPath":
                                sensorDataPath =seg[1];
                                break;
                            case "sen_path":
                                sen_path =seg[1];
                                break;
                            default:
                                break;
                        }
                    }
                }
            }catch (FileNotFoundException fnfe){
                fnfe.printStackTrace();
                return;
            }catch (Exception e){
                e.printStackTrace();
                return;
            }

        }
    }

    public int dtaparam_loader(){
        boolean isSearched = false;
        try {
            FileInputStream configF = new FileInputStream(configPath);
            BufferedReader configB = new BufferedReader(new InputStreamReader(configF));
            FileInputStream dtaparamF = new FileInputStream(dtaparamPath);
            BufferedReader dtaparamB = new BufferedReader(new InputStreamReader(dtaparamF));
            List<String> ff = new LinkedList<String>();
            List<String> queryList= new LinkedList<String>();
            List<String> paraList= new LinkedList<String>();
            List<String> dataList= new LinkedList<String>();
            String queryStr = "";
            String paraStr = "";
            String dataStr = "";
            String str1;
            while((str1 = dtaparamB.readLine())!=null){
                ff.add(Tool.unComment(str1).trim());
            }

            while((str1 = configB.readLine())!=null){
                String realLine = Tool.unComment(str1).trim();
                String[] seg = realLine.split("\"");
                int segCount =seg.length;
                if(segCount < 2){
                    continue;
                }
                else if(segCount == 2){
                    isSearched = (seg[1].matches("dtaparam"));
                }
                else if(isSearched){
                    String colName = seg[1];
                    paraList.add(colName);
                    Pattern pattern = Pattern.compile(String.format("(?<!/)[ ]*%s[ ]*=[ ]*[^/]*",(colName)));

                    for (String line: ff) {
                        Matcher matcher = pattern.matcher(line);
                        if(matcher.find())
                        {
                            String metadata = matcher.group();
                            metadata = metadata.split("=")[1].trim();
                            switch (colName){
                                case "StartSimulation":
                                    dtaparamData.startSimulation.getDate(metadata);
                                    break;
                                case "StopSimulation":
                                    dtaparamData.stopSimulation.getDate(metadata);
                                    break;
                                case "OdInterval":
                                    dtaparamData.odInterval = Integer.parseInt(metadata);
                                    break;
                                case "HorizonLength":
                                    dtaparamData.horizonLenghth = Integer.parseInt(metadata);
                                    break;
                                case "UpdateInterval":
                                    dtaparamData.updateInterval = Integer.parseInt(metadata);
                                    break;
                                case "AdvanceInterval":
                                    dtaparamData.advanceInterval = Integer.parseInt(metadata);
                                    break;
                                case "MaxEstIter":
                                    dtaparamData.maxEstIter = Integer.parseInt(metadata);
                                    break;
                                case "MaxPredIter":
                                    dtaparamData.maxPredIter = Integer.parseInt(metadata);
                                    break;
                                default:
                            }

                            if(metadata.contains(":")){
                                metadata = "'"+metadata +"'";
                            }

                            dataList.add(metadata);
                            continue;
                        }
                    }
                }
                else{
                    continue;
                }
            }

            for (String line:ff){
                if(line.contains("MaxEstIter")){
                    MaxEstIter = Integer.parseInt(line.split("=")[1].trim());
                    break;
                }
            }

            simuStartTimeStr = dataList.get(0).substring(1,dataList.get(0).length()-1);
            simuStopTimeStr = dataList.get(1).substring(1,dataList.get(1).length()-1);
            String[] timeStr1 = simuStartTimeStr.split(":");
            String[] timeStr2 = simuStopTimeStr.split(":");
            Integer[] time1 = new Integer[3];
            Integer[] time2 = new Integer[3];
            for(int i=0;i<3;i++){
                time1[i] = Integer.parseInt(timeStr1[i]);
                time2[i] = Integer.parseInt(timeStr2[i]);
            }
            MYtime t1 = new MYtime();
            MYtime t2 = new MYtime();
            t1.getDate(time1[0],time1[1],time1[2]);
            t2.getDate(time2[0],time2[1],time2[2]);
            int delta = MYtime.getDeltaSec(t2,t1);
            intervalValue = Integer.parseInt(dataList.get(2));
            intervalNum = delta/60/intervalValue;
            Tool.println(String.format("Interval number = %d",intervalNum));
            paraList.remove(0);
            for(int i=0;i<paraList.size();i++){
                paraStr = paraStr + paraList.get(i)+",";
                dataStr = dataStr + dataList.get(i)+",";
                queryStr = queryStr + String.format("%s=%s AND ",paraList.get(i),dataList.get(i));

            }
            queryStr = queryStr.substring(0, queryStr.length()-4);
            paraStr = paraStr.substring(0, paraStr.length()-1);
            dataStr = dataStr.substring(0, dataStr.length()-1);
            dtaparamData.dtaparamId = insertIfNotExistAndReturnId("dtaparam", queryStr, paraStr, dataStr);
            return dtaparamData.dtaparamId;

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public int network_loader(){
        String[] wordSeg = networkPath.split("/");
        String lastWord = wordSeg[wordSeg.length-1].trim();
        String pureFileName = (lastWord.substring(0,lastWord.length()-4));
        String queryStr;
        String paraStr;
        String dataStr;
        int Nnode=0;
        int Nlink=0;
        int Nseg=0;
        int Nlane=0;
        try{
            FileInputStream networkF = new FileInputStream(networkPath);
            BufferedReader networkB = new BufferedReader(new InputStreamReader(networkF));
            String line;
            while((line = networkB.readLine())!=null){
                String realLine = Tool.unComment(line).trim();
                if(realLine.contains("[Nodes]")){
                    Nnode = Integer.parseInt(realLine.split(":")[1].trim());
                }
                else if(realLine.contains("[Links]"))
                {
                    String[] data = realLine.split(":");
                    Nlink=Integer.parseInt(data[1].trim());
                    Nseg=Integer.parseInt(data[2].trim());
                    Nlane=Integer.parseInt(data[3].trim());
                }
            }

            networkData.name = pureFileName;
            networkData.nodeNum = Nnode;
            networkData.linkNum = Nlink;
            networkData.segmentNum = Nseg;
            networkData.laneNum = Nlane;

            queryStr=String.format("Name='%s' AND " +
                            "NodeNum=%d AND LinkNum=%d AND SegmentNum=%d AND LaneNum=%d"
                    ,pureFileName, Nnode, Nlink, Nseg, Nlane);
            paraStr="Name, NodeNum, LinkNum, SegmentNum, LaneNum";
            dataStr=String.format("'%s', %d, %d, %d, %d",pureFileName, Nnode, Nlink, Nseg, Nlane);
            return networkData.networkId = insertIfNotExistAndReturnId("network", queryStr, paraStr, dataStr);

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public int behavior_loader(){
        String[] phaseArray = {"[Habitual]","[PreTrip]","[EnRoutePresc]","[EnRouteDesc]"};
        String[] trimedPhaseArray = new String[4];
        String[] itemArray= {"bTTlowVOT","bTTmedVOT","bTThiVOT","bVOTMean","bVOTSD"};
        Map<String, Integer> phamap = new HashMap<String, Integer>();
        Map<String, Integer> colmap = new HashMap<String, Integer>();
        String[] serialV = {"","","",""};
        String queryStr;
        String paraStr;
        String dataStr;
        int order=0;
        for (String item: phaseArray) {
            trimedPhaseArray[order] = removeFirstAndEnd(item);
            phamap.put(item,order);

            order++;
        }
        for (int i=0;i<itemArray.length;i++){
            colmap.put(itemArray[i],i);
        }


        double[][] vectorArray=new double[4][5];

        Pattern bracketTest=Pattern.compile("\\[(.*)\\]");
        Pattern phaseTest=Pattern.compile("\\[Habitual\\]|\\[PreTrip\\]|\\[EnRoutePresc\\]|\\[EnRouteDesc\\]");
        Pattern columnTest=Pattern.compile("bTTlowVOT|bTTmedVOT|bTThiVOT|bVOTMean|bVOTSD");
        try{
            FileInputStream behaviorF = new FileInputStream(behaviorPath);
            BufferedReader behaviorB = new BufferedReader(new InputStreamReader(behaviorF));
            String line;
            String result="";
            while((line=behaviorB.readLine())!=null){
                String realLine = Tool.unComment(line).trim();

                Matcher phaseResult=phaseTest.matcher(realLine);
                if(phaseResult.find()){
                    result = phaseResult.group();
                    continue;
                }
                Matcher columnResult=columnTest.matcher(realLine);
                if(columnResult.find()){
                    String colName = columnResult.group();
                    String data = realLine.split("=")[1];
                    (vectorArray[phamap.get(result)][colmap.get(colName)])=Float.parseFloat(data);
                }
            }

            for (int i=0;i<4;i++){
                serialV[i] =  Arrays.toString(vectorArray[i]);
            }

            behaviorData.habitualArray = vectorArray[0];
            behaviorData.preTripArray = vectorArray[1];
            behaviorData.enRouteDescArray = vectorArray[2];
            behaviorData.enRoutePrescArray = vectorArray[3];

            queryStr=String.format("%s='%s' AND %s='%s' AND %s='%s' AND %s='%s'",
                    trimedPhaseArray[0],serialV[0],
                    trimedPhaseArray[1],serialV[1],
                    trimedPhaseArray[2],serialV[2],
                    trimedPhaseArray[3],serialV[3]);
            paraStr=String.format("%s, %s, %s, %s",trimedPhaseArray[0],
                    trimedPhaseArray[1],trimedPhaseArray[2],trimedPhaseArray[3]);
            dataStr=String.format("'%s', '%s', '%s', '%s'",
                    serialV[0],serialV[1],serialV[2],serialV[3]);

            return behaviorData.behaviorId = insertIfNotExistAndReturnId("behavior", queryStr, paraStr, dataStr );

        }catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private String removeFirstAndEnd(String str) {
        if(str!=null && str.length()>=2){
            str = str.substring(1,str.length()-1);
        }
        return str;
    }


    public Double[] stringToDoubleArray(String arraySrtr){
        String[] strList = arraySrtr.split(",");
        Double[] array = new Double[strList.length];

        for(int i=0;i<strList.length;i++){
            array[i] = Double.parseDouble(strList[i]);
        }
        return array;
    }

    public Integer[] stringToIntArray(String arrayStr){
        String[] strList = arrayStr.split(",");
        Integer[] array = new Integer[strList.length];

        for(int i=0;i<strList.length;i++){
            array[i] = Integer.parseInt(strList[i]);
        }
        return array;
    }


    public int supplyparam_loader() {
        String[] paraList={"SegmentId","freeFlowSpeed","jamDensity","alpha","beta","SegmentCapacity","Vmin","Kmin"};

        String queryStr="";
        String paraStr;
        String dataStr="";

        LinkedList<Integer> idList = new LinkedList<>();
        LinkedList<Double>[] vecList = new LinkedList[7];
        for(int i=0;i<7;i++){
            vecList[i] = new LinkedList<Double>();
        }
        String[] vecListStr = new String[8];

        try{
            FileInputStream supplyF = new FileInputStream(supplyparamPath);
            BufferedReader supplyB = new BufferedReader(new InputStreamReader(supplyF));
            String line;
            while((line = supplyB.readLine())!=null){
                String realLine = Tool.unComment(line).trim();
                if(realLine.contains("{")){
                    realLine = removeFirstAndEnd(realLine);
                    String[] itemStrList =realLine.split("\t");

                    idList.add(Integer.parseInt(itemStrList[0]));
                    for (int i=1;i<itemStrList.length;i++){
                        vecList[i-1].add(Double.parseDouble(itemStrList[i]));
                    }
                }
            }

            vecListStr[0] = Arrays.toString(idList.toArray());

            for(int i=1;i<vecListStr.length;i++){
                vecListStr[i] = Arrays.toString(vecList[i-1].toArray());
            }
            for (int i=0;i<vecListStr.length;i++){
                queryStr = queryStr + String.format("%s='%s' AND ",paraList[i],vecListStr[i]);
                dataStr = dataStr +"'" + vecListStr[i] + "',";
            }
            supplyData.segmentIdList = idList;
            supplyData.freeFlowSpeedList = vecList[0];
            supplyData.jamDensityList = vecList[1];
            supplyData.alphaList = vecList[2];
            supplyData.betaList = vecList[3];
            supplyData.segmentCapacityList = vecList[4];
            supplyData.vminList = vecList[5];
            supplyData.kminList = vecList[6];

            dataStr = dataStr.substring(0,dataStr.length()-1);
            queryStr = queryStr.substring(0, queryStr.length()-4);
            paraStr="SegmentId,freeFlowSpeed,jamDensity,alpha,beta,SegmentCapacity,Vmin,Kmin";

            return supplyData.supplyparamId = insertIfNotExistAndReturnId("supplyparam", queryStr, paraStr, dataStr);

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }

    public int insertIfNotExistAndReturnId(String tableName, String queryStr, String paraStr, String dataStr){
        String idName = tableName+"Id";
        String queryCommand = String.format("SELECT %s FROM %s WHERE %s ",idName, tableName, queryStr);
        List<String> result=  DBD.sqlQuery(queryCommand, shallPrintSQL);
        if(result.size()==1){
            return Integer.parseInt(result.get(0));
        }
        else{
            DBD.sqlUpdate(String.format("INSERT INTO %s(%s) VALUES(%s)",tableName,paraStr,dataStr),shallPrintSQL);
            List<String> newResult=  DBD.sqlQuery(queryCommand, shallPrintSQL);
            return Integer.parseInt(newResult.get(0));
        }
    }


    public String demand_loader_get_csvString(String histOrMitisimCsvFlowPath){
        try{
            FileInputStream f = new FileInputStream(histOrMitisimCsvFlowPath);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));
            String csvString="";
            String line;
            int n=0;
            while((line = b.readLine())!=null){

                n++;
            }

            f = new FileInputStream(histOrMitisimCsvFlowPath);
            b = new BufferedReader(new InputStreamReader(f));
            String[] strArray = new String[n];
            for(int i=0;i<n;i++){
                strArray[i] = b.readLine();
            }
            csvString = Arrays.toString(strArray);
            //csvString = csvString.replace(",,",",");
            return csvString;
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*public String[] demand_loader(){
        try {
            FileInputStream f = new FileInputStream(demandPath);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));
            String line;
            int len = 0;
            HashMap<Integer, LinkedList<double[]>> hashMap  = new HashMap<>();
            HashMap<Integer, int[]> timeDict = new HashMap<>();
            String[] seg = new String[3];
            LinkedList<double[]> demandSeriesList = new LinkedList<>();
            int[] timeSeries = new int[2];
            Integer timeLabel;
            double[] demandSeries = new double[3];
            while ((line = b.readLine()) != null) {
                len = line.length();
                if(len>5){
                    char firstChar = line.charAt(0);
                    if(firstChar=='{'){
                        demandSeries = new double[3];
                        seg = line.substring(1, len-2).split("\\s+");
                        demandSeries[0] = Double.parseDouble(seg[0]);
                        demandSeries[1] = Double.parseDouble(seg[1]);
                        demandSeries[2] = Double.parseDouble(seg[2]);
                        demandSeriesList.add(demandSeries);
                    }
                    else if(firstChar <= '9' && firstChar >='0'){
                        seg = line.trim().split("\\s+");
                        timeSeries = new int[2];
                        timeLabel = Integer.parseInt(seg[0]);
                        timeSeries[0] = Integer.parseInt(seg[1]);
                        timeSeries[1] = Integer.parseInt(seg[2]);
                        timeDict.put(timeLabel, timeSeries);
                        demandSeriesList = new LinkedList<>();
                        hashMap.put(timeLabel, demandSeriesList);
                    }
                }
            }
            mainRecord.demandTagTimeMap= timeDict;
            mainRecord.demandFlowTimeMap = hashMap;
            Gson gson = new Gson();
            String strOfHashMap = gson.toJson(hashMap);
            String strOfLinkedList = gson.toJson(timeDict);
            mainRecord.gsonOfDemandFlow = strOfHashMap;
            String[] strList = {strOfLinkedList,strOfHashMap};
            return strList;

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return new String[]{"",""};
        }catch (Exception e){
            e.printStackTrace();
            return new String[]{"",""};
        }
    }*/

    public String sensor_loader(){
        try {
            FileInputStream f = new FileInputStream(sensorPath);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));
            String line;
            int len = 0;
            HashMap<Integer, int[]> hashMap  = new HashMap<>();
            int[] sensorSeries = new int[1];
            int sensorCount = 0;
            while((line = b.readLine())!=null){
                if(line.contains("}")){
                    break;
                }
                else {
                    sensorCount++;
                }
            }

            sensorCount = sensorCount-2;

            f = new FileInputStream(sensorPath);
            b = new BufferedReader(new InputStreamReader(f));

            int index = 0;
            while ((line = b.readLine()) != null) {
                len = line.length();
                if(len!=0) {
                    char firstChar = line.charAt(0);
                    if (firstChar >= '0' && firstChar <= '9') {
                        String[] seg = line.trim().split("\\s+");
                        if (seg.length == 3) {
                            sensorSeries[index] = Integer.parseInt(seg[2]);
                            index++;
                        }
                        else {
                            index = 0;
                            sensorSeries = new int[sensorCount];
                            hashMap.put(Integer.parseInt(seg[0]), sensorSeries);
                        }
                    }
                }
            }

            mainRecord.sensorFlowTimeMap = hashMap;
            Gson gson = new Gson();
            String strOfHashMap = gson.toJson(hashMap);
            mainRecord.gsonOfSensorFlow = strOfHashMap;
            return strOfHashMap;

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return"";
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public Double readDemandScaleFactor(String demandPath){
        try {
            FileInputStream f = new FileInputStream(demandPath);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));
            String line;
            Double scaleFactor = 1.0;

            while ((line = b.readLine()) != null) {
                String realLine = Tool.unComment(line);
                if(realLine.length()!=0){
                    String[] seg = realLine.trim().split(" |\t");
                    scaleFactor = Double.parseDouble(seg[2]);
                    break;
                }
            }

            return scaleFactor;

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void main_loader(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        String datetimestr = dateFormat.format(date);
        String[] strList = datetimestr.split("\\s+");
        String dateStr = strList[0];
        String timeStr = strList[1];
        MYtime myt = new MYtime();

        String currentIntervalTimeStr = simuStartTimeStr;

        String histODCsvStr = demand_loader_get_csvString(histODCsvPath);
        String mitsimODCsvStr = demand_loader_get_csvString(mitsimODCsvPath);
        mainRecord.histODScaleFactor = readDemandScaleFactor(histODPath);
        mainRecord.mitsimODScaleFactor = readDemandScaleFactor(histODPath);
        String sensorStr = sensor_loader();

        HashMap<Integer, int[]> odFlowArrayTimeMap = new HashMap<>();
        HashMap<Integer, int[]> sensorDataArrayTimeMap = new HashMap<>();

        HashMap<Integer,int[]> sen_flw_dict=sen_flw_dict_generator();
        HashMap<Integer,double[]> sen_spd_dict=sen_spd_dict_generator();

        HashMap<Integer,Double> precision = CalculatePerformance();

        for(int i =1;i<=intervalNum;i++){
            String flowTimeSpamp=timespan_generator(i,":",",");
            myt.getDate(flowTimeSpamp.split(",")[0]);
            int absoluteSec = myt.getAbsoluteSecond();
            String estOD_filePath= odFlowPath+"estimatedOD["+ flowTimeSpamp +"]"+i*MaxEstIter+".dat";
            String sensorData_filePath = sensorDataPath+"Sim"+i*MaxEstIter+".dat";
            if(i>1) {
                currentIntervalTimeStr = getNextTime(currentIntervalTimeStr,intervalValue*60,":");
            }

            odFlowArrayTimeMap.put(absoluteSec, flow_array_generator(estOD_filePath));
            sensorDataArrayTimeMap.put(absoluteSec, flow_array_generator(sensorData_filePath));
        }


        mainRecord.dtaparamId = dtaparamData.dtaparamId;
        mainRecord.networkId = networkData.networkId;
        mainRecord.behaviorId = behaviorData.behaviorId;
        mainRecord.supplyparamId = supplyData.supplyparamId;
        mainRecord.runningDate = dateStr;
        mainRecord.runningTime = timeStr;
        mainRecord.simulationDate = dateStr;
        mainRecord.simulationStartTime = simuStartTimeStr;
        mainRecord.simulationStopTime = simuStopTimeStr;
        mainRecord.intervalNum = intervalNum;

        mainRecord.histODFlowCsvString = histODCsvStr;
        mainRecord.mitsimODFlowCsvString = mitsimODCsvStr;
        mainRecord.odFlowArrayTimeMap = odFlowArrayTimeMap;
        mainRecord.sensorDataArrayTimeMap = sensorDataArrayTimeMap;
        mainRecord.sen_Flw_TimeMap = sen_flw_dict;
        mainRecord.sen_Spd_TimeMap = sen_spd_dict;
        mainRecord.precisionTimeMap = precision;


        Gson gson = new Gson();

        String odFlowListStr = gson.toJson(odFlowArrayTimeMap);
        String sensorDataListStr = gson.toJson(sensorDataArrayTimeMap);


        String sen_flw_str = gson.toJson(sen_flw_dict);
        String sen_spd_str = gson.toJson(sen_spd_dict);
        String precisionStr = gson.toJson(precision);
        Tool.println(String.format("%d, %d, %d, %d, %d, %d",histODCsvStr.length(),mitsimODCsvStr.length(),
                sensorStr.length(),odFlowListStr.length(),sensorStr.length(),sen_flw_str.length(),sen_spd_str.length()));
        String command=String.format("INSERT INTO main("+
                        "dtaparamId, networkId, behaviorId, supplyparamId,"+
                        "runningDate, runningTime, simulationDate," +
                        "simulationStartTime,simulationStopTime,intervalNum," +
                        "histOD_flow, histOD_scale, mitsimOD_flow, mitsimOD_scale,sensor_flow," +
                        "estimateOd,sensorData,"+
                        "sen_flw,sen_spd,result_precision) "+
                        "VALUES(%d,%d,%d,%d,'%s','%s','%s','%s','%s',%d ,'%s',%f,'%s',%f,'%s','%s','%s','%s','%s','%s')",
                id_list.get(0),id_list.get(1),id_list.get(2),id_list.get(3),
                dateStr,timeStr,dateStr,
                simuStartTimeStr,simuStopTimeStr,intervalNum,
                histODCsvStr,mainRecord.histODScaleFactor,mitsimODCsvStr, mainRecord.mitsimODScaleFactor, sensorStr
                ,odFlowListStr,sensorDataListStr,
                sen_flw_str,sen_spd_str,precisionStr);

        DBD.sqlUpdate(command,shallPrintSQL);
        List<String> result = DBD.sqlQuery(String.format("SELECT dataid FROM main WHERE " +
                "runningDate ='%s' and runningTime='%s' ",dateStr,timeStr),shallPrintSQL);
        mainRecord.dataId = Integer.parseInt(result.get(0).trim());

        Tool.println("Insert main record " + mainRecord.dataId);

    }

    public int[] flow_array_generator(String flow_path){
        try {
            ArrayList<Integer> flowList = new ArrayList<>();
            FileInputStream f = new FileInputStream(flow_path);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));
            String line;
            while ((line = b.readLine()) != null) {
                String realLine = line.trim();
                if (realLine.matches("-?\\d+(\\.\\d+)?")) {
                    flowList.add(Integer.parseInt(realLine));
                }
            }
            int[] array = new int[flowList.size()];
            for(int i=0;i<flowList.size();i++) {
                array[i] = flowList.get(i).intValue();
            }

            return array;

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public HashMap<Integer, int[]> sen_flw_dict_generator(){

        HashMap<Integer, int[]>  final_flw_output = new HashMap<>();
        Boolean isFirstLine = true;

        try{
            for(int i=0;i<intervalNum;i++){
                String timeSpanStr = timespan_generator(i+1,"","-");
                String completeSENpath=String.format(sen_path+"sen_%s_%s_%s.out","flw", "Est",timeSpanStr);
                FileInputStream f = new FileInputStream(completeSENpath);
                BufferedReader b = new BufferedReader(new InputStreamReader(f));
                String line;
                while((line = b.readLine())!=null){
                    String realLine = Tool.unComment(line).trim();
                    if(realLine.length()!=0){
                        if(isFirstLine) {
                            isFirstLine =false;
                            continue;
                        }

                        String[] itemStrList =realLine.split("\t");
                        int [] array = new int[itemStrList.length-1];
                        for (int j=1;j<itemStrList.length;j++){
                            array[j-1] = Integer.parseInt(itemStrList[j]);
                        }
                        final_flw_output.put(Integer.parseInt(itemStrList[0]),array);
                    }
                }
            }
            return final_flw_output;

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<Integer, double[]> sen_spd_dict_generator(){

        HashMap<Integer, double[]>  final_spd_output = new HashMap<>();
        Boolean isFirstLine = true;

        try{
            for(int i=0;i<intervalNum;i++){
                String timeSpanStr = timespan_generator(i+1,"","-");
                String completeSENpath=String.format(sen_path+"sen_%s_%s_%s.out","spd", "Est",timeSpanStr);
                FileInputStream f = new FileInputStream(completeSENpath);
                BufferedReader b = new BufferedReader(new InputStreamReader(f));
                String line;
                while((line = b.readLine())!=null){
                    String realLine = Tool.unComment(line).trim();
                    if(realLine.length()!=0){
                        if(isFirstLine) {
                            isFirstLine =false;
                            continue;
                        }

                        String[] itemStrList =realLine.split("\t");
                        double [] array = new double[itemStrList.length-1];
                        for (int j=1;j<itemStrList.length;j++){
                            array[j-1] = Double.parseDouble(itemStrList[j]);
                        }
                        final_spd_output.put(Integer.parseInt(itemStrList[0]),array);
                    }
                }
            }
            return final_spd_output;

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public String timespan_generator(int timeIndex,String splitStr, String divider){
        String beginTimeStr =getNextTime(simuStartTimeStr, 60*(timeIndex-1)*intervalValue,splitStr);
        String endTimeStr =getNextTime(simuStartTimeStr, 60*timeIndex*intervalValue,splitStr);
        return String.format("%s%s%s",beginTimeStr,divider,endTimeStr);
    }

    public String getNextTime(String timeStr, int timeInterval, String splitStr){

        MYtime t1 = new MYtime();
        MYtime t2 = new MYtime();
        String[] timeList = timeStr.split(":");

        t1.getDate(Integer.parseInt(timeList[0]),
                Integer.parseInt(timeList[1]),
                Integer.parseInt(timeList[2]));

        t1.addSec(timeInterval);
        return String.format("%s%s%s%s%s",
                new DecimalFormat("00").format(t1.getHOUR()),splitStr,
                new DecimalFormat("00").format(t1.getMINUTE()),splitStr,
                new DecimalFormat("00").format(t1.getSECOND()));
    }



    public List<String> searchFiles(String rootdir, String pattern)throws Exception{

        File dir = new File(rootdir);
        String[] children = dir.list();
        if (children == null) {
            Tool.println("rootdir doesn't exist");
            return null;
        }
        else {
            List<String> resultList = new LinkedList<>();
            for (int i = 0; i < children.length; i++) {
                String filename = children[i];
                if (filename.matches(pattern)) {
                    resultList.add(filename);
                }
            }
            return resultList;
        }
    }

    //TODO: Calculate the precision of the estimation process.
    public HashMap<Integer, Double> CalculatePerformance(){
        return null;
    }

    public void InitRecordInstance(){
        dtaparamData = new DtaparamData();
        networkData = new NetworkData();
        behaviorData = new BehaviorData();
        supplyData = new SupplyData();
        mainRecord = new MainRecord();
    }

    public Boolean IsRecordFromDatabaseValid(){
        if(IsDtaparamDataValid()
                &&IsNetworkDataValid()
                &&IsBehaviorDataValid()
                &&IsSupplyDataValid()
                &&IsMainRecordValid()){


            return true;
        }

        return false;
    }

    public Boolean IsDtaparamDataValid(){
        String result = queryResult("dtaparam","*");;
        int dtaparamColumnNum=9;

        if(result==null) {
            return false;
        }
        String[] seg = result.split(",");
        if(seg.length!= dtaparamColumnNum){
            return false;
        }

        if(Integer.parseInt(seg[0])!=dtaparamData.dtaparamId){
            return false;
        }
        if(Integer.parseInt(seg[3])!=dtaparamData.odInterval){
            return false;
        }
        if(Integer.parseInt(seg[4])!=dtaparamData.horizonLenghth){
            return false;
        }
        if(Integer.parseInt(seg[5])!=dtaparamData.updateInterval){
            return false;
        }
        if(Integer.parseInt(seg[6])!=dtaparamData.advanceInterval){
            return false;
        }
        if(Integer.parseInt(seg[7])!=dtaparamData.maxEstIter){
            return false;
        }
        if(Integer.parseInt(seg[8])!=dtaparamData.maxPredIter){
            return false;
        }
        MYtime testTime = new MYtime();
        testTime.getDate(seg[1]);
        if(testTime.getAbsoluteSecond()!=dtaparamData.startSimulation.getAbsoluteSecond()){
            return false;
        }
        testTime.getDate(seg[2]);
        if(testTime.getAbsoluteSecond()!=dtaparamData.stopSimulation.getAbsoluteSecond()){
            return false;
        }
        return true;
    }

    public Boolean IsNetworkDataValid(){
        String result = queryResult("network","*");
        int networkColumnNum=6;

        if(result==null) {
            return false;
        }
        String[] seg = result.split(",");
        if(seg.length!= networkColumnNum){
            return false;
        }

        if(Integer.parseInt(seg[0])!=networkData.networkId){
            return false;
        }
        if(seg[1].compareTo(networkData.name)!=0){
            return false;
        }
        if(Integer.parseInt(seg[2])!=networkData.nodeNum){
            return false;
        }
        if(Integer.parseInt(seg[3])!=networkData.linkNum){
            return false;
        }
        if(Integer.parseInt(seg[4])!=networkData.segmentNum){
            return false;
        }
        if(Integer.parseInt(seg[5])!=networkData.laneNum){
            return false;
        }

        return true;
    }

    public Boolean IsBehaviorDataValid(){
        if(!Tool.testDoubleArraySame(queryResult("behavior","habitual"),behaviorData.habitualArray)){
            return false;
        }
        if(!Tool.testDoubleArraySame(queryResult("behavior","preTrip"),behaviorData.preTripArray)){
            return false;
        }
        if(!Tool.testDoubleArraySame(queryResult("behavior","enRouteDesc"),behaviorData.enRouteDescArray)){
            return false;
        }
        if(!Tool.testDoubleArraySame(queryResult("behavior","enRoutePresc"),behaviorData.enRoutePrescArray)){
            return false;
        }
        return true;
    }

    public Boolean IsSupplyDataValid(){
        if(!Tool.testIntArraySame(queryResult("supplyparam","SegmentId"),supplyData.segmentIdList)){
            return false;
        }
        if(!Tool.testDoubleArraySame(queryResult("supplyparam","alpha"),supplyData.alphaList)){
            return false;
        }
        if(!Tool.testDoubleArraySame(queryResult("supplyparam","beta"),supplyData.betaList)){
            return false;
        }
        if(!Tool.testDoubleArraySame(queryResult("supplyparam","freeFlowSpeed"),supplyData.freeFlowSpeedList)){
            return false;
        }
        if(!Tool.testDoubleArraySame(queryResult("supplyparam","jamDensity"),supplyData.jamDensityList)){
            return false;
        }
        if(!Tool.testDoubleArraySame(queryResult("supplyparam","Kmin"),supplyData.kminList)){
            return false;
        }
        if(!Tool.testDoubleArraySame(queryResult("supplyparam","SegmentCapacity"),supplyData.segmentCapacityList)){
            return false;
        }
        if(!Tool.testDoubleArraySame(queryResult("supplyparam","Vmin"),supplyData.vminList)){
            return false;
        }
        return true;
    }

    public Boolean IsMainRecordValid(){
        if(!Tool.testIntArraySame(queryResult("main","dtaparamId,networkId,behaviorId,supplyParamId,intervalNum"),
                new int[]{mainRecord.dtaparamId,mainRecord.networkId,mainRecord.behaviorId
                        ,mainRecord.supplyparamId,mainRecord.intervalNum})){
            return false;
        }

        if(!Tool.testStringArraySame(queryResult("main","simulationDate,simulationStartTime,simulationStopTime"),
                new String[]{mainRecord.simulationDate,mainRecord.simulationStartTime,mainRecord.simulationStopTime})){
            return false;
        }

        if(!Tool.testStringSame(queryResult("main","histOD_flow"),mainRecord.histODFlowCsvString)){
            return false;
        }

        if(Double.parseDouble(queryResult("main","histOD_flow"))!=mainRecord.histODScaleFactor){
            return false;
        }

        if(!Tool.testStringSame(queryResult("main","mitsimOD_flow"),mainRecord.mitsimODFlowCsvString)){
            return false;
        }

        if(Double.parseDouble(queryResult("main","mitsimOD_flow"))!=mainRecord.mitsimODScaleFactor){
            return false;
        }

        if(!Tool.testIntArrayMap((HashMap) GetJsonObjectFromDatabase("estimateOD",
                new TypeToken<HashMap<Integer,int[]>>(){}.getType()),
                mainRecord.odFlowArrayTimeMap)){
            return false;
        }

        if(!Tool.testIntArrayMap((HashMap) GetJsonObjectFromDatabase("sensorData",
                new TypeToken<HashMap<Integer,int[]>>(){}.getType()),
                mainRecord.sensorDataArrayTimeMap)){
            return false;
        }

        if(!Tool.testIntArrayMap((HashMap) GetJsonObjectFromDatabase("sen_flw",
                new TypeToken<HashMap<Integer,int[]>>(){}.getType()),
                mainRecord.sen_Flw_TimeMap)){
            return false;
        }

        if(!Tool.testDoubleArrayMap((HashMap) GetJsonObjectFromDatabase("sen_spd",
                new TypeToken<HashMap<Integer,double[]>>(){}.getType()),
                mainRecord.sen_Spd_TimeMap)){
            return false;
        }

// FIXME! TIME CONSUMING- just compare gson str? that maybe way much faster~
//        if(!Tool.testDoubleLinkedListArrayMap((HashMap) GetJsonObjectFromDatabase("demand_flow",
//                new TypeToken<HashMap<Integer,LinkedList<double[]>>>(){}.getType()),
//                mainRecord.demandFlowTimeMap)){
//            return false;
//        }
//
//        if(!Tool.testIntLinkedListArrayMap((HashMap) GetJsonObjectFromDatabase("sensor_flow",
//                new TypeToken<HashMap<Integer,LinkedList<int[]>>>(){}.getType()),
//                mainRecord.sensorFlowTimeMap)){
//            return false;
//        }

        String gsonStr_Table = queryResult("main","demand_flow");

        if(!Tool.testStringSame(mainRecord.gsonOfDemandFlow,gsonStr_Table)){
            return false;
        }

        gsonStr_Table = queryResult("main","sensor_flow");

        if(!Tool.testStringSame(mainRecord.gsonOfSensorFlow,gsonStr_Table)){
            return false;
        }

        return true;
    }

    public String queryResult(String tableName, String selectCol){
        int id=0;
        String idname = tableName+"id";
        switch (tableName){
            case "dtaparam":
                id = mainRecord.dtaparamId;
                break;
            case "network":
                id = mainRecord.networkId;
                break;
            case "behavior":
                id = mainRecord.behaviorId;
                break;
            case "supplyparam":
                id = mainRecord.supplyparamId;
                break;
            case "main":
                id = mainRecord.dataId;
                idname = "dataid";
                break;

        }
        List<String> result = DBD.sqlQuery(String.format("SELECT %s FROM %s WHERE %s=%d",selectCol,tableName,idname,id),false);
        if(result==null){
            return null;
        }
        else{
            return result.get(0);
        }
    }


    public Object GetJsonObjectFromDatabase(String jsonCol,Type classOfT){
        String jsonStr = queryResult("main",jsonCol);
        Gson gson = new Gson();
        Object o = gson.fromJson(jsonStr, classOfT);
        return o;
    }
    //TODO: Update the file paths and other parameters from file
    public void UpdatePathFromFile(){

    }

    public static void main(String[] args){
        InsertProcess dbi = new InsertProcess();

        dbi.UpdatePathFromFile();
        DBD.UpdatePathFromFile();

        DBD.connect();

        Tool.println("Load data path and database configuraion");
        if(args!=null && args.length>0){
            LoadFilePath(args[0]);
        }

        dbi.InitRecordInstance();

        Tool.println("Handling inserting CONFIG TABLE process~");
        dbi.id_list.add(dbi.dtaparam_loader());
        dbi.id_list.add(dbi.network_loader());
        dbi.id_list.add(dbi.behavior_loader());
        dbi.id_list.add(dbi.supplyparam_loader());

        Tool.println(String.format("Get IdList %d %d %d %d ",
                dbi.id_list.get(0),dbi.id_list.get(1),dbi.id_list.get(2),dbi.id_list.get(3)));
        Tool.println("Handling inserting MAIN TABLE process~");
        dbi.main_loader();
        Tool.println("Finished inserting!");


        //TODO:Check database data validity~
        Tool.println("Check validity!");
        if(dbi.IsRecordFromDatabaseValid()){
            Tool.println("Validity Approved!");
        }
        else{
            Tool.println("Incorrect Validity!");
        }

        DBD.disconnect();
    }
}

