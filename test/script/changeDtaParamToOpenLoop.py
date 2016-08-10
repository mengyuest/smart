import os,sys
def SHIFT_PARAM_TO_OPEN_LOOP(path):
	tempPath=path+'.temp'
	with open(path,'rb') as infile:
		with open(tempPath,'wb') as outfile:
			for line in infile.readlines():
				if line.startswith('MaxEstIter'):
					row=line.split('=')
					line = row[0]+'=  1\n'
				if line.startswith('EnableOnlineCalibration'):
					row=line.split('=')
					line = row[0]+'= 0\n'
				outfile.write(line)
	
	os.rename(tempPath,path)

if __name__ == '__main__':
	if len(sys.argv) !=2:
		print "Usage: python changeDtaParamToOpenLoop.py dtaparam_path"
		exit()
	SHIFT_PARAM_TO_OPEN_LOOP(sys.argv[1])
					 
