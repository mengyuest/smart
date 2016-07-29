# Using bash to kill the previous postgres process

port=5432
if [ "$1" == "" ]
then
	echo "using default port 5432"
else 
	re='^[[:digit:]]{1,5}$'
	echo $re
	if  [[ $1 =~ $re ]];then
		port=$1
		echo "setting port to $port"
	else
		echo "wrong valid of port numbers"
		return
	fi
fi

str=$(sudo lsof -i:$port -F p)
if [ "$str" == "" ]
then
	echo "no such process listening to port:$port"
else
	processid=${str: 1}
	sudo kill $processid
	echo "killed process $processid"
fi
echo "finished~"

