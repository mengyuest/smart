sensor_out_path="data/sensor.out"
mycsv_path="data/sensor.csv"

f=open(sensor_out_path,'rb')
lines=f.readlines()
table=[]
record=[]
for line in lines:
	if "{" in line:
		record=[]
		record.append(line.strip().split(" ")[0])
	elif "1" in line:
		record.append(line.strip().split(" ")[2])
	elif "}" in line:
		table.append(record)

f.close()

colNum=len(table[0])
rowNum=len(table)

f = open(mycsv_path, "w")
header="0"
for i in range(0,colNum):
	header= header + ","+str(i)
f.write(header+"\n")

for i in range(0,rowNum):
	rowStr=table[i][0]
	for j in range(1,colNum):
		rowStr=rowStr+","+table[i][j]
	f.write(rowStr+"\n")

f.close()
