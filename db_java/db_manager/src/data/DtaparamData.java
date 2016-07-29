package data;

/**
 * Created by dynamit on 7/29/16.
 */
public class DtaparamData{
    public int dtaparamId;
    public MYtime startSimulation = new MYtime();
    public MYtime stopSimulation = new MYtime();
    public int odInterval;
    public int horizonLenghth;
    public int updateInterval;
    public int advanceInterval;
    public int maxEstIter;
    public int maxPredIter;

    public DtaparamData(){
        MYtime startSimulation = new MYtime();
        MYtime stopSimulation = new MYtime();
    }
}
