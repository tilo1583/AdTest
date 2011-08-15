source = open("C:/Users/Sadish/AdTest/assets/jokes.sql")
dest = open("C:/Users/Sadish/AdTest/assets/jokes_temp.sql" ,"w")
strlen_list = []
cnt=0
while 1:    
    line = source.readline()
    if not line: break
    strlen_list.append(len(line))
    cnt=cnt+1
strlen_list.sort()
#print strlen_list
cnt_above1k = 0
source.close()

for num in strlen_list:
    if num > 1000:
        cnt_above1k += 1
        
source = open("C:/Users/Sadish/AdTest/assets/jokes.sql")
while 1:    
    line = source.readline()
    if len(line) < 1000:
        dest.write(line)
print "line count:"+str(cnt);
print "list count:"+str(len(strlen_list));
print cnt_above1k
source.close()
dest.close()
