/**
 * Created by Meng Yue on 7/24/16.
 */


import java.util.regex.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import com.google.gson.Gson;

import java.text.*;
public class db_insert {

    public db_driver DBD = new db_driver();
    public Boolean shallPrintSQL = true;

    public String dtaparamPath = "/home/dynamit/DynaMIT/dtaparam.dat";
    public String networkPath = "/home/dynamit/DynaMIT/july_demo_network_v11.dat";
    public String behaviorPath = "/home/dynamit/DynaMIT/BehavioralParameters.dat";
    public String supplyparamPath = "/home/dynamit/DynaMIT/supplyparam.dat";
    public String sensorPath="/home/dynamit/DynaMIT/sensor.dat";
    public String demandPath = "/home/dynamit/DynaMIT/demand_DynaMIT_july_demo_5min_Final7.dat";
    public String configPath="/home/dynamit/student/mengyue/drill/database.config";
    public String odFlowPath="/home/dynamit/DynaMIT/temp/";
    public String sensorDataPath="/home/dynamit/DynaMIT/";
    public String sen_path="/home/dynamit/DynaMIT/output/";

    public List<Integer> id_list = new LinkedList<Integer>();
    public int intervalNum;
    public int intervalValue;
    public int MaxEstIter = 1;
    public String simuStartTimeStr;
    public String simuStopTimeStr;


    public static void println(String a){
        System.out.println(a);
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
                ff.add(unComment(str1).trim());
            }
                
