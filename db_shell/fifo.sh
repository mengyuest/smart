# Once in either Terminal
rm A
rm B
mkfifo A B

# In first Terminal
( gnome-terminal -e "bash -c \" echo process A;read; exit;exec bash\"";echo done >A) &

# In second Terminal
( gnome-terminal -e "bash -c \" echo process B;read;exit;exec bash\""; echo done >B) &

# In third Terminal
read < A; read <B; ls

rm A
rm B
