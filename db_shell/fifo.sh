# Once in either Terminal
mkfifo A B

# In first Terminal
( echo CommandA; sleep 3; echo done > A ) &

# In second Terminal
( echo CommandB; sleep 8; echo done > B ) &

# In third Terminal
read < A; read < B; echo Rest
