import matplotlib.pyplot as plt
import numpy as np
import csv
import math

hist_flw_csv_path="data/hist_flw.csv"
sen_flw_csv_path="data/sen_flw.csv"
sensor_csv_path="data/sensor.csv"


def calRMSN(est_array,real_array):
	#est_array=(int(i) for i in est_array)
	#real_array=(int(i) for i in real_array)
	n=len(est_array)
	upper=0
	down=0
	for i in range(0,n):
		upper=upper+(est_array[i]-real_array[i])**2
		down=down+real_array[i]
	if down==0:
		return 0
	RMSN=math.sqrt(upper*n)/down
	return RMSN

def drawDiagonalGraph(observed,estimated,filepath):
	maxmax=max(max(estimated),max(observed))
	minmin=min(min(estimated),min(observed))
	plt.xlabel("Observed Sensor Counts (veh/5min)")
	plt.ylabel("Estimation Sensor Counts (veh/5min)")
	plt.scatter(estimated,observed,color="red",marker='.',s=40)
	plt.plot(np.linspace(minmin,maxmax,1000), np.linspace(minmin,maxmax,1000), color="black", linewidth=1.0)
	plt.xlim([minmin,maxmax])
 	plt.ylim([minmin,maxmax])
	plt.savefig(filepath,dpi=72)

def drawDescendingGraph(rmsn_sensor_list,filepath):
	plt.close()
	plt.figure()
	rmsn_sensor_list.sort(reverse=True)
	plt.xlabel("RMSN Value")
	plt.ylabel("Sensor count in descending order")
	plt.plot(range(1,len(rmsn_sensor_list)+1),rmsn_sensor_list,color="green",linewidth=2)

	plt.savefig(filepath,dpi=72)

def drawWholeRMSNGraph(timetagList,rmsn_a_list,rmsn_b_list,filepath):
	plt.rc('legend', fontsize=10, handlelength=1)
	plt.close()
	plt.figure()
	x=range(1,len(timetagList)+1)
	plt.xticks(x, timetagList)
	plt.xlabel("Time of day")
	plt.ylabel("Flow count rmsn")
	plt.plot(x,rmsn_a_list,color="green",linewidth=2,label="Historical flow count RMSN, Overall RMSN=%f"%(sum(rmsn_a_list)/len(rmsn_a_list)))
	plt.plot(x,rmsn_b_list,color="blue",linewidth=2,label="EKF estimated flow count RMSN, Overall RMSN=%f"%(sum(rmsn_b_list)/len(rmsn_b_list)))
	plt.legend()
	plt.savefig(filepath,dpi=72)

def numlist(listlist):
	plt.close()
	plt.figure()
	a=len(listlist)
	b=len(listlist[1])
	newIntListList=[]
	for i in range(0,a):
		newIntList=[]
		for j in range(0,b):
			newIntList.append((round)(float(listlist[i][j])))
		newIntListList.append(newIntList)
	return newIntListList

def calTimeList(intList):
	num = len(intList)
	timeList=[]
	for i in range(0,num):
		timeList.append("%02d:%02d"%(intList[i]/3600,intList[i]%3600/60))
	return timeList

if __name__ == '__main__':
	hist_csv = csv.reader(open(hist_flw_csv_path,'rb'))
	hist_data= np.array(numlist(list(hist_csv)))
	sen_flw_csv=csv.reader(open(sen_flw_csv_path,'rb'))
	sen_data = np.array(numlist(list(sen_flw_csv)))
	sensor_csv=csv.reader(open(sensor_csv_path,'rb'))
	sensor_data= np.array(numlist(list(sensor_csv)))
	timeSum=len(hist_data)-1	
	countSum=len(hist_data[1])-1
	drawDiagonalGraph(sensor_data[1:timeSum+1,1:countSum+1].flatten(),sen_data[1:timeSum+1,1:countSum+1].flatten(),"graph/diagon_est.png")
	drawDiagonalGraph(sensor_data[1:timeSum+1,1:countSum+1].flatten(),hist_data[1:timeSum+1,1:countSum+1].flatten(),"graph/diagon_hist.png")
	timeList = calTimeList(sen_data[1:timeSum+1,0])

	a_list=[]
	b_list=[]
	
	for j in range(0,countSum):
		tempA=0
		tempB=0
		tempA=calRMSN(sen_data[1:timeSum+1,j],sensor_data[1:timeSum+1,j])
		tempB=calRMSN(hist_data[1:timeSum+1,j],sensor_data[1:timeSum+1,j])
		a_list.append(tempA)
		b_list.append(tempB)

	drawDescendingGraph(a_list,"graph/descend_est.png")
	drawDescendingGraph(b_list,"graph/descend_hist.png")

	a_list=[]
	b_list=[]
	for i in range(0,timeSum):
		a_list.append(calRMSN(list(hist_data[i+1]),list(sensor_data[i+1])))
		b_list.append(calRMSN(list(sen_data[i+1]),list(sensor_data[i+1])))
	drawWholeRMSNGraph(timeList,a_list,b_list,"graph/wrmsng.png")
	


