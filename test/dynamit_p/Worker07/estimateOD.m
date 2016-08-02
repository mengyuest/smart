%    dtaString command_line = matlab_script_path + matlab_script_name_no_ext
%        + " " + sensor_filename + " " + pastveh_filename + " " + updatedOD_filename
%        + " " + assign_mat_filename + " " + varcov_filename + " " + estimatedOD_filename
%        + " " + objfunc_filename;

function ret_value = estimateOD(sensor_filename, pastveh_filename, ...
                                updatedOD_filename, assign_mat_filename, ...
                                varcov_filename, estimatedOD_filename, ...
                                objfunc_filename)

% load observed sensor counts
%counts = load('./temp/counts[05:00:00,05:15:00]2.dat');
counts = load(sensor_filename);

% load the past vehicles
%pastveh = load('./temp/pastveh[05:00:00,05:15:00]2.dat');
pastveh = load(pastveh_filename);

% load updated OD flows (used as historical)
%updatedOD = load('./temp/updatedOD[05:00:00,05:15:00]2.dat');
updatedOD = load(updatedOD_filename);

% load assignment matrices
%assign = load('./temp/assign[05:00:00,05:15:00]2.dat');
assign = load(assign_mat_filename);

% load variance vector
%varcov = load('./varcov.dat');
varcov = load(varcov_filename);

disp('       loading complete');

% compute net counts-past vehicles
netCounts = counts - pastveh;

% compute Ynet vectors
Ynet = [ netCounts ; updatedOD ];

% assume all vectors are column vectors, otherwise need to 'var=var(:);'
varcov = varcov(:);

nL = size(counts, 1); % nP = size(pastveh, 1); % check nP == nL 

nOD = size(updatedOD, 1);
nAll=nL+nOD; % assert(nAll <= size(sqrtvarcov,1) )
disp(['# of sensors = ', int2str(nL), ', # of ODs = ', int2str(nOD)]);

% weight the assignment matrix and counts
sqrtvarcov = sqrt(varcov(1:nAll));
diag_sqrtvarcov=sparse((1:nAll), (1:nAll), sqrtvarcov);

% Sparsify the assignment matrices
assign = sparse(assign);
assign = diag_sqrtvarcov(1:nL, 1:nL)*assign;

% weight the historical OD flows
Ynet = diag_sqrtvarcov * Ynet;

% create augmented H matrix
%Iod = eye(nOD);
Iod = diag_sqrtvarcov(nL+1:nL+nOD, nL+1:nL+nOD);
H = [assign;Iod];
lowerbound = zeros(nOD,1);

disp('        starting GLS ');
[Xhat, residualNorm, dummy, exitflag, output] = lsqlin(H, Ynet, [], [], [], [], lowerbound, []);
Xhat = floor( Xhat+0.5 ); %change to integers
% save estimates
%dlmwrite('./temp/estimatedOD_matlab[05:00:00,05:15:00]2.dat', Xhat, ' ');
dlmwrite(estimatedOD_filename, Xhat, ' ');

%log_idx = 2
%dlmwrite('./temp/objfunc.log', [log_idx, residualNorm], '-append');
dlmwrite(objfunc_filename, residualNorm, '\n');

residualNorm 
exitflag 
output 
% exit
ret_value = exitflag;