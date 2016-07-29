#!/usr/bin/python2

#BRIEF : Setting up a database for DynaMIT, preparing for the regression in the following procedures. Using the postgre drive psycopg.
#AUTHOR: MENG YUE
#DATE  : July 20 2016

import psycopg2

# Set up database
# cur: the cursor of the database
# dataPath: the path for the dtaparam.dat
def setup_database_handler(cur,configPath):
	tableName=""
	f=open(configPath,'r')
	for line in f:
		realLine=delimitComment(line)
		seg = realLine.split('"')
		segCount = len(seg)
		if(segCount<3):
			continue
		elif (segCount==3):
			tableName=seg[1]
			setup_table(cur,tableName)
		else:
			colName=seg[1]
			colType=seg[3]
			command = "ALTER TABLE %s ADD %s %s ;"%(tableName, colName, colType)
			cur.execute(command)		

def delimitComment(rawStr):
	try:
		index = rawStr.index("//")
		return rawStr[:index]
	except ValueError:
		return rawStr
	else:
		raise	
 
def show_database(cur, tableName):
	cur.execute("SELECT * FROM " + tableName + ";")
	a=cur.fetchone()
	print a

# Should alter the command to only setup table under false expression, because once there is a table, we shouldn't drop it.
def setup_table(cur, tableName):
	tableExist = table_exist(cur, tableName)
	if tableExist:
		cur.execute("DROP TABLE " + tableName +" CASCADE ;")
	cur.execute("CREATE TABLE " + tableName + "();")


def table_exist(cur, tableName):
	cur.execute("SELECT count(*) FROM pg_tables WHERE tablename = '"+tableName+"';")
	a=cur.fetchone()
	return (int(a[0])!=0) 

if __name__ == "__main__":
	
	# define data file path
	configPath = "database.config"

	# SQL Setup process
	conn = psycopg2.connect(dbname="dyna", user="dynamit",host="/tmp")
	cur = conn.cursor()

	# Load data and save to database process
	setup_database_handler(cur, configPath)

	# Show the result	
	show_database(cur, "dtaparam")


	conn.commit()
	cur.close()
	conn.close()

	print("Finish database setting")
                                                                                      
