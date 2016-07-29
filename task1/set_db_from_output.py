#BRIEF : Setting up a database for speed,density,flow and queue length from output files of DynaMIT using the postgre drive psycopg.
#AUTHOR: MENG YUE
#DATE  : July 14 2016

import psycopg2


# Transform raw txt data files to which the SQL can read
#filePath="/home/dynamit/student/mengyue/drill/task2/data.txt"


#filePath="data_dsy.txt"
f_dsy=open("data_dsy.txt",'r')
f_db=open("data_db.txt",'w')
tempstr=f_dsy.readline()
segmentNum=len(tempstr.split(('\t')))-1
print("segment number is: "+str(segmentNum))

for i in range(0,20):
	datalist=f_dsy.readline()
#	if(datalist.endswith('\n')):
#		datalist=datalist[:-1]
	datalist=(datalist.split("\t"))
	datalist.pop(0)
	f_db.write(datalist[0])
	for j in range(1,min(1500,segmentNum)):
		f_db.write("\t"+datalist[j])
#	f_db.write("\r")

f_dsy.close()
f_db.close()

# SQL Setup and Read-in process
conn = psycopg2.connect("dbname=mydb user=dynamit")

cur = conn.cursor()

cur.execute("SELECT count(*) FROM pg_tables WHERE tablename = 'test';")

a=cur.fetchone()

if int(a[0])!=0:
	cur.execute("DROP TABLE test;")
cur.execute("CREATE TABLE test();")

for i in range(0,min(segmentNum,1500)):
	cur.execute("ALTER TABLE test ADD seg"+str(i)+" real;")

cur.execute("COPY test FROM '/home/dynamit/student/mengyue/drill/task1/data_db.txt';")

conn.commit()

cur.close()

conn.close()

print("Finish database setting")
