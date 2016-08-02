% ------------------------------------------------------------------------------------------------------------------------
% Online calibration main function, designed to compile to executable via
% mcc and invoked by DynaMIT indirectly.
% Last update: 30 Dec 2014
% Author: smart
%
%-------------------------------------------------------------------------------------------------------------------------
function status = onlineCalib(fn_meta, ...
                              fn_hist_od, ...
                              fn_sugg_od, ...
                              fn_est_od, ...
                              fn_hist_sp, ...
                              fn_sugg_sp, ...
                              fn_est_sp, ...
                              fn_sensors)

ext = '.dat';

disp('Reading input files...');
addpath(genpath([pwd '/supplementary_calibration_functions'])); %add path to file
% read meta 
full_fn_meta = [fn_meta ext];
data = dlmread(full_fn_meta, ' ', [0 0 2 5]);
interval = data(1:2,:);
meta = data(3,:);
arDegree = meta(1);
%-------------------------------------
fid = fopen(full_fn_meta,'r');
linesToSkip = 3;
for ii = 1:linesToSkip
    fgetl(fid);
end
drDir =  fgetl(fid);
%-------------------------------------
%drDir = data(4,:);
if (arDegree < 0)
	% flag error
	status = -1;
	return
end

% load current historical od
full_fn_hist_od = [fn_hist_od ext];
data = dlmread(full_fn_hist_od);
odIds = data(:,1:2)';
odHist = data(:,3)';
numODs = size(odIds,2);
odSugg = 0; %TODO: integrate sugguested OD from SA 

% load historical od and estimated od for previous intervals
prevHistODs = zeros(arDegree, numODs);
prevEstODs = zeros(arDegree, numODs);
for i=1:arDegree
	full_fn_hist_od = [fn_hist_od '_t_minus_' int2str(i) ext];
	data = dlmread(	full_fn_hist_od );
	prevHistODs(i,:) = data(:,3)';

	full_fn_est_od = [fn_est_od '_t_minus_' int2str(i) ext];
	data = dlmread(	full_fn_est_od );
	prevEstODs(i,:) = data(:,3)';
end

% load historical supply params and estimated supply param for previous intervals
full_fn_hist_sp = [fn_hist_sp ext];
data = dlmread(full_fn_hist_sp);
segIds = data(:,1)';
supplyParamsHist = data(:,2:end);

full_fn_sugg_sp = [fn_sugg_sp ext];
%data = dlmread(full_fn_sugg_sp);
%supplyParamsSugg = data;
supplyParamsSugg = [];

numSegs = size(segIds,2);
numParamTypes = size(supplyParamsHist,2);
prevSupplyParamsEst = zeros(numSegs, numParamTypes, arDegree);

for i=1:arDegree
	full_fn_est_sp = [fn_est_sp '_t_minus_' int2str(i) ext];
	data = dlmread(full_fn_est_sp);
	prevSupplyParamsEst(:,:,i) = data(:,2:end);
end

% load real-time sensor readings
fn_cur_sensors = [fn_sensors ext];
data = dlmread(fn_cur_sensors);
sensorReadings = data(:,1:2)';


% try to find optimised value for current state
disp('Start calibrating...');
[estOD, estSupplyParams] = OC_calib_impl(interval, ...
                                        drDir, ...
                                        meta, ...
                                        odIds, ...                                             
                                        odHist, ...
                                        prevHistODs, ...
                                        prevEstODs, ...
                                        segIds, ...
                                        supplyParamsHist, ...
                                        supplyParamsSugg, ...
                                        prevSupplyParamsEst, ...
                                        sensorReadings);
                                
% save estimated result to file
disp('Writing results to file...');
full_fn_result_od = [fn_est_od ext];
dlmwrite(full_fn_result_od, estOD', 'delimiter', ' ');
full_fn_result_sp = [fn_est_sp ext];
dlmwrite(full_fn_result_sp, [segIds' estSupplyParams], 'delimiter', ' ');

status = 0;
end
