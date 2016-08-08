

markerWidth=2;
markerSize=9;
lineWidth=3;
degree=4;
B=importdata('result.mat');
A=zeros(4,10);
A(1,:)=B(1:10);
A(2,:)=B(11:20);
A(3,:)=B(21:30);
A(4,:)=B(31:40);
X=1:10;

p=polyfit(X,A(1,:),degree);
q=polyfit(X,A(2,:),degree);
s=polyfit(X,A(3,:),degree);
r=polyfit(X,A(4,:),degree);

figure
plot(X,A(1,:),'k*','markersize', markerSize,'LineWidth',markerWidth);

hold on
plot(X,A(2,:),'b>','markersize', markerSize,'LineWidth',markerWidth);
plot(X,A(3,:),'ro','markersize', markerSize,'LineWidth',markerWidth);
plot(X,A(4,:),'ms','markersize', markerSize,'LineWidth',markerWidth);
x=1:0.0001:10;
h1=plot(x,polyval(p,x),'k','LineWidth',lineWidth);
h2=plot(x,polyval(q,x),'b','LineWidth',lineWidth);
h3=plot(x,polyval(s,x),'r','LineWidth',lineWidth);
h4=plot(x,polyval(r,x),'m','LineWidth',lineWidth);
legend([h1,h2,h3,h4],{'FHOD','LEOD','SMA','EMA'});
