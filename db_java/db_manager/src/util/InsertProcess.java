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

    private static String configPath        =   "";//"/home/dynamit/student/mengyue/drill/db_java/db_manager/config/database.config";
    private static String DYNAMITPATH       =   "";//"/home/dynamit/student/mengyue/drill/test/DynaMIT/";
    private static String MITSIMPATH        =   "";//"/home/dynamit/student/mengyue/drill/test/MITSIM/";
    private static String METADATAPATH      =   "";//"/home/dynamit/student/mengyue/drill/test/DynaMIT/metadata.dat"
    private static String dtaparamPath      =   "";//DYNAMITPATH + "dtaparam.dat";
    private static String networkPath       =   "";//DYNAMITPATH + "aug_network_v7_Hz.dat";//"july_demo_network_v11.dat";
    private static String behaviorPath      =   "";//DYNAMITPATH + "BehavioralParameters.dat";
    private static String supplyparamPath   =   "";//DYNAMITPATH + "supplyparam.dat";
    private static String sensorPath        =   "";//MITSIMPATH  + "Output/sensor.out";
    private static String histODCsvPath     =   "";//DYNAMITPATH + "demand_DynaMIT_hist_nZero_pert_Gaussian_BN5.csv";//"demand_DynaMIT.csv";
    private static String mitsimODCsvPath   =   "";//MITSIMPATH + "demand_MITSIM.csv";
    private static String histODPath        =   "";//DYNAMITPATH + "demand_DynaMIT_hist_nZero_pert_Gaussian_BN5.dat";//"demand_DynaMIT.dat";
    private static String mitsimODPath      =   "";//MITSIMPATH + "demand_MITSIM.dat";
    private static String odFlowPath        =   "";//DYNAMITPATH + "temp/";
    private static String sensorDataPath    =   "";//DYNAMITPATH;
    private static String sen_path          =   "";//DYNAMITPATH + "output/";

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


    public String[] demand_loader_get_csvString(String histOrMitisimCsvFlowPath){
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
            return strArray;
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String[] getOdPairArrayFromDemandCsvStr(String[] csvStrArray){
        ArrayList<String> odPairs = new ArrayList<>();
        int count = csvStrArray.length;
        int startIndex = 1;
        if(!csvStrArray[0].contains("origin")){
            startIndex = 0;
        }
        String firstTimeStamp = csvStrArray[startIndex].split(",")[2].trim();
        for(int i=startIndex;i<count;i++){
            if(!csvStrArray[i].split(",")[2].contains(firstTimeStamp)){
                break;
            }
            else{
                odPairs.add(csvStrArray[i].split(",")[0].trim()+" "+csvStrArray[i].split(",")[1].trim());
            }
        }

        return odPairs.toArray(new String[0]);

    }

    //Fixme: Only when demand is in quite standard form that each time interval has equal odpair nums.
    public String demandCsvStr2myCsvStr(String[] csvStrArray, String[] odPairs, Double scale){
        int startIndex = 1;
        if(!csvStrArray[0].contains("origin")){
            startIndex = 0;
        }

        int odPairCount=odPairs.length;
        int timeCount = (csvStrArray.length-startIndex)/odPairCount;

        StringBuilder sb = new StringBuilder();
        sb.append(scale);
        for(int i=0;i<odPairCount;i++){
            sb.append(","+odPairs[i]);
        }
        sb.append("\n");

        for(int i=0;i<timeCount;i++){
            int currentIndex = i*odPairCount+startIndex;
            sb.append(csvStrArray[currentIndex].split(",")[2].trim());
            for(int j=0;j<odPairCount;j++){
                sb.append(","+csvStrArray[currentIndex+j].split(",")[3].trim());
            }
            sb.append("\n");
        }

        return sb.substring(0);
    }

    public String estimatedOD2myCsvStr(int startTimeInSecond, int maxEstIter, String[] odPairs, Double scale){
        StringBuilder sb =new StringBuilder();

        sb.append(scale);
        for(String str:odPairs){
            sb.append(","+str);
        }
        sb.append("\n");
        try {
            int stopTimeInSecond = startTimeInSecond + intervalValue*60;
            String initEODFile = String.format("estimatedOD[%s,%s]%d.dat",
                    MYtime.generateDate(startTimeInSecond,":"),
                    MYtime.generateDate(stopTimeInSecond,":"),maxEstIter);

            FileInputStream f = new FileInputStream(odFlowPath + initEODFile);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));

            sb.append(startTimeInSecond);
            String line;
            while((line=b.readLine())!=null){
                if(line.trim().length()!=0) {
                    sb.append("," + line.trim());
                }
            }
            sb.append("\n");
            String otherEODPath = DYNAMITPATH + "EOD.txt";
            File thisFile = new File(otherEODPath);
            if(thisFile.exists()) {
                f = new FileInputStream(otherEODPath);
                b = new BufferedReader(new InputStreamReader(f));
                while ((line = b.readLine()) != null) {
                    if (line.trim().length() != 0) {
                        sb.append(line.trim());
                        sb.append("\n");
                    }
                }
            }
            return sb.substring(0);


        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }


    public HashMap<Integer,HashMap<String,Double>> demandCsvStr2mapmap(String[] csvStrArray){
        int count = csvStrArray.length;
        HashMap<Integer, HashMap<String,Double>> map = new HashMap<>();
        String[] strList = csvStrArray[0].trim().split(",");
        int startIndex = 1;
        if(!strList[0].contains("origin")){
            startIndex = 0;
        }

        for (int i=startIndex;i<count;i++){
            strList = csvStrArray[i].trim().split(",");
            String od_pair = strList[0]+" "+strList[1];
            Integer timeStamp = Integer.parseInt(strList[2]);
            if(map.containsKey(timeStamp)){
                map.get(timeStamp).put(od_pair,Double.parseDouble(strList[3]));
            }
            else{
                HashMap<String, Double> mapmap = new HashMap<>();
                map.put(timeStamp,mapmap);
                mapmap.put(od_pair,Double.parseDouble(strList[3]));
            }
        }
        return map;
    }

    public String mapmap2myCsvStr(HashMap<Integer,HashMap<String,Double>> map, double factor){
        Object[] timeKey = map.keySet().toArray();
        Object[] odKey = map.get(timeKey[0]).keySet().toArray();
        int timeCount = timeKey.length;
        int odCount = odKey.length;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%f",factor));
        for(int i=1;i<odCount+1;i++){
            sb.append(",");
            sb.append(odKey[i-1]);
        }
        sb.append("\n");

        for(int i=1;i<timeCount;i++){
            sb.append(String.format("%d",(Integer)timeKey[i]));
            for(int j=0;j<odCount;j++){
                sb.append(String.format(",%f",map.get(timeKey[i]).get(odKey[j])));
            }
            sb.append("\n");
        }

        return sb.substring(0);
    }

    public Double getFactorFromMyCsvStr(String myCsvStr){
        String factorPart =  myCsvStr.substring(0,50).split(",")[0];
        return Double.parseDouble(factorPart);
    }

    public HashMap<Integer,Map<String, Double>> myCsvStr2mapmap(String myCsvStr){
        HashMap<Integer,Map<String,Double>> map = new HashMap<>();
        String[] allStr = myCsvStr.split("\n");
        String odPairKeyStr = allStr[0];
        String[] odPairKey = odPairKeyStr.split(",");
        int lineSum = allStr.length;
        for (int i=1;i<lineSum;i++){
            HashMap<String, Double> mapmap = new HashMap<>();

            String[] odPairValueList = allStr[i].split(",");
            map.put(Integer.parseInt(odPairValueList[0]),mapmap);
            for(int j=1;j<odPairValueList.length+1;j++){
                mapmap.put(odPairKey[j],Double.parseDouble(odPairValueList[j]));
            }
        }
        return map;
    }

    public void mapmap2DemandDat(double factor, HashMap<Integer,Map<String, Double>> map, String outputPath){
        try{
            File thisFile = new File(outputPath);
            if(!thisFile.exists()) {
                thisFile.createNewFile();
            }
            FileOutputStream f = new FileOutputStream(thisFile);
            BufferedWriter b = new BufferedWriter(new OutputStreamWriter(f));
            String csvString="";
            String line;
            int n=0;

            Object[] timeKeys = map.keySet().toArray();
            Object[] odKeys = map.get(timeKeys[0]).keySet().toArray();
            int timeKeyCount = timeKeys.length;
            int odKeyCount = odKeys.length;
            for(int i=0;i<timeKeyCount;i++){
                b.write(String.format("%d\t0\t%d\n",timeKeys[i],(int)factor));
                b.write("{\n");
                for (int j=0;j<odKeyCount;j++){
                    String[] od_name = ((String)odKeys[j]).split(" ");
                    b.write(String.format("{%s\t%s\t%d}\n",od_name[0],od_name
                            [1],map.get(timeKeys[i]).get(odKeys[j]).intValue()));
                }
                b.write("}\n");
            }
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //TODO estimatedOD string from database turn to file estimatedOD
    public HashMap<Integer,HashMap<String, Double>> estimatedODDatStr2mapmap(){
        return null;
    }

    public void mapmap2EstimateDat(HashMap<Integer,Map<String, Double>> map, String outputDir){
        try{
            File thisDir = new File(outputDir);
            if(!thisDir.exists()) {
                thisDir.mkdir();
            }
            FileOutputStream f;
            BufferedWriter b;


            Object[] timeKeys = map.keySet().toArray();
            Object[] odKeys = map.get(timeKeys[0]).keySet().toArray();
            int timeKeyCount = timeKeys.length;
            int odKeyCount = odKeys.length;
            for(int i=0;i<timeKeyCount;i++){
                String timestamp = "["+MYtime.generateDate((Integer)timeKeys[i],":")+","+MYtime.generateDate((Integer)timeKeys[i]+300,":")+"]";
                f = new FileOutputStream(String.format("%sestimatedOD%s.dat",outputDir,timestamp));
                b = new BufferedWriter(new OutputStreamWriter(f));
                for (int j=0;j<odKeyCount;j++){
                    b.write(map.get(timeKeys[i]).get(odKeys[j]).intValue());
                    b.write("}\n");
                }

            }
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String sensor2myCsvStr(){
        try {
            FileInputStream f = new FileInputStream(sensorPath);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));
            StringBuilder sb = new StringBuilder();
            String line;
            int len = 0;
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

            for(int i=0;i<sensorCount;i++){
                sb.append(","+i);
            }
            sb.append("\n");

            f = new FileInputStream(sensorPath);
            b = new BufferedReader(new InputStreamReader(f));

            int index = 0;
            while ((line = b.readLine()) != null) {
                line = line.trim();
                len = line.length();
                if(len!=0) {
                    char firstChar = line.charAt(0);
                    if (firstChar >= '0' && firstChar <= '9') {
                        String[] seg = line.trim().split("\\s+");
                        if (seg.length == 3) {
                            sb.append(","+seg[2]);
                        }
                        else {
                            sb.append(seg[0]);
                        }
                    }
                    else{
                        if(line.contains("}")){
                            sb.append("\n");
                        }
                    }
                }
            }
            return sb.substring(0);

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return"";
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public String sen_data2myCsvStr(String senType){
        try {
            FileInputStream f;
            BufferedReader b;
            StringBuilder sb = new StringBuilder();

            for(int i=0;i<intervalNum;i++){
                String flowTimeSpamp=timespan_generator(i+1,"","-");
                f= new FileInputStream(String.format("%s%s_%s.out",sen_path,senType,flowTimeSpamp));
                b= new BufferedReader(new InputStreamReader(f));
                String line ="";
                line = b.readLine().trim();

                if(i==0){
                    line = line.replaceAll("\\s+",",");
                    sb.append(line+"\n");
                }

                while((line=b.readLine())!=null){
                    line = line.trim().replaceAll("\\s+",",");
                    sb.append(line+"\n");
                }
            }


            return sb.substring(0);

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

        String[] histODCsvStrArray = demand_loader_get_csvString(histODCsvPath);
        String[] mitsimODCsvStrArray = demand_loader_get_csvString(mitsimODCsvPath);
        String[] odPairArray = getOdPairArrayFromDemandCsvStr(histODCsvStrArray);
        mainRecord.histODScaleFactor = readDemandScaleFactor(histODPath);
        mainRecord.mitsimODScaleFactor = readDemandScaleFactor(histODPath);
        String histODMyCsvStr = demandCsvStr2myCsvStr(histODCsvStrArray,odPairArray,mainRecord.histODScaleFactor);
        mainRecord.histODFlowMyCsvString = histODMyCsvStr;
        String mitsimODMyCsvStr = demandCsvStr2myCsvStr(mitsimODCsvStrArray,odPairArray, mainRecord.mitsimODScaleFactor);
        mainRecord.mitsimODFlowMyCsvString =mitsimODMyCsvStr;
        String sensorStr = sensor2myCsvStr();
        mainRecord.sensorMyCsvString =sensorStr;

        HashMap<Integer, int[]> sensorDataArrayTimeMap = new HashMap<>();

        HashMap<Integer,int[]> sen_flw_dict=sen_flw_dict_generator();
        HashMap<Integer,double[]> sen_spd_dict=sen_spd_dict_generator();

        HashMap<Integer,Double> precision = CalculatePerformance();

        mainRecord.dtaparamId = dtaparamData.dtaparamId;
        mainRecord.networkId = networkData.networkId;
        mainRecord.behaviorId = behaviorData.behaviorId;
        mainRecord.supplyparamId = supplyData.supplyparamId;
        mainRecord.runningDate = dateStr;
        mainRecord.runningTime = timeStr;
        mainRecord.simulationStartTime = simuStartTimeStr;
        mainRecord.simulationStopTime = simuStopTimeStr;
        mainRecord.intervalNum = intervalNum;

        mainRecord.sensorDataArrayTimeMap = sensorDataArrayTimeMap;
        mainRecord.sen_Flw_TimeMap = sen_flw_dict;
        mainRecord.sen_Spd_TimeMap = sen_spd_dict;
        mainRecord.precisionTimeMap = precision;

        String odFlowListStr = estimatedOD2myCsvStr(MYtime.generateSeconds(simuStartTimeStr,":"),dtaparamData.maxEstIter,odPairArray,mainRecord.histODScaleFactor);
        mainRecord.estimatedODFlowMyCsvString = odFlowListStr;

        String sen_flw_str = sen_data2myCsvStr("sen_flw_Est");//gson.toJson(sen_flw_dict);
        mainRecord.sen_flwMyCsvString = sen_flw_str;
        String sen_spd_str = sen_data2myCsvStr("sen_spd_Est");//gson.toJson(sen_spd_dict);
        mainRecord.sen_spdMyCsvString = sen_spd_str;

        Gson gson = new Gson();
        //String sensorDataListStr = gson.toJson(sensorDataArrayTimeMap);
        String precisionStr = gson.toJson(precision);
        Tool.println(String.format("%d, %d, %d, %d, %d, %d",histODMyCsvStr.length(),mitsimODMyCsvStr.length(),
                sensorStr.length(),odFlowListStr.length(),sensorStr.length(),sen_flw_str.length(),sen_spd_str.length()));
        String command=String.format("INSERT INTO main("+
                        "dtaparamId, networkId, behaviorId, supplyparamId,"+
                        "runningDate, runningTime, simulationDate," +
                        "simulationStartTime,simulationStopTime,intervalNum," +
                        "histOD_flow, histOD_scale, mitsimOD_flow, mitsimOD_scale,sensor_flow," +
                        "estimatedOd,sen_flw,sen_spd,result_precision) "+
                        "VALUES(%d,%d,%d,%d,'%s','%s','%s','%s','%s',%d ,'%s',%f,'%s',%f,'%s','%s','%s','%s','%s')",
                id_list.get(0),id_list.get(1),id_list.get(2),id_list.get(3),
                dateStr,timeStr,mainRecord.simulationDate,
                simuStartTimeStr,simuStopTimeStr,intervalNum,
                histODMyCsvStr,mainRecord.histODScaleFactor,mitsimODMyCsvStr, mainRecord.mitsimODScaleFactor, sensorStr
                ,odFlowListStr, sen_flw_str,sen_spd_str,precisionStr);

        DBD.sqlUpdate(command,shallPrintSQL);
        List<String> result = DBD.sqlQuery(String.format("SELECT dataid FROM main WHERE " +
                "simulationDate='%s' ",mainRecord.simulationDate),shallPrintSQL);
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


        if(Double.parseDouble(queryResult("main","histOD_scale"))!=mainRecord.histODScaleFactor){
            return false;
        }


        if(Double.parseDouble(queryResult("main","mitsimOD_scale"))!=mainRecord.mitsimODScaleFactor){
            return false;
        }


        //3*DEMAND(HIST MITSIM ESTMIATE) SENSOR SEN_FLOW SEN_SPD
        if(!Tool.testStringSame(queryResult("main","histOD_flow"),mainRecord.histODFlowMyCsvString)){
            return false;
        }

        if(!Tool.testStringSame(queryResult("main","mitsimOD_flow"),mainRecord.mitsimODFlowMyCsvString)){
            return false;
        }

        if(!Tool.testStringSame(queryResult("main","estimatedOD"),mainRecord.estimatedODFlowMyCsvString)){
            return false;
        }

        if(!Tool.testStringSame(queryResult("main","sensor_flow"),mainRecord.sensorMyCsvString)){
            return false;
        }

        if(!Tool.testStringSame(queryResult("main","sen_flw"),mainRecord.sen_flwMyCsvString)){
            return false;
        }

        if(!Tool.testStringSame(queryResult("main","sen_spd"),mainRecord.sen_spdMyCsvString)){
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
    public void UpdatePathFromFile(String path){
        try {
            FileInputStream f = new FileInputStream(path);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));

            String line = "";
            while((line = b.readLine())!=null){
                String realLine = Tool.unComment(line).trim();
                String[] segList = realLine.split("=");
                switch (segList[0].trim()){
                    case "RUN_LOCAL":
                        DatabaseDriver.RUN_LOCAL = (segList[1].toLowerCase().contains("true"));
                        break;
                    case "JDBC_DRIVER":
                        DatabaseDriver.JDBC_DRIVER = Tool.unquote(segList[1]);
                        break;
                    case "DB_LOCAL":
                        DatabaseDriver.DB_LOCAL = Tool.unquote(segList[1]);
                        break;
                    case "USER_LOCAL":
                        DatabaseDriver.USER_LOCAL = Tool.unquote(segList[1]);
                        break;
                    case "PASS_LOCAL":
                        DatabaseDriver.PASS_LOCAL = Tool.unquote(segList[1]);
                        break;
                    case "DB_SERVER":
                        DatabaseDriver.DB_SERVER = Tool.unquote(segList[1]);
                        break;
                    case "USER_SERVER":
                        DatabaseDriver.USER_SERVER = Tool.unquote(segList[1]);
                        break;
                    case "PASS_SERVER":
                        DatabaseDriver.PASS_SERVER = Tool.unquote(segList[1]);
                        break;
                    case "config":
                        configPath = Tool.unquote(segList[1]);
                        break;
                    case "DynaMIT":
                        DYNAMITPATH = Tool.unquote(segList[1]);
                        break;
                    case "MITSIM":
                        MITSIMPATH = Tool.unquote(segList[1]);
                        break;
                    case "METADATA":
                        METADATAPATH = Tool.unquote(segList[1]);
                        break;
                }
            }

            dtaparamPath = DYNAMITPATH+"dtaparam.dat";
            f = new FileInputStream(dtaparamPath);
            b = new BufferedReader(new InputStreamReader(f));

            while((line=b.readLine())!=null){
                String realLine = Tool.unComment(line).trim();
                String[] segList = realLine.split("=");
                if(segList.length==2) {
                    String str = Tool.unquote(segList[1]);
                    switch (segList[0].trim()) {
                        case "InputDirectory":

                            if (str.length()==1&&str.charAt(0) == '.') {
                                sensorDataPath = DYNAMITPATH;
                            } else {
                                if(str.charAt(str.length()-1)!='/') {
                                    str = str+'/';
                                }
                                sensorDataPath = DYNAMITPATH+str.substring(2);
                            }
                            break;
                        case "OutputDirectory":
                            if (str.length()==1&&str.charAt(0) == '.') {
                                sen_path = DYNAMITPATH;
                            } else {
                                if(str.charAt(str.length()-1)!='/') {
                                    str = str+'/';
                                }
                                sen_path = DYNAMITPATH+str.substring(2);
                            }
                            break;
                        case "TmpDirectory":
                            if (str.length()==1&&str.charAt(0) == '.') {
                                odFlowPath = DYNAMITPATH;
                            } else {
                                if(str.charAt(str.length()-1)!='/') {
                                    str = str+'/';
                                }
                                odFlowPath = DYNAMITPATH+str.substring(2);
                            }
                            break;
                        case "NetworkFile":
                            networkPath = DYNAMITPATH + Tool.unquote(segList[1]);
                            break;
                        case "SupplyParamFile":
                            supplyparamPath = DYNAMITPATH + Tool.unquote(segList[1]);
                            break;
                        case "BehParamFile":
                            behaviorPath = DYNAMITPATH + Tool.unquote(segList[1]);
                            break;
                        case "MitsimSensorsFile":
                            sensorPath = DYNAMITPATH + Tool.unquote(segList[1]);
                            break;
                        case "HistODFile":
                            int count = str.length();
                            histODPath = DYNAMITPATH + str;
                            histODCsvPath = DYNAMITPATH + str.substring(0, count - 3) + "csv";
                    }
                }
            }

            f = new FileInputStream(MITSIMPATH + "master.mitsim");
            b = new BufferedReader(new InputStreamReader(f));
            while((line=b.readLine())!=null){
                String realLine = Tool.unComment(line).trim();
                String[] segList = realLine.split("=");
                if(segList.length==2) {
                    String str = Tool.unquote(segList[1]);
                    switch (segList[0].trim()) {
                        case "[Trip Table File]":
                            mitsimODPath = MITSIMPATH + str;
                            mitsimODCsvPath = MITSIMPATH + str.substring(0, str.length() - 3) + "csv";
                            break;
                    }
                }
            }


        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ReadFromMetaDataFile() {
        try {
            String line;
            FileInputStream f = new FileInputStream( METADATAPATH);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));
            while ((line = b.readLine()) != null) {
                String realLine = Tool.unComment(line).trim();
                String[] segList = realLine.split("=");
                if (segList.length == 2) {

                    switch (segList[0].trim()) {
                        case "simulationDate":
                            String str = Tool.unquote(segList[1]);
                            mainRecord.simulationDate = str;
                            break;
                    }
                }
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        String path=DatabaseDriver.PATH;
        if(args!=null && args.length>0){
            path = args[0];
        }
        InsertProcess dbi = new InsertProcess();

        dbi.InitRecordInstance();

        dbi.UpdatePathFromFile(path);

        dbi.ReadFromMetaDataFile();

        DBD.UpdatePathFromFile();

        DBD.connect();

        Tool.println("Load data path and database configuraion");



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

