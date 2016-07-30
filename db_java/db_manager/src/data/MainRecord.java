package data;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by dynamit on 7/29/16.
 */
public class MainRecord{
    //Input parameters
    public int dataId;
    public int dtaparamId;
    public int networkId;
    public int behaviorId;
    public int supplyparamId;
    public String runningDate;
    public String runningTime;
    public String simulationDate;
    public String simulationStartTime;
    public String simulationStopTime;
    public int intervalNum;

    //Metadata(Tags)
    public int dayOfWeek;
    public int weekOfMonth;
    public int monthOfYear;
    public Boolean isHoliday;
    public String season;
    public String weather;
    public double temperature;
    public double humidity;
    public double rainfall;
    public double wind;
    public String incident;
    public String specialEvent;
    public String description;

    //Historical network state and measurement
    public HashMap<Integer, int[]> sensorFlowTimeMap;
    public String gsonOfSensorFlow;
    public double histODScaleFactor;
    public String histODFlowCsvString;
    public double mitsimODScaleFactor;
    public String mitsimODFlowCsvString;
    public String gsonOfDemandFlow;


    //Output datas
    public HashMap<Integer, int[]> odFlowArrayTimeMap;
    public HashMap<Integer, int[]> sensorDataArrayTimeMap;
    public HashMap<Integer,int[]> sen_Flw_TimeMap;
    public HashMap<Integer,double[]> sen_Spd_TimeMap;
    public HashMap<Integer, Double> precisionTimeMap;

}
