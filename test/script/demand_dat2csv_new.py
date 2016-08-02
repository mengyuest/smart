#########################################
# convert demand.dat to csv file
#
#  input:   demand.dat
# author:   Haizheng
#   date:   Jul 30, 2014
#version:   1.0
#########################################

import os, sys, re

#input_dir = "./"
#input_dem_fn = "demand.dat"

output_dir = "./outputs/"
#output_csv_fn = "demand.csv"

if len(sys.argv) != 3:
    print "Usage: dat2csv input_file output_file"
    exit()

input_fn = str(sys.argv[1])
if '/' not in str(sys.argv[2]):
    output_fn = output_dir + str(sys.argv[2])
else:
    output_fn = str(sys.argv[2])

    
### save demand.dat into OD_entries in ["origin","dest","interval","demand"] format
timeframe = 0
myfile = open(input_fn, 'r')
OD_entries = [["origin","dest","interval","demand"]]
counter=0
for line in myfile.readlines():
    #print line
    #counter=counter+1
    while (line.startswith("#")):
        continue
        #skip comments
        
    if '{' not in line and '}' not in line:
        line_list = re.split(' |\t', line)
	#print line_list
        if len(line_list) != 3:
            continue
        timeframe = line_list[0]
        #record timeframe
        
    else:
        line_list = re.split(' |\t', line.strip().lstrip('{').rstrip('}\r\n').strip())
        #print line_list
        if len(line_list) == 3:
            OD_entries.append( [line_list[0],line_list[1],timeframe,line_list[2]] )
        #record OD entries
        
myfile.close()

f = open(output_fn, 'w')

for entry in OD_entries:
    f.write("%s,%s,%s,%s,\n" % (entry[0],entry[1],entry[2],entry[3]) )
f.close()

print("Conversion from '%s' to '%s' success!\n" % (input_fn,output_fn) )
    
