%script to be run in dynamit that predicts duration for the 
% samples and outputs an incident.dat file in the correct format.
load incident_gp_model
load dynamit_samples

[Ef, Varf] = gp_pred(gp,x,y,X_test);
out = zeros(length(Ef),4);
for i = 1:length(Ef)
    out(i,1) = dMitId_test(i);
	out(i,4) = 1-X_test(i,27);
	out(i,2) = start_time(i);
	out(i,3) = start_time(i) + (round(exp(Ef(i)))*60);
end

%out = num2str(out);
%save incident.dat out -ascii
filename = fopen('incident.dat','w');
fprintf(filename,'{%d, %d, %d, %f}\n',out');
fclose(filename);
