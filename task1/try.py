f1=open('/home/dynamit/student/mengyue/drill/task2/data.txt','r')
f2=open('data2.txt','w')
number=int(f1.readline())
for i in range(0,number):
	rawData=int(f1.readline())
	newData=rawData*rawData
	f2.write(str(newData)+"\r\n")
f1.close()
f2.close()


