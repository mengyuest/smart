# -*- coding: utf-8 -*-
"""
Created on Sat Feb 27 22:00:40 2016
Read DynaMIT-R output sensorand aggregate them into 5 min interval
 on them
@author: mit-its, Haizheng

Updated the path by Meng Yue
"""
import datetime
import os
import csv
import copy
from subprocess import call
#from random import SystemRandom

START_TIME = datetime.datetime(2016,1,8,18,00,0)   #date does not matter                                                # Simulation Interval start time 
END_TIME = datetime.datetime(2016,1,8,21,30,0) 
INTERVAL = 5
N_SENSORS = 650
#N_RUN = 5
randomNumPool = ['37806839','39838707','48175031','74217391','59441441']
dataFolder = '/home/dynamit/student/mengyue/drill/test/DynaMIT_openloop/sen_flw_data/' 
relativeDataFolder="sen_flw_data/"    
outputMeanFlowCsvFileName = 'hist_flw.csv'   
dynamitDir = '/home/dynamit/student/mengyue/drill/test/DynaMIT_openloop'

def RUN_ONCE_DYNAMIT(dynamitDir, fixSeedNumber):
    PREPARE_DYNAMIT_DTAPARAM(dynamitDir, fixSeedNumber)
    absDynamitDir = os.path.abspath(dynamitDir)
    origPath = os.getcwd();
    if origPath != absDynamitDir:
        os.chdir(absDynamitDir)
    #print os.listdir('.')
    call(['./backupToDir'])
    call(['./runDynaMIT.sh', 'dtaparam.dat'])
    call(['./new_mvAway.sh', relativeDataFolder+str(fixSeedNumber)])
    if os.getcwd() != origPath:
        os.chdir(origPath)

def PREPARE_DYNAMIT_DTAPARAM(dynamitDir, fixSeedNumber):
    absDynamitDir = os.path.abspath(dynamitDir)
    # origPath = os.getcwd();
    # if origPath != dynamitDir:
    #     os.chdir(dynamitDir)
    fn = absDynamitDir + '/dtaparam.dat'
    tempfn = fn + '.temp'
    with open(fn, 'rb') as infile:
        with open(tempfn, 'wb') as outfile:
            for line in infile.readlines():
                if line.startswith('FixSeed'):
                    row = line.split('=')
                    origDemandFN = row[1].strip()
                    line = row[0]+'= '+fixSeedNumber+'\n'
                outfile.write(line)

    #fn and tempfn both closed
    os.rename(tempfn, fn)
    # if os.getcwd() != origPath:
    #     os.chdir(origPath)
    return origDemandFN
    
def READ_SENSOR_DYNAMIT(DM_FOLDER):                                                                       # Dynamit Sensor flow (predicted) output file prefix
    STRING_EST = "/sen_flw_Est_"  
    Sensor_dynamit = {}
    delta = datetime.timedelta(minutes=INTERVAL)
    N_INT = int((END_TIME - START_TIME).seconds)/int(delta.seconds)
    CUR_TIME = START_TIME
    for x in range(1,N_INT+1):
        string_start = str(CUR_TIME.time())
        string_start = string_start.replace(":","")
        CUR_TIME = CUR_TIME + delta
        string_end = str(CUR_TIME.time())
        string_end = string_end.replace(":","")
        filename = DM_FOLDER + STRING_EST + string_start + "-" + string_end + ".out"
        #print(" Reading file ",filename)
        SENDM_rfile = open(filename, "r")                                                           # Open Sensor reading for given interval from DynaMIT output  
        flag_first = 0
        Interval =  CUR_TIME.hour*60*60 + CUR_TIME.minute*60
        Sensor_dynamit[Interval] = {}
        for x in range(1,N_SENSORS+1):
            Sensor_dynamit[Interval][x-1] = 0
        for line in SENDM_rfile:
            if flag_first == 0:
                flag_first = 1
            else:
                line = line.split("\t")
                for x in range(1,N_SENSORS+1):
                    Sensor_dynamit[Interval][x-1] += float(line[x])/60
        SENDM_rfile.close()
    print("Sensor DM",sorted(Sensor_dynamit.keys()))
    return Sensor_dynamit
    
def SAVE_TO_HIST_SENSOR_CSV(Sensor_dynamit,HistSsrCSVName):
    timestamps = sorted( Sensor_dynamit.keys() )
    csvRows = []
    for endSec in timestamps:
        row = [endSec]
        for ssrId in sorted(Sensor_dynamit[endSec]):
            row.append(Sensor_dynamit[endSec][ssrId])
        csvRows.append(row)
    with open(HistSsrCSVName, 'wb') as csvf:
        csvTitleRow = Sensor_dynamit[timestamps[0]].keys()
        csvTitleRow.insert(0,0) #for alignment
        writer = csv.writer(csvf, delimiter=',')
        writer.writerow(csvTitleRow)
        writer.writerows(csvRows)
        
def ADD_SENSOR_DICT(sensor_DM1, sensor_DM2): 
#add 2 sensor dicts and 
#return error when the two sizes are not the same
    timestamps = sorted(sensor_DM1.keys())
    assert( timestamps == sorted(sensor_DM2.keys()) )
    ssrList = sorted(sensor_DM1[timestamps[0]])
    res_ssr_DM = {}
    for endSec in timestamps:
        res_ssr_DM[endSec] = {}
        # assume the first interval has the same number of sensors as the rest
        for ssr in ssrList:
            assert( ssr in sensor_DM1[endSec] and ssr in sensor_DM2[endSec] )
            res_ssr_DM[endSec][ssr] = sensor_DM1[endSec][ssr] + sensor_DM2[endSec][ssr]
    return res_ssr_DM

def DIVIDE_SENSOR_DICT_BY(ssr_DM, factor):
    for endSec in ssr_DM.keys():
        # assume the first interval has the same number of sensors as the rest
        for ssr in ssr_DM[endSec]:
            ssr_DM[endSec][ssr] = float(ssr_DM[endSec][ssr])/factor
    return ssr_DM
        
def READ_FLW_IN_DM_FOLDERS(i3dFolders):
    #i3dFolders = ['37806839','39838707','48175031','74217391','59441441']
        # Path to 'output' folder in DynaMIT-R directory
    nValidFolders = 0
    sensor_DM = {}
    sum_ssr_DM = {}
    for i in xrange(5):
        sensor_DM[i] = READ_SENSOR_DYNAMIT(dataFolder+i3dFolders[i])
        if not sum_ssr_DM:
            sum_ssr_DM = copy.deepcopy(sensor_DM[i])
        else:
            sum_ssr_DM = ADD_SENSOR_DICT(sum_ssr_DM, sensor_DM[i])
        nValidFolders += 1
    mean_ssr_DM = DIVIDE_SENSOR_DICT_BY(sum_ssr_DM, nValidFolders)
    return sensor_DM, mean_ssr_DM

for rndNum in randomNumPool:
    RUN_ONCE_DYNAMIT(dynamitDir, rndNum)

ssrDM, meanssr = READ_FLW_IN_DM_FOLDERS(randomNumPool)
SAVE_TO_HIST_SENSOR_CSV(meanssr,dataFolder+outputMeanFlowCsvFileName)
