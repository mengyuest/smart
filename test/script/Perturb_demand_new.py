import csv, os, re,sys
import numpy.random as rd

def CSV_TO_DEMAND_DAT(input_csv_fn, output_OD_fn, demand_factor):
    ### save OD demand in a dictionary from csv file
    OD_demand = {};
    myfile = open(input_csv_fn, 'r')#####
    line = myfile.readline()
    line_list = re.split(',| |\t', line.strip())

    if "origin" not in line_list:
        OD_demand[line_list[2]] = [ [line_list[0]+" "+line_list[1], line_list[3]] ]


    for line in myfile.readlines():
        line_list = re.split(',| |\t', line.strip())
        if (len(line_list) != 4 and not (len(line_list) == 5 and line_list[4] == '')):
            print line_list
            print("errpr: csv file corrupt")
            break
        
        if line_list[2] in OD_demand:
            OD_demand[line_list[2]].append([line_list[0]+" "+line_list[1], line_list[3]])
        else:
            OD_demand[line_list[2]] = [ [line_list[0]+" "+line_list[1], line_list[3]] ]
            
    myfile.close()

    ### print OD_demand into demand.dat file
    f = open(output_OD_fn, 'w')
    timestamps = sorted( map(int, OD_demand.keys()) )
    for key in timestamps:
        f.write("%d 0 %s\n" % (key,demand_factor))
        f.write("{\n")
        for entry in OD_demand[str(key)]:
            f.write("{%s %s}\n" % (entry[0],entry[1]))
        f.write("}\n\n")
        
    f.write("<END>")
    f.close()
    print("Conversion from '%s' to '%s' success!\n" % (input_csv_fn, output_OD_fn) )

def READ_DEMAND_SAVE_PERT_CSV(infn, outfn):
    sigmaPercent = 0.33
    minOrigDem = 0.4
    tot_demand = 0
    maxDemandIn5Min = 150
    print(infn + "  "+outfn)
    # read csv and save to perturbed demand file
    with open(infn,'rb') as infile:
        reader = csv.reader(infile)
        has_header = csv.Sniffer().has_header(infile.read(1024))
        infile.seek(0)
        reader = csv.reader(infile)
        if has_header:
            next(reader)
        with open(outfn,'wb') as outfile:
            writer = csv.writer(outfile)
            for rows in reader:
                origDem = float(rows[3])
                if origDem <1:
                    origDem = minOrigDem
                scale = float(rd.normal(1,sigmaPercent,1))
                newDem = origDem * scale
                if newDem < 0:
                    newDem = 0
                if newDem > maxDemandIn5Min:
                    newDem = maxDemandIn5Min
                newDem = round(newDem)
                rows[3] = str(newDem)
                writer.writerow(rows)
                tot_demand += newDem
    return tot_demand

if __name__ == "__main__":

	demandFactor = 10

	if len(sys.argv) != 4:
		print "Usage: python Perturb_demand_new.py input_csv_file output_dat_folder output_csv_folder"
		exit()

	infn = str(sys.argv[1])
	csvFolder=str(sys.argv[2])
	datFolder=str(sys.argv[3])

	

	csvDir = os.path.dirname(csvFolder)
	datDir = os.path.dirname(datFolder)
	if not os.path.exists(csvDir):
		os.makedirs(csvDir)
	if not os.path.exists(datDir):
		os.makedirs(datDir)
	num_demand_files = 3
	#print csv first
	csvFilePattern = 'demand_MY_'
	datFilePattern = 'demand_MY_'
	for i in xrange(1,num_demand_files+1):
		csvFN = csvFolder + csvFilePattern + '%02d' %i + '.csv'
		tot_demand = READ_DEMAND_SAVE_PERT_CSV(infn, csvFN)
		print 'csv#%2d ' %i + 'generated, total trips: ' + str(tot_demand)

	for i in xrange(1,num_demand_files+1):
		csvFN = csvFolder + csvFilePattern + '%02d' %i + '.csv'
		datFN = datFolder + datFilePattern + '%02d' %i + '.dat'
		CSV_TO_DEMAND_DAT(csvFN, datFN, demandFactor)
	#outfn = '/home/dynamit/20151224_Haizheng/closeloop_demand_gen/demand_perturb.csv'


            