            while((str1 = configB.readLine())!=null){
                String realLine = unComment(str1).trim();
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
            println(String.format("Interval number = %d",intervalNum));
            paraList.remove(0);
            for(int i=0;i<paraList.size();i++){
                paraStr = paraStr + paraList.get(i)+",";
                dataStr = dataStr + dataList.get(i)+",";
                queryStr = queryStr + String.format("%s=%s AND ",paraList.get(i),dataList.get(i));

            }
            queryStr = queryStr.substring(0, queryStr.length()-4);
            paraStr = paraStr.substring(0, paraStr.length()-1);
            dataStr = dataStr.substring(0, dataStr.length()-1);
            return insertIfNotExistAndReturnId("dtaparam", queryStr, paraStr, dataStr);

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
                String realLine = unComment(line).trim();
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
            queryStr=String.format("Name='%s' AND " +
                    "NodeNum=%d AND LinkNum=%d AND SegmentNum=%d AND LaneNum=%d"
                    ,pureFileName, Nnode, Nlink, Nseg, Nlane);
            paraStr="Name, NodeNum, LinkNum, SegmentNum, LaneNum";
            dataStr=String.format("'%s', %d, %d, %d, %d",pureFileName, Nnode, Nlink, Nseg, Nlane);
            return insertIfNotExistAndReturnId("network", queryStr, paraStr, dataStr);

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
                String realLine = unComment(line).trim();

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

            queryStr=String.format("%s='%s' AND %s='%s' AND %s='%s' AND %s='%s'",
                    trimedPhaseArray[0],serialV[0],
                    trimedPhaseArray[1],serialV[1],
                    trimedPhaseArray[2],serialV[2],
                    trimedPhaseArray[3],serialV[3]);
            paraStr=String.format("%s, %s, %s, %s",trimedPhaseArray[0],
                    trimedPhaseArray[1],trimedPhaseArray[2],trimedPhaseArray[3]);
            dataStr=String.format("'%s', '%s', '%s', '%s'",
                    serialV[0],serialV[1],serialV[2],serialV[3]);

            return insertIfNotExistAndReturnId("behavior", queryStr, paraStr, dataStr );

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
                String realLine = unComment(line).trim();
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
            dataStr = dataStr.substring(0,dataStr.length()-1);
            queryStr = queryStr.substring(0, queryStr.length()-4);
            paraStr="SegmentId,freeFlowSpeed,jamDensity,alpha,beta,SegmentCapacity,Vmin,Kmin";

            return insertIfNotExistAndReturnId("supplyparam", queryStr, paraStr, dataStr);

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


    public String[] demand_loader(){

        try {
            FileInputStream f = new FileInputStream(demandPath);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));
            String line;
            int len = 0;
            int currentTime=0;
            HashMap<Integer, LinkedList<Double[]>> hashMap  = new HashMap<>();
            LinkedList<Integer[]> timeSeriesList = new LinkedList<>();
            String[] seg = new String[3];
            LinkedList<Double[]> demandSeriesList = new LinkedList<>();
            Integer[] timeSeries = new Integer[3];
            Double[] demandSeries = new Double[3];
            while ((line = b.readLine()) != null) {
                len = line.length();
                if(len>5){
                    char firstChar = line.charAt(0);
                    if(firstChar=='{'){
                        demandSeries = new Double[3];
                        seg = line.substring(1, len-2).split("\\s+");
                        demandSeries[0] = Double.parseDouble(seg[0]);
                        demandSeries[1] = Double.parseDouble(seg[1]);
                        demandSeries[2] = Double.parseDouble(seg[2]);
                        demandSeriesList.add(demandSeries);
                    }
                    else if(firstChar <= '9' && firstChar >='0'){
                        seg = line.trim().split("\\s+");
                        timeSeries = new Integer[3];
                        timeSeries[0] = Integer.parseInt(seg[0]);
                        timeSeries[1] = Integer.parseInt(seg[1]);
                        timeSeries[2] = Integer.parseInt(seg[2]);
                        currentTime = timeSeries[0];
                        timeSeriesList.add(timeSeries);
                        demandSeriesList = new LinkedList<>();
                        hashMap.put(currentTime, demandSeriesList);
                    }
                }
            }
            Gson gson = new Gson();
            String strOfHashMap = gson.toJson(hashMap);
            String strOfLinkedList = gson.toJson(timeSeriesList);
            String[] strList = {strOfLinkedList,strOfHashMap};
            println(String.format("%d",strList[1].length()));
            return new String[]{"",""};
            //return strList;

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return new String[]{"",""};
        }catch (Exception e){
            e.printStackTrace();
            return new String[]{"",""};
        }
    }

    public String[] sensor_loader(){
        return new String[]{"",""};
    }

    public void main_loader(){
        String[] demandStr = demand_loader();
        String[] sensorStr = sensor_loader();

        String currentIntervalTimeStr = simuStartTimeStr;
        for(int i =1;i<=intervalNum;i++){

            String simuInfo = "Inserted at "+DateFormat.getDateTimeInstance();
            String flowTimeSpamp=timespan_generator(i,":",",");
            String estOD_filePath= odFlowPath+"estimatedOD["+ flowTimeSpamp +"]"+i*MaxEstIter+".dat";
            String sensorData_filePath = sensorDataPath+"Sim"+i*MaxEstIter+".dat";
            if(i>1) {
                currentIntervalTimeStr = getNextTime(currentIntervalTimeStr,intervalValue*60,":");
            }

            try{
                String odFlowListStr = flow_str_generator(estOD_filePath);
                String sensorDataListStr = flow_str_generator(sensorData_filePath);

                String timeSpanStr = timespan_generator(i,"","-");
                String sen_flw_str=sen_str_generator(sen_path, "flw","Est",timeSpanStr);
                String sen_spd_str=sen_str_generator(sen_path, "spd","Est",timeSpanStr);

                String command=String.format("INSERT INTO main("+
                "dtaparamId, networkId, behaviorId, supplyparamId,"+
                        "startTime,demand_time,demand_flow,sensor_time,sensor_flow,simuInfo,estimateOD,sensorData,"+
                        "sen_flw,sen_spd) "+
                        "VALUES(%d,%d,%d,%d,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
                        id_list.get(0),id_list.get(1),id_list.get(2),id_list.get(3),
                        currentIntervalTimeStr,demandStr[0],demandStr[1],sensorStr[0],sensorStr[1],simuInfo,odFlowListStr,sensorDataListStr,
                        sen_flw_str,sen_spd_str);

                DBD.sqlUpdate(command,shallPrintSQL);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String flow_str_generator(String flow_path){
        try {
            LinkedList<Integer> flowList = new LinkedList<>();
            FileInputStream f = new FileInputStream(flow_path);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));
            String line;
            while ((line = b.readLine()) != null) {
                String realLine = line.trim();
                if (realLine.matches("-?\\d+(\\.\\d+)?")) {
                    flowList.add(Integer.parseInt(realLine));
                }
            }

            return Arrays.toString(flowList.toArray());

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return "";
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }

    public String sen_str_generator(String sen_path, String stateStr, String behaviorStr, String timeSpanStr){

        LinkedList<Double> i3d_array = new LinkedList();
        String completei3dpath=String.format(sen_path+"sen_%s_%s_%s.out",stateStr, behaviorStr,timeSpanStr);
        try{
            FileInputStream i3dF = new FileInputStream(completei3dpath);
            BufferedReader i3dB = new BufferedReader(new InputStreamReader(i3dF));
            String line;
            while((line = i3dB.readLine())!=null){
                String realLine = unComment(line).trim();
                if(realLine.length()!=0){
                    String[] itemStrList =realLine.split("\t");

                    for (int i=0;i<itemStrList.length;i++){
                        i3d_array.add(Double.parseDouble(itemStrList[i]));
                    }
                }
            }

            return Arrays.toString(i3d_array.toArray());

        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return "";
        }catch (Exception e){
            e.printStackTrace();
            return "";
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

    public List<String> searchFiles(String rootdir, String pattern)throws Exception{

            File dir = new File(rootdir);
            String[] children = dir.list();
            if (children == null) {
                println("rootdir doesn't exist");
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


    public static void main(String[] args){
        db_insert dbi = new db_insert();

        dbi.DBD.connect(db_driver.DB_LOCAL);
        dbi.id_list.add(dbi.dtaparam_loader());
        dbi.id_list.add(dbi.network_loader());
        dbi.id_list.add(dbi.behavior_loader());
        dbi.id_list.add(dbi.supplyparam_loader());

        println(String.format("Get IdList %d %d %d %d ",
                dbi.id_list.get(0),dbi.id_list.get(1),dbi.id_list.get(2),dbi.id_list.get(3)));
        dbi.main_loader();
        println("finished inserting~");
        dbi.DBD.disconnect();
    }
}
