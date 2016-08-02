if [ "$1" == "" ]
then
    echo "Usage: "
    echo "    $0 target_folder"
    exit
fi
cp output/sen_flw_Est* $1/DM_FOLDER
cp output/sen_flw_Pred* $1/DM_FOLDER
cp EOD.txt $1
cp algoParams_EKF.mat $1
cp dtaparam.dat $1
